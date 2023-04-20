package org.fcitx.fcitx5.android.input.generate.model

import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import org.fcitx.fcitx5.android.input.generate.data.IGenerateContentResponse

class GenerateContentSource {

    sealed class State {
        class Loading(val message: String): State()

        class Done(val message: String, val response: IGenerateContentResponse): State()

        class ApiError(val error: String): State()
    }

    private val repo by lazy { GenerateRepository() }

    val sourceState = MutableSharedFlow<State>(1, 1, BufferOverflow.DROP_LATEST)

    /**
     * 时间问题，先限制一页数据
     */
    suspend fun loadAnswersFor(
        message: String,
        fraudPrompt: String,
        advicesPrompt: String
    ) {
        sourceState.tryEmit(State.Loading(message))
        val newState = try {
            val answers = repo.fetchGeneratedContent(message,fraudPrompt,advicesPrompt)
            if (answers == null) {
                State.ApiError("接口发生错误")
            }else {
                State.Done(message, answers)
            }
        }catch (e: Exception) {
            State.ApiError( "执行过程中发生错误: ${e.message}")
        }
        sourceState.tryEmit(newState)
    }
}