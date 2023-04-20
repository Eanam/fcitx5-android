package org.fcitx.fcitx5.android.input.wave.data

import com.google.gson.annotations.SerializedName

data class STTResponse(
    @SerializedName("code") val code: Int,
    @SerializedName("message") val message: String
)
