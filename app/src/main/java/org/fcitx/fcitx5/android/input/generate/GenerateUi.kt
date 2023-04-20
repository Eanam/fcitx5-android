package org.fcitx.fcitx5.android.input.generate

import android.content.Context
import android.widget.ViewAnimator
import androidx.transition.Fade
import androidx.transition.TransitionManager
import org.fcitx.fcitx5.android.data.prefs.AppPrefs
import org.fcitx.fcitx5.android.data.theme.Theme
import org.fcitx.fcitx5.android.input.generate.state_ui.AdvicesDisplayUi
import org.fcitx.fcitx5.android.input.generate.state_ui.EmptyUi
import org.fcitx.fcitx5.android.input.generate.state_ui.FraudWarningUi
import splitties.views.dsl.core.*
import timber.log.Timber

class GenerateUi(
    override val ctx: Context,
    private val theme: Theme,
    private val commitContentCallback: (String) -> Unit,
): Ui {

    companion object {
        private const val TAG = "GenerateUi"
    }

    private val emptyUi by lazy { EmptyUi(ctx, theme) }
    private val advicesDisplayUi by lazy { AdvicesDisplayUi(ctx, theme, commitContentCallback) }
    private val fraudWarningUi by lazy { FraudWarningUi(ctx, theme, commitContentCallback) }
    private val disableAnimation by AppPrefs.getInstance().advanced.disableAnimation

    override val root = view(::ViewAnimator) {
        add(advicesDisplayUi.root, lParams(matchParent, matchParent))
        add(emptyUi.root, lParams(matchParent, matchParent))
        add(fraudWarningUi.root, lParams(matchParent, matchParent))
    }

    fun switchUiByState(state: GenerateUiState) {
        Timber.tag(TAG).d("Switch GenerateWindow to ${state.javaClass.simpleName}")
        if (!disableAnimation)
            TransitionManager.beginDelayedTransition(root, Fade().apply { duration = 100L })
        when(state) {
            GenerateUiState.NotCopiedYet -> {
                root.displayedChild = 1
            }
            is GenerateUiState.HasCopiedContent,
            is GenerateUiState.LoadingAnswers,
            is GenerateUiState.AdvicesConfirmed,
            is GenerateUiState.ApiError -> {
                root.displayedChild = 0
                advicesDisplayUi.onStateUpdated(state)
            }
            is GenerateUiState.FraudMessageConfirmed -> {
                fraudWarningUi.updateUiContent(state.response)
                root.displayedChild = 2
            }
            else -> {}
        }
    }
}