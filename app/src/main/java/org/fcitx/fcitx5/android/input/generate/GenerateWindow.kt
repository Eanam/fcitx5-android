package org.fcitx.fcitx5.android.input.generate

import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.fcitx.fcitx5.android.data.clipboard.ClipboardManager
import org.fcitx.fcitx5.android.input.FcitxInputMethodService
import org.fcitx.fcitx5.android.input.dependency.inputMethodService
import org.fcitx.fcitx5.android.input.dependency.theme
import org.fcitx.fcitx5.android.input.generate.data.AdviceResponse
import org.fcitx.fcitx5.android.input.generate.data.FraudResponse
import org.fcitx.fcitx5.android.input.generate.model.ADVICE_FOR_OLD_MAN_PROMPT
import org.fcitx.fcitx5.android.input.generate.model.FRAUD_PROMPT
import org.fcitx.fcitx5.android.input.generate.model.GenerateContentSource
import org.fcitx.fcitx5.android.input.wm.InputWindow
import timber.log.Timber

class GenerateWindow: InputWindow.ExtendedInputWindow<GenerateWindow>() {

    companion object {
        private const val TAG = "GenerateWindow"
    }

    private val service: FcitxInputMethodService by manager.inputMethodService()
    private val theme by manager.theme()
    private val ui by lazy {
        GenerateUi(context, theme) { content: String ->
            service.commitText(content)
        }
    }
    private var clipboardContent = ClipboardManager.lastEntry?.text ?: ""
    private var reqJob: Job? = null
    private var listenJob: Job? = null
    private val source by lazy { GenerateContentSource() }

    override val title: String = "复制回答"

    override fun onCreateView() = ui.root

    override fun onAttached() {
        val initState = if (clipboardContent.isBlank()) {
            GenerateUiState.NotCopiedYet
        }else {
            GenerateUiState.HasCopiedContent(clipboardContent)
        }

        ui.switchUiByState(initState)

        if (initState is GenerateUiState.HasCopiedContent) {
            //先监听状态变化，避免错过
            listenJob = service.lifecycleScope.launch(Dispatchers.Main) {
                source.sourceState.collectLatest {
                    onGenerateContentSourceUpdated(it)
                }
            }
            reqJob = service.lifecycleScope.launch(Dispatchers.IO) {
                source.loadAnswersFor(initState.copiedContent, FRAUD_PROMPT, ADVICE_FOR_OLD_MAN_PROMPT)
            }
        }
    }

    override fun onDetached() {
        reqJob?.cancel()
        reqJob = null
        listenJob?.cancel()
        listenJob = null
    }

    private fun onGenerateContentSourceUpdated(state: GenerateContentSource.State) {
        Timber.tag(TAG).d("onGenerateContentSourceUpdated -> ${state.javaClass.simpleName}")
        var uiState: GenerateUiState? = null
        when(state) {
            is GenerateContentSource.State.Loading -> {
                uiState = GenerateUiState.LoadingAnswers(state.message)
            }
            is GenerateContentSource.State.Done -> {
                when(val response = state.response) {
                    is FraudResponse -> {
                        uiState = GenerateUiState.FraudMessageConfirmed(state.message, response)
                    }
                    is AdviceResponse -> {
                        uiState = GenerateUiState.AdvicesConfirmed(state.message, response)
                    }
                }
            }
            is GenerateContentSource.State.ApiError -> {
                uiState = GenerateUiState.ApiError(state.error)
            }
        }

        uiState?.let {
            ui.switchUiByState(it)
        }
    }
}