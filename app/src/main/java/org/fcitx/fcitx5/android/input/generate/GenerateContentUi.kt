package org.fcitx.fcitx5.android.input.generate

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.view.Gravity
import org.fcitx.fcitx5.android.R
import org.fcitx.fcitx5.android.data.theme.Theme
import splitties.dimensions.dp
import splitties.views.dsl.constraintlayout.*
import splitties.views.dsl.core.*
import splitties.views.setPaddingDp

class GenerateContentUi(
    val content: String,
    override val ctx: Context,
    private val theme: Theme
): Ui {

    private val contentTv = textView {
        includeFontPadding = false
        setTextColor(Color.BLACK)
        textSize = 14f
        text = content
    }

    private val sendButton = textView {
        includeFontPadding = false
        setTextColor(Color.BLACK)
        textSize = 14f
        typeface = Typeface.defaultFromStyle(Typeface.BOLD)
        text = "发送"
        setBackgroundResource(R.drawable.bg_content_send_bt)
        gravity = Gravity.CENTER
        setPaddingDp(6, 0, 6, 0)
    }

    override val root = constraintLayout {
        add(sendButton, lParams(wrapContent, matchParent) {
            endOfParent()
        })
        add(contentTv, lParams(wrapContent, wrapContent) {
            startOfParent(dp(10))
            before(sendButton, dp(10))
            topOfParent(dp(10))
            bottomOfParent(dp(10))
            horizontalBias = 0f
        })
        setBackgroundResource(R.drawable.bg_content_ui)
    }

}