package org.fcitx.fcitx5.android.input.generate

import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import org.fcitx.fcitx5.android.data.theme.Theme
import org.fcitx.fcitx5.android.input.generate.data.GenerateContentEntity

class GenerateContentAdapter(private val theme: Theme)
    : PagingDataAdapter<GenerateContentEntity, GenerateContentAdapter.ViewHolder>(diffCallback)  {

    companion object {
        private val diffCallback = object: DiffUtil.ItemCallback<GenerateContentEntity>() {
            override fun areItemsTheSame(
                oldItem: GenerateContentEntity,
                newItem: GenerateContentEntity
            ) = true

            override fun areContentsTheSame(
                oldItem: GenerateContentEntity,
                newItem: GenerateContentEntity
            ) = oldItem == newItem
        }
    }

    private val data = mutableListOf<GenerateContentEntity>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(GenerateContentUi(parent.context, theme))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        getItem(position)?.let {
            holder.generateContentUi.text.text = it.content
            adjustTextLayoutBias(holder.generateContentUi, it.isFromUser)
        }
    }

    private fun adjustTextLayoutBias(generateContentUi: GenerateContentUi, isMsgFromUser: Boolean) {
        (generateContentUi.text.layoutParams as? ConstraintLayout.LayoutParams)?.horizontalBias = if (isMsgFromUser) 1f else 0f
    }

    class ViewHolder(val generateContentUi: GenerateContentUi): RecyclerView.ViewHolder(generateContentUi.root)
}