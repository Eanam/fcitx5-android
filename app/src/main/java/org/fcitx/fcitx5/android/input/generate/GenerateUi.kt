package org.fcitx.fcitx5.android.input.generate

import android.content.Context
import android.graphics.Color
import org.fcitx.fcitx5.android.data.theme.Theme
import splitties.dimensions.dp
import splitties.views.dsl.recyclerview.recyclerView
import android.widget.ViewAnimator
import splitties.views.dsl.core.*

class GenerateUi(override val ctx: Context, private val theme: Theme): Ui {

    val recyclerView = recyclerView {
        addItemDecoration(MessageItemDecoration(dp(4)))
        setBackgroundColor(Color.GREEN) //TODO: TEST
    }

    override val root = view(::ViewAnimator) {
        add(recyclerView, lParams(matchParent, matchParent))
    }
}