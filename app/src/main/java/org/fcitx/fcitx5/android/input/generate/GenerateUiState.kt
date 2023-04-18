package org.fcitx.fcitx5.android.input.generate

import org.fcitx.fcitx5.android.input.generate.data.AdviceResponse
import org.fcitx.fcitx5.android.input.generate.data.FraudResponse

sealed class GenerateUiState {
    //暂未复制内容
    object NotCopiedYet: GenerateUiState()

    class HasCopiedContent(val copiedContent: String): GenerateUiState()

    class LoadingAnswers(val copiedContent: String): GenerateUiState()

    class FraudMessageConfirmed(val copiedContent: String, val response: FraudResponse): GenerateUiState()

    class AdvicesConfirmed(val copiedContent: String, val response: AdviceResponse): GenerateUiState()

    class ApiError(val error: String): GenerateUiState()
}