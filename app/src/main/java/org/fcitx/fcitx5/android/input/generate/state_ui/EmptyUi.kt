package org.fcitx.fcitx5.android.input.generate.state_ui

import android.content.Context
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.CHAIN_PACKED
import org.fcitx.fcitx5.android.R
import org.fcitx.fcitx5.android.data.theme.Theme
import splitties.dimensions.dp
import splitties.views.dsl.constraintlayout.*
import splitties.views.dsl.core.*
import splitties.views.imageResource

class EmptyUi(override val ctx: Context, private val theme: Theme) : Ui {

    private val icon = imageView {
        imageResource = R.drawable.ic_baseline_content_paste_24
        colorFilter = PorterDuffColorFilter(theme.altKeyTextColor, PorterDuff.Mode.SRC_IN)
    }

    private val instructionText = textView {
        setText(R.string.instruction_copy)
        setTextColor(theme.keyTextColor)
    }

    override val root = constraintLayout {
        add(icon, lParams(dp(90), dp(90)) {
            topOfParent(dp(24))
            startOfParent()
            endOfParent()
            above(instructionText)
            verticalChainStyle = CHAIN_PACKED
        })
        add(instructionText, lParams(wrapContent, wrapContent) {
            below(icon, dp(16))
            startOfParent()
            endOfParent()
            bottomOfParent()
        })
    }

}