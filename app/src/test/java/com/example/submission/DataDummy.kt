package com.example.submission

import androidx.paging.PagingData
import com.example.submission.response.ListStoryItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

object DataDummy {
    fun generateDummyStories(): List<ListStoryItem> {
        val stories = mutableListOf<ListStoryItem>()
        for (i in 1..10) {
            val story = ListStoryItem(
                id = "story-$i",
                name = "Story $i",
                description = "Description of story $i",
                photoUrl = "https://example.com/photo/$i",
                lon = null,
                lat = null,
                createdAt = "2022-12-19T12:00:00Z"
            )
            stories.add(story)
        }
        return stories
    }

    fun generatePagingData(): Flow<PagingData<ListStoryItem>> {
        return flowOf(PagingData.from(generateDummyStories()))
    }
}
