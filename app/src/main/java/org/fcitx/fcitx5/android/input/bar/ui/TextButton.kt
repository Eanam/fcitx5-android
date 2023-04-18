package org.fcitx.fcitx5.android.input.bar.ui

import android.content.Context
import android.graphics.Typeface
import androidx.annotation.ColorInt
import org.fcitx.fcitx5.android.data.prefs.AppPrefs
import org.fcitx.fcitx5.android.data.theme.Theme
import org.fcitx.fcitx5.android.input.keyboard.CustomGestureView
import org.fcitx.fcitx5.android.utils.borderlessRippleDrawable
import org.fcitx.fcitx5.android.utils.circlePressHighlightDrawable
import splitties.dimensions.dp
import splitties.views.dsl.core.add
import splitties.views.dsl.core.lParams
import splitties.views.dsl.core.textView
import splitties.views.dsl.core.wrapContent
import splitties.views.gravityCenter
import splitties.views.gravityVerticalCenter

class TextButton(context: Context) : CustomGestureView(context) {

    companion object {
        val disableAnimation by AppPrefs.getInstance().advanced.disableAnimation
    }

    constructor(context: Context, text: String, theme: Theme) : this(context) {
        configText(text, theme.altKeyTextColor)
        setPressHighlightColor(theme.keyPressHighlightColor)
    }

    private val titleText = textView {
        typeface = Typeface.defaultFromStyle(Typeface.BOLD)
        gravity = gravityVerticalCenter
        textSize = 16f
    }

    init {
        add(titleText, lParams(wrapContent, wrapContent, gravityCenter))
    }

    private fun configText(text: String, textColor: Int) {
        titleText.text = text
        titleText.setTextColor(textColor)
    }

    private fun setPressHighlightColor(@ColorInt color: Int) {
        background = if (disableAnimation) {
            circlePressHighlightDrawable(color)
        } else {
            borderlessRippleDrawable(color, dp(20))
        }
    }
}
