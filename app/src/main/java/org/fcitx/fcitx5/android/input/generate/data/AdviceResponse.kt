package org.fcitx.fcitx5.android.input.generate.data

import com.google.gson.annotations.SerializedName

data class AdviceResponse(
    @SerializedName("advices") val advices: List<String>
): IGenerateContentResponse