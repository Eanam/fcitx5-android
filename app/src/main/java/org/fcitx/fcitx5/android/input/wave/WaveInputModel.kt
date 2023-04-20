package org.fcitx.fcitx5.android.input.wave

import com.github.squti.androidwaverecorder.RecorderState
import com.github.squti.androidwaverecorder.WaveRecorder
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.logging.HttpLoggingInterceptor
import org.fcitx.fcitx5.android.input.wave.service.STTService
import org.fcitx.fcitx5.android.utils.appContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber
import java.io.File

class WaveInputModel(initUiState: WaveInputUiState) {

    companion object {
        private const val TAG = "WaveInputModel"
    }

    val uiState = MutableSharedFlow<WaveInputUiState>(0, 1, BufferOverflow.DROP_LATEST)

    private var filePath : String? = null
    private var waveRecorder: WaveRecorder? = null
    private val waveRecorderListener by lazy {
        { recordState: RecorderState ->
            when (recordState) {
                RecorderState.RECORDING -> updateUiState(WaveInputUiState.InputtingWave)
                RecorderState.STOP -> updateUiState(WaveInputUiState.DoneInputWave(filePath))
                else -> {/*do nothing*/}
            }
        }
    }
    private val sttService by lazy {
        val okHttpClient =
            OkHttpClient
                .Builder()
                .addInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY })
                .build()

        Retrofit
            .Builder()
            .client(okHttpClient)
            .baseUrl("https://buddha-stt.roudan.io/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(STTService::class.java)
    }
    private val sttSecretId = "muimui"
    private val sttSecretKey = "muimui"

    fun startRecording() {
        Timber.tag(TAG).d("call startRecording")
        filePath = createCurrentInputWaveFile() ?: kotlin.run {
            Timber.tag(TAG).d("stop startRecording when create wav file")
            return
        }

        waveRecorder = WaveRecorder(filePath!!).apply {
            onStateChangeListener = waveRecorderListener
        }
        waveRecorder!!.startRecording()
    }

    fun endRecording() {
        Timber.tag(TAG).d("call endRecording")
        waveRecorder?.stopRecording()
    }

    /**
     * 请求语音转换接口
     */
    suspend fun requestSTT(paramFilePath: String?) {
        Timber.tag(TAG).d("call requestSTT")
        val wavFile = paramFilePath?.let { File(it) }
        if (paramFilePath == null || wavFile?.exists() != true) {
            uiState.tryEmit(WaveInputUiState.BeforeInputWave)
            return
        }

        updateUiState(WaveInputUiState.RecognizingWave)

        val requestBody =
            MultipartBody
                .Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart(
                    "file",
                    wavFile.name,
                    wavFile.asRequestBody("application/octet-stream".toMediaType())
                )
                .addFormDataPart("tencentSecretId", sttSecretId)
                .addFormDataPart("tencentSecretKey", sttSecretKey)
                .build()

        Timber.tag(TAG).d("start requestSTT requestBody -> ${requestBody.type}")
        val response = try {
            sttService.requestSTT(requestBody)
        }catch (e: Exception) {
            Timber.tag(TAG).d("requestSTT error -> ${e.message}")
            updateUiState(WaveInputUiState.FailRecognizedWave(e.message ?: ""))
            return
        }

        Timber.tag(TAG).d("requestSTT response -> $response")

        if (response.code != 0) {
            updateUiState(WaveInputUiState.FailRecognizedWave(response.message))
            return
        }

        updateUiState(WaveInputUiState.DoneRecognizedWave(response.message))
    }

    private fun getInputWaveDirPath(): String? {
        val dirPath = appContext.externalCacheDir?.absolutePath?.let {
            "$it/input_waves"
        } ?: return null

        val dir = File(dirPath)
        if (dir.exists()) return dirPath
        dir.mkdirs()
        return dirPath
    }

    private fun createCurrentInputWaveFile(): String? {
        return getInputWaveDirPath()?.let {
            "$it/${System.currentTimeMillis()}.wav"
        }?.let {
            val file = File(it)
            if (!file.exists()) file.createNewFile()
            file.absolutePath
        }
    }

    private fun updateUiState(state: WaveInputUiState) {
        uiState.tryEmit(state)
    }
}