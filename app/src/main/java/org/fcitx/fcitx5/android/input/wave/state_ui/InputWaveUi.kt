package org.fcitx.fcitx5.android.input.wave.state_ui

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import org.fcitx.fcitx5.android.R
import org.fcitx.fcitx5.android.data.theme.Theme
import org.fcitx.fcitx5.android.input.wave.WaveInputUiState
import splitties.dimensions.dp
import splitties.views.dsl.core.*

class InputWaveUi(
    override val ctx: Context,
    private val theme: Theme
): Ui {

    companion object {
        private const val TAG = "InputWaveUi"
    }

    val inputIconIv = imageView {
        setImageResource(R.drawable.ic_wave_input)
        scaleType = ImageView.ScaleType.FIT_XY
    }

    private val tipsTv = textView {
        setTextColor(Color.BLACK)
        setText(R.string.before_input_wave_tips)
        textSize = 15f
        typeface = Typeface.defaultFromStyle(Typeface.BOLD)
    }

    private val errorTv = textView {
        setTextColor(Color.RED)
        text = "识别出错"
        textSize = 12f
        typeface = Typeface.defaultFromStyle(Typeface.BOLD)
        visibility = View.GONE
    }

    @SuppressLint("ClickableViewAccessibility")
    override val root: View = verticalLayout {
        gravity = Gravity.CENTER
        add(inputIconIv, lParams(dp(150), dp(150)))
        add(tipsTv, lParams(wrapContent, wrapContent) {
            setMargins(0, dp(17), 0, 0)
        })
        add(errorTv, lParams(wrapContent, wrapContent) {
            setMargins(0, dp(7), 0, 0)
        })
    }

    fun handleWaveInputUiState(state: WaveInputUiState) {
        when(state) {
            WaveInputUiState.BeforeInputWave,
            is WaveInputUiState.FailRecognizedWave -> {
                tipsTv.setText(R.string.before_input_wave_tips)
                inputIconIv.alpha = 1f
                inputIconIv.setImageResource(R.drawable.ic_wave_input)
                errorTv.visibility = if (state is WaveInputUiState.FailRecognizedWave) {
                    errorTv.text = state.error
                    View.VISIBLE
                }else {
                    View.GONE
                }
            }
            WaveInputUiState.InputtingWave -> {
                tipsTv.text = ""
                inputIconIv.alpha = 0.6f
                errorTv.visibility = View.GONE
            }
            WaveInputUiState.RecognizingWave -> {
                tipsTv.setText(R.string.recognize_input_wave_tips)
                inputIconIv.alpha = 1f
                inputIconIv.setImageResource(R.drawable.ic_wave_recognize)
                errorTv.visibility = View.GONE
            }
            else -> {/*do nothing*/}
        }
    }
}