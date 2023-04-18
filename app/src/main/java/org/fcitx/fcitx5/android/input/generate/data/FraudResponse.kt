package org.fcitx.fcitx5.android.input.generate.data

import com.google.gson.annotations.SerializedName

data class FraudResponse(
    @SerializedName("is_scam") val isScam: Boolean,
    @SerializedName("percentage") val percentage: String,
    @SerializedName("reason") val reason: String,
    @SerializedName("advices") val advices: List<String>
): IGenerateContentResponse