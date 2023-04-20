package org.fcitx.fcitx5.android.input.generate.state_ui

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.widget.ViewAnimator
import androidx.core.widget.NestedScrollView
import org.fcitx.fcitx5.android.R
import org.fcitx.fcitx5.android.data.theme.Theme
import org.fcitx.fcitx5.android.input.generate.GenerateContentUi
import org.fcitx.fcitx5.android.input.generate.GenerateUiState
import splitties.dimensions.dp
import splitties.views.dsl.constraintlayout.*
import splitties.views.dsl.core.*
import splitties.views.padding
import timber.log.Timber

class AdvicesDisplayUi(
    override val ctx: Context,
    private val theme: Theme,
    private val commitContentCallback: (String) -> Unit,
): Ui {

    //展示复制的内容
    private val copiedContentDisplayTv = textView {
        includeFontPadding = false
        textSize = 14f
        setTextColor(Color.BLACK)
        setBackgroundResource(R.drawable.bg_copy_content_tv)
        padding = dp(10)
        maxWidth = dp(250)
    }

    private val flexibleAreaUi = FlexibleAreaUi(ctx, theme, commitContentCallback)

    override val root = constraintLayout {
        add(copiedContentDisplayTv, lParams(wrapContent, wrapContent){
            topOfParent(dp(14))
            endOfParent(dp(15))
        })
        add(flexibleAreaUi.root, lParams(matchParent, dp(0)){
            below(copiedContentDisplayTv, dp(12))
            bottomOfParent()
        })
    }

    //设置请求的状态
    fun onStateUpdated(state: GenerateUiState) {
        when(state) {
            GenerateUiState.NotCopiedYet -> throw IllegalArgumentException()
            is GenerateUiState.HasCopiedContent -> {
                copiedContentDisplayTv.text = state.copiedContent
            }
            else -> {}
        }
        flexibleAreaUi.onStateUpdate(state)
    }


    private class FlexibleAreaUi(
        override val ctx: Context,
        private val theme: Theme,
        private val commitContentCallback: (String) -> Unit,
    ): Ui {

        companion object {
            private const val TAG = "FlexibleAreaUi"
        }

        private val loadingTv = textView {
            includeFontPadding = false
            textSize = 14f
            setTextColor(Color.WHITE)
            setBackgroundResource(R.drawable.bg_loading_content_tv)
            text = "正在加载内容......"
            padding = dp(10)
            maxWidth = dp(250)
        }
        private val loadingLayout = constraintLayout {
            add(loadingTv, lParams(wrapContent, wrapContent) {
                topOfParent()
                startOfParent(dp(15))
            })
        }

        private val errorTv = textView {
            includeFontPadding = false
            textSize = 14f
            setTextColor(Color.WHITE)
            setBackgroundResource(R.drawable.bg_error_content_tv)
            padding = dp(10)
            maxWidth = dp(250)
        }
        private val errorLayout = constraintLayout {
            add(errorTv, lParams(wrapContent, wrapContent) {
                topOfParent()
                startOfParent(dp(15))
            })
        }

        private val advicesTitleTv = textView {
            includeFontPadding = false
            textSize = 14f
            setTextColor(Color.BLACK)
            typeface = Typeface.defaultFromStyle(Typeface.BOLD)
            text = "以下是为您推荐的内容"
        }
        private val advicesLayout = verticalLayout {
            add(advicesTitleTv, lParams(wrapContent, wrapContent) {
                setMargins(dp(14), dp(14), 0, 0)
            })
        }
        private val advicesRecyclerLayout = view(::NestedScrollView) {
            add(advicesLayout, lParams(matchParent, matchParent))
            setBackgroundResource(R.drawable.bg_advices_display_area)
        }

        override val root = view(::ViewAnimator) {
            add(loadingLayout, lParams(matchParent, matchParent))
            add(errorLayout, lParams(matchParent, matchParent))
            add(advicesRecyclerLayout, lParams(matchParent, matchParent){
                setMargins(dp(7), dp(12), dp(7), dp(12))
            })
        }

        fun onStateUpdate(state: GenerateUiState) {
            Timber.tag(TAG).d("onStateUpdate -> ${state.javaClass.simpleName}")
            when(state) {
                is GenerateUiState.LoadingAnswers -> {
                    root.displayedChild = 0
                }
                is GenerateUiState.AdvicesConfirmed -> {
                    updateAdvicesLayout(state.response.getContents())
                    root.displayedChild = 2
                }
                is GenerateUiState.ApiError -> {
                    errorTv.text = state.error
                    root.displayedChild = 1
                }
                else -> {}
            }
        }

        private fun updateAdvicesLayout(advices: List<String>) {
            advicesLayout.removeAllViews()
            advicesLayout.apply {
                add(advicesTitleTv, lParams(wrapContent, wrapContent) {
                    setMargins(dp(14), dp(14), 0, 0)
                })
                advices.forEach {
                    add(
                        GenerateContentUi(it, ctx, theme, commitContentCallback).root,
                        lParams(matchParent, wrapContent){
                            setMargins(dp(10), dp(10), dp(10), 0)
                        }
                    )
                }
            }
        }
    }
}