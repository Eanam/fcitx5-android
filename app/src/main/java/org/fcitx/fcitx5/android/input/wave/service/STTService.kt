package org.fcitx.fcitx5.android.input.wave.service

import okhttp3.RequestBody
import org.fcitx.fcitx5.android.input.wave.data.STTResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface STTService {

    @POST("api/stt")
    suspend fun requestSTT(@Body body: RequestBody): STTResponse

}