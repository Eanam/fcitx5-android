package org.fcitx.fcitx5.android.input.bar.ui.idle

import android.content.Context
import com.google.android.flexbox.AlignItems
import com.google.android.flexbox.FlexboxLayout
import com.google.android.flexbox.JustifyContent
import org.fcitx.fcitx5.android.data.theme.Theme
import org.fcitx.fcitx5.android.input.bar.ui.TextButton
import splitties.views.dsl.core.Ui
import splitties.views.dsl.core.view
import splitties.views.dsl.core.wrapContent

class IntelligentBarUi(override val ctx: Context, private val theme: Theme) : Ui {

    override val root = view(::FlexboxLayout) {
        alignItems = AlignItems.CENTER
        justifyContent = JustifyContent.SPACE_AROUND
    }

    private fun toolButton(btText: String) = TextButton(ctx, btText, theme).also {
        root.addView(it, FlexboxLayout.LayoutParams(it.wrapContent, it.wrapContent))
    }

    //新工具栏按钮
    val copyAndGenerateAnswerButton = toolButton("复制回答")
    val inputByHand = toolButton("手写输入")
    val inputByVoice = toolButton("语音输入")
    val inputByShot = toolButton("拍照识别")
}