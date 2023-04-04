package org.fcitx.fcitx5.android.input.generate

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class MessageItemDecoration(val verticalSpace: Int): RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) = outRect.run {
        top = verticalSpace
        bottom = verticalSpace
    }

}