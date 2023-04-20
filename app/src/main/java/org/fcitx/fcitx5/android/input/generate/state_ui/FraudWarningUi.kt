package org.fcitx.fcitx5.android.input.generate.state_ui

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.widget.ImageView
import androidx.core.widget.NestedScrollView
import org.fcitx.fcitx5.android.R
import org.fcitx.fcitx5.android.data.theme.Theme
import org.fcitx.fcitx5.android.input.generate.GenerateContentUi
import org.fcitx.fcitx5.android.input.generate.data.FraudResponse
import splitties.dimensions.dp
import splitties.views.dsl.constraintlayout.*
import splitties.views.dsl.core.*
import splitties.views.imageResource

class FraudWarningUi(
    override val ctx: Context,
    private val theme: Theme,
    private val commitContentCallback: (String) -> Unit,
): Ui {

    private val titleTv = textView {
        text = "温馨提示，此条消息需谨慎"
        setTextColor(Color.WHITE)
        textSize = 16f
        includeFontPadding = false
    }

    private val iconIv = imageView {
        imageResource = R.drawable.ic_generate_warning
        scaleType = ImageView.ScaleType.FIT_XY
    }

    private val crimeRatioTv = textView {
        includeFontPadding = false
        textSize = 12f
        setTextColor(Color.WHITE)
    }
    private val waringArea = constraintLayout {
        add(iconIv, lParams(dp(38), dp(38)) {
            startOfParent()
            topOfParent()
            bottomOfParent()
        })
        add(crimeRatioTv, lParams(0, wrapContent) {
            after(iconIv, dp(11))
            topOfParent()
            bottomOfParent()
            endOfParent()
        })
    }

    private val innerLayout = verticalLayout {}

    override val root = view(::NestedScrollView) {
        add(innerLayout, lParams(matchParent, matchParent))
        setBackgroundColor(Color.RED)
    }

    @SuppressLint("SetTextI18n")
    fun updateUiContent(response: FraudResponse) {
        crimeRatioTv.text = "涉嫌诈骗内容(${response.percentage})\n${response.reason}"
        innerLayout.apply {
            removeAllViews()
            add(titleTv, lParams(wrapContent, wrapContent) {
                setMargins(dp(20), dp(20), 0, 0)
            })
            add(waringArea, lParams(matchParent, wrapContent) {
                setMargins(dp(18), dp(12), dp(18), dp(4))
            })
            response.replies.forEach {
                add(
                    GenerateContentUi(it, ctx, theme, commitContentCallback).root,
                    lParams(matchParent, wrapContent) {
                        setMargins(dp(13), dp(12), dp(13), 0)
                    }
                )
            }
        }
    }
}