package org.fcitx.fcitx5.android.ui.main.settings.theme

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class ThemeListItemDecoration(val itemWidth: Int, val spanCount: Int) :
    RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val columnWidth = parent.width / spanCount
        val offset = (parent.width - itemWidth * spanCount) / (spanCount + 1)
        val halfOffset = offset / 2
        val position = parent.getChildAdapterPosition(view)
        val rowCount = parent.adapter?.run { itemCount / spanCount } ?: -1
        val n = position % spanCount
        outRect.set(
            (n + 1) * offset + n * (itemWidth - columnWidth),
            if (position < spanCount) offset else halfOffset,
            0, // (n + 1) * (columnWidth - itemWidth - offset)
            if (position / spanCount == rowCount - 1) offset else halfOffset
        )
    }
}
