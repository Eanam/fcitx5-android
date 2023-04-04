package org.fcitx.fcitx5.android.input.generate.data

data class GenerateContentEntity(
    val isFromUser: Boolean = false,    //used to decide ui style
    val content: String
)