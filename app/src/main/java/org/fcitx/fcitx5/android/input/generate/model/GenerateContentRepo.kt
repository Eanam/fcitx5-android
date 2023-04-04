package org.fcitx.fcitx5.android.input.generate.model

import androidx.paging.PagingSource
import androidx.paging.PagingState
import org.fcitx.fcitx5.android.input.generate.data.GenerateContentEntity
import kotlin.math.min

class GenerateContentRepo: PagingSource<Int, GenerateContentEntity>() {

    //TODO: TEST
    private val fakeData by lazy {
        listOf(
            GenerateContentEntity(true, "这是复制的内容"),
            GenerateContentEntity(content = "这是回答1的内容"),
            GenerateContentEntity(content = "这是回答2的内容"),
            GenerateContentEntity(content = "这是回答3的内容"),
            GenerateContentEntity(content = "这是回答4的内容"),
            GenerateContentEntity(content = "这是回答5的内容"),
            GenerateContentEntity(content = "这是回答6的内容"),
            GenerateContentEntity(content = "这是回答7的内容"),
            GenerateContentEntity(content = "这是回答8的内容"),
            GenerateContentEntity(content = "这是回答9的内容"),
        )
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, GenerateContentEntity> {
        val pageIndex = params.key ?: 0
        val firstItemIndex = pageIndex * params.loadSize
        val lastItemIndex = min(fakeData.size, (pageIndex + 1) * params.loadSize - 1)
        return try {
            LoadResult.Page(
                data = fakeData.subList(firstItemIndex, lastItemIndex + 1),
                if (pageIndex == 0) null else pageIndex - 1,
                if (lastItemIndex >= fakeData.size) null else pageIndex + 1
            )
        }catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, GenerateContentEntity>): Int? {
        TODO("Not yet implemented")
    }
}