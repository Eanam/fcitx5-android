package org.fcitx.fcitx5.android.input.generate

import androidx.lifecycle.lifecycleScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.fcitx.fcitx5.android.input.FcitxInputMethodService
import org.fcitx.fcitx5.android.input.dependency.inputMethodService
import org.fcitx.fcitx5.android.input.dependency.theme
import org.fcitx.fcitx5.android.input.generate.data.GenerateContentEntity
import org.fcitx.fcitx5.android.input.generate.model.GenerateContentRepo
import org.fcitx.fcitx5.android.input.wm.InputWindow

class GenerateWindow: InputWindow.ExtendedInputWindow<GenerateWindow>() {

    private val service: FcitxInputMethodService by manager.inputMethodService()
    private val theme by manager.theme()
    private val adapter by lazy { GenerateContentAdapter(theme) }
    private val ui by lazy {
        GenerateUi(context, theme).apply {
            recyclerView.apply {
                layoutManager = LinearLayoutManager(this@GenerateWindow.context, RecyclerView.VERTICAL, false)
                adapter = this@GenerateWindow.adapter
            }
        }
    }
    private val generateContentPager by lazy {
        val repo = GenerateContentRepo()
        Pager(PagingConfig(pageSize = 3)) { repo }
    }
    private var pagerJob: Job? = null

    override fun onCreateView() = ui.root

    override fun onAttached() {
        pagerJob = generateContentPager.flow.onEach {
            adapter.submitData(it)
        }.launchIn(service.lifecycleScope)
    }

    override fun onDetached() {
        pagerJob?.cancel()
        pagerJob = null
    }
}