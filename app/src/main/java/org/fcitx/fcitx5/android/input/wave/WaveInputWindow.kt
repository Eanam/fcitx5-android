package org.fcitx.fcitx5.android.input.wave

import android.annotation.SuppressLint
import android.view.MotionEvent
import android.view.View
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.fcitx.fcitx5.android.input.FcitxInputMethodService
import org.fcitx.fcitx5.android.input.dependency.inputMethodService
import org.fcitx.fcitx5.android.input.dependency.theme
import org.fcitx.fcitx5.android.input.generate.GenerateUiState
import org.fcitx.fcitx5.android.input.generate.data.AdviceResponse
import org.fcitx.fcitx5.android.input.generate.data.FraudResponse
import org.fcitx.fcitx5.android.input.generate.model.GenerateContentSource
import org.fcitx.fcitx5.android.input.wave.prompt.ADVICE_FOR_OLD_MAN_PROMPT
import org.fcitx.fcitx5.android.input.wave.prompt.FRAUD_PROMPT
import org.fcitx.fcitx5.android.input.wm.InputWindow
import timber.log.Timber

class WaveInputWindow: InputWindow.ExtendedInputWindow<WaveInputWindow>() {

    companion object {
        private const val TAG = "WaveInputWindow"
    }

    override val title: String = "语音输入"

    private val service: FcitxInputMethodService by manager.inputMethodService()
    private val theme by manager.theme()
    private val ui by lazy {
        WaveInputUi(context, theme) { content: String ->
            service.commitText(content)
        }
    }
    private val model by lazy { WaveInputModel(WaveInputUiState.BeforeInputWave) }
    private var uiStateListenJob: Job? = null
    private var recognizeWaveJob: Job? = null

    private val generateSource by lazy { GenerateContentSource() }
    private var generateReqJob: Job? = null
    private var generateListenJob: Job? = null

    override fun onCreateView() = ui.root

    @SuppressLint("ClickableViewAccessibility")
    override fun onAttached() {
        uiStateListenJob = service.lifecycleScope.launch(Dispatchers.Main) {
            model.uiState.collectLatest { handleWaveInputUiState(it) }
        }

        ui.inputWaveUi.inputIconIv.setOnTouchListener(::handleOnTouch)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onDetached() {
        ui.inputWaveUi.inputIconIv.setOnTouchListener(null)
        uiStateListenJob?.cancel()
        uiStateListenJob = null
        recognizeWaveJob?.cancel()
        recognizeWaveJob = null
        generateReqJob?.cancel()
        generateReqJob = null
        generateListenJob?.cancel()
        generateListenJob = null
    }

    private fun handleWaveInputUiState(state: WaveInputUiState) {
        Timber.tag(TAG).d("handleWaveInputUiState newState -> ${state.javaClass.simpleName}")
        when(state) {
            is WaveInputUiState.DoneInputWave -> {
                recognizeWaveJob?.cancel()
                recognizeWaveJob = service.lifecycleScope.launch(Dispatchers.IO) {
                    model.requestSTT(state.filePath)
                }
            }
            is WaveInputUiState.DoneRecognizedWave -> {
                //先监听状态变化，避免错过
                generateListenJob?.cancel()
                generateListenJob = service.lifecycleScope.launch(Dispatchers.Main) {
                    generateSource.sourceState.collectLatest {
                        onGenerateContentSourceUpdated(it)
                    }
                }
                generateReqJob?.cancel()
                generateReqJob = service.lifecycleScope.launch(Dispatchers.IO) {
                    generateSource.loadAnswersFor(
                        state.content,
                        FRAUD_PROMPT,
                        ADVICE_FOR_OLD_MAN_PROMPT
                    )
                }
            }
            else -> {/*do nothing*/}
        }
        ui.handleWaveInputUiState(state)
    }

    private fun handleOnTouch(view: View, event: MotionEvent): Boolean {
        return when(event.action) {
            MotionEvent.ACTION_DOWN -> {
                Timber.tag(TAG).d("inputIconIv onTouch -> DOWN")
                onPressedStart()
                true
            }
            MotionEvent.ACTION_UP -> {
                Timber.tag(TAG).d("inputIconIv onTouch -> UP")
                onPressedEnd()
                true
            }
            else -> false
        }
    }

    private fun onPressedStart() {
        model.startRecording()
    }

    private fun onPressedEnd() {
        model.endRecording()
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
            ui.generateUi.switchUiByState(it)
        }
    }
}