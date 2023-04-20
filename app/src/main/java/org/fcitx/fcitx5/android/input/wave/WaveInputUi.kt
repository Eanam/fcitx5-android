package org.fcitx.fcitx5.android.input.wave

import android.content.Context
import android.widget.ViewAnimator
import org.fcitx.fcitx5.android.data.theme.Theme
import org.fcitx.fcitx5.android.input.generate.GenerateUi
import org.fcitx.fcitx5.android.input.generate.GenerateUiState
import org.fcitx.fcitx5.android.input.wave.state_ui.InputWaveUi
import splitties.views.dsl.core.*

class WaveInputUi(
    override val ctx: Context,
    private val theme: Theme,
    private val commitContentCallback: (String) -> Unit,
): Ui {

    val inputWaveUi by lazy { InputWaveUi(ctx, theme) }
    val generateUi by lazy { GenerateUi(ctx, theme, commitContentCallback) }


    override val root = view(::ViewAnimator) {
        add(inputWaveUi.root, lParams(matchParent, matchParent))
        add(generateUi.root, lParams(matchParent, matchParent))
    }

    fun handleWaveInputUiState(state: WaveInputUiState) {
        when(state) {
            WaveInputUiState.BeforeInputWave,
            WaveInputUiState.InputtingWave,
            WaveInputUiState.RecognizingWave,
            is WaveInputUiState.FailRecognizedWave -> {
                root.displayedChild = 0
                inputWaveUi.handleWaveInputUiState(state)
            }
            is WaveInputUiState.DoneRecognizedWave -> {
                root.displayedChild = 1
                generateUi.switchUiByState(GenerateUiState.HasCopiedContent(state.content))
            }
            else -> {

            }
        }
    }
}