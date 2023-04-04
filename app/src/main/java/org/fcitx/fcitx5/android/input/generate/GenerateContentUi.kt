package org.fcitx.fcitx5.android.input.generate

import android.content.Context
import org.fcitx.fcitx5.android.data.theme.Theme
import android.graphics.Color
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import org.fcitx.fcitx5.android.R
import splitties.dimensions.dp
import splitties.views.dsl.constraintlayout.*
import splitties.views.dsl.core.*
import splitties.views.setPaddingDp

class GenerateContentUi(override val ctx: Context, private val theme: Theme): Ui {

    val text = textView {
        maxLines = 2
        textSize = 12f
        setPaddingDp(10, 5, 10, 5)
        setTextColor(Color.WHITE)
        background = ContextCompat.getDrawable(ctx, R.drawable.bg_generate_content_item)
    }

    private val wrapper = constraintLayout {
        add(text, lParams(wrapContent, wrapContent) {
            topOfParent()
            bottomOfParent()
            startOfParent(dp(2))
            endOfParent(dp(2))
        })
    }

    override val root = view(::CardView) {
        layoutParams = lParams(matchParent, wrapContent)
        setBackgroundColor(Color.TRANSPARENT)
        cardElevation = 0f
        add(wrapper, lParams(matchParent, wrapContent))
    }

}