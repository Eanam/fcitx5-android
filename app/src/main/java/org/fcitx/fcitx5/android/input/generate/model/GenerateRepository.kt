package org.fcitx.fcitx5.android.input.generate.model

import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import mobi.appcent.openai.apis.BaseApi
import mobi.appcent.openai.common.UrlConstant
import mobi.appcent.openai.infrastructure.*
import mobi.appcent.openai.models.ChatCompletionRequestMessage
import mobi.appcent.openai.models.CreateChatCompletionRequest
import mobi.appcent.openai.models.CreateChatCompletionResponse
import mobi.appcent.openai.models.CreateChatCompletionResponseChoices
import okhttp3.logging.HttpLoggingInterceptor
import org.fcitx.fcitx5.android.input.generate.data.AdviceResponse
import org.fcitx.fcitx5.android.input.generate.data.FraudResponse
import org.fcitx.fcitx5.android.input.generate.data.IGenerateContentResponse
import timber.log.Timber

class GenerateRepository {

    companion object {
        private const val TAG = "GenerateRepository"
    }

    private val url = "https://api.openai.cztcode.com/v1"
    private val fraudPromptToken = "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"
    private val advicesPromptToken = "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"
    private val fraudApi by lazy {
        MyChatApi().apply {
            initApiClient(MyChatApiClient(baseUrl = url, apiKey = fraudPromptToken))
        }
    }
    private val advicesApi by lazy {
        MyChatApi().apply {
            initApiClient(MyChatApiClient(baseUrl = url, apiKey = advicesPromptToken))
        }
    }

    suspend fun fetchGeneratedContent(
        message: String,
        fraudPrompt: String,
        advicesPrompt: String
    ): IGenerateContentResponse? {
        Timber.tag(TAG).d("fetchGeneratedContent: start -> $message")
        return withContext(Dispatchers.IO) {
            //先判断是否是诈骗短信
            val rawFraudResponse = fraudApi.createMyChatCompletion(
                CreateChatCompletionRequest(
                    model = "gpt-3.5-turbo",
                    messages = arrayOf(
                        ChatCompletionRequestMessage(
                            role = ChatCompletionRequestMessage.Role.USER.value,
                            content = "$fraudPrompt $message"
                        )
                    )
                )
            )
            val fraudAnswers = rawFraudResponse?.choices?.parseToFraudResponse()
            Timber.tag(TAG).d("fetchGeneratedContent: result ->  ${fraudAnswers?.joinToString { it.toString() }}")
            //证明接口有问题
            if (fraudAnswers.isNullOrEmpty()) return@withContext null
            val fraudAnswer = fraudAnswers.firstOrNull { it.isScam }
            if (fraudAnswer != null) return@withContext fraudAnswer

            Timber.tag(TAG).d("This don\'t contains fraud content...")

            //不是诈骗短信，则给老人提示快捷回复
            val rawAdvicesResponse = advicesApi.createMyChatCompletion(
                CreateChatCompletionRequest(
                    model = "gpt-3.5-turbo",
                    messages = arrayOf(
                        ChatCompletionRequestMessage(
                            role = ChatCompletionRequestMessage.Role.USER.value,
                            content = "$advicesPrompt $message"
                        )
                    )
                )
            )

            val advicesAnswers = rawAdvicesResponse?.choices?.parseToAdvicesResponse()
            Timber.tag(TAG).d("fetchGeneratedContent: result ->  ${advicesAnswers?.joinToString { it.toString() }}")
            //证明接口有问题
            if (advicesAnswers.isNullOrEmpty()) return@withContext null
            return@withContext advicesAnswers.firstOrNull()
        }
    }

    private fun List<CreateChatCompletionResponseChoices>.parseToFraudResponse(): List<FraudResponse> {
        return mapNotNull {
            val content = it.message?.content ?: return@mapNotNull null
            Timber.tag(TAG).d("parseToFraudResponse -> $content")
            try {
                Gson().fromJson(content, FraudResponse::class.java)
            }catch (e: Exception) {
                null
            }
        }
    }

    private fun List<CreateChatCompletionResponseChoices>.parseToAdvicesResponse(): List<AdviceResponse> {
        return mapNotNull {
            val content = it.message?.content ?: return@mapNotNull null
            Timber.tag(TAG).d("parseToFraudResponse -> $content")
            try {
                Gson().fromJson(content, AdviceResponse::class.java)
            }catch (e: Exception) {
                Timber.tag(TAG).d("parseToAdvicesResponse error -> ${e.message}")
                null
            }
        }
    }

    class MyChatApiClient(baseUrl: String, apiKey: String?): ApiClient(
        baseUrl =  baseUrl,
        apiKey = apiKey,
        organization = null,
        logLevel = HttpLoggingInterceptor.Level.BODY
    )
    class MyChatApi: BaseApi() {
        suspend fun createMyChatCompletion(body: CreateChatCompletionRequest): CreateChatCompletionResponse? {
            val localVariableBody: CreateChatCompletionRequest = body

            val localVariableConfig = RequestConfig(
                RequestMethod.POST,
                UrlConstant.CHAT_COMPLETION_URL
            )

            val response = apiClient.request<CreateChatCompletionResponse>(
                localVariableConfig,
                localVariableBody
            )

            return when (response.responseType) {
                ResponseType.Success -> (response as Success<*>).data as CreateChatCompletionResponse
                ResponseType.ClientError -> null
                ResponseType.ServerError -> null
            }
        }
    }
}