package com.example.submission.data

import android.util.Log
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.submission.api.ApiService
import com.example.submission.response.ListStoryItem

class StoryPagingSource(
    private val apiService: ApiService
) : PagingSource<Int, ListStoryItem>() {

    companion object {
        fun snapshot(items: List<ListStoryItem>): PagingData<ListStoryItem> {
            return PagingData.from(items)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ListStoryItem> {
        return try {
            val page = params.key ?: 1
            val pageSize = params.loadSize

            Log.d("StoryPagingSource", "Loading page $page with size $pageSize")

            val response = apiService.getStories(page, pageSize)
            val stories = response.listStory

            LoadResult.Page(
                data = stories,
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (stories.isEmpty()) null else page + 1
            )
        } catch (e: Exception) {
            Log.e("StoryPagingSource", "Error loading data", e)
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, ListStoryItem>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}
