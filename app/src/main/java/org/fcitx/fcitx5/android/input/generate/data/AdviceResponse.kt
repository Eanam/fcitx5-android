package org.fcitx.fcitx5.android.input.generate.data

import com.google.gson.annotations.SerializedName

data class AdviceResponse(
    @SerializedName("replies") val replies: List<String>? = null,
    @SerializedName("advices") val advices: List<String>? = null
): IGenerateContentResponse {
    fun getContents(): List<String> {
        return replies ?: advices ?: emptyList()
    }
}