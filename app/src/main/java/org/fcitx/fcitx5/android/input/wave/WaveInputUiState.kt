package org.fcitx.fcitx5.android.input.wave

sealed class WaveInputUiState {

    object BeforeInputWave: WaveInputUiState()

    object InputtingWave: WaveInputUiState()

    class DoneInputWave(val filePath: String?): WaveInputUiState()

    object RecognizingWave: WaveInputUiState()

    class DoneRecognizedWave(val content: String): WaveInputUiState()

    class FailRecognizedWave(val error: String): WaveInputUiState()

}