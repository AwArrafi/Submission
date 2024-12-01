package com.example.submission.repository

import com.example.submission.api.ApiService
import com.example.submission.data.DataStoreManager
import com.example.submission.response.StoryDetailResponse
import com.example.submission.response.StoryResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf

class StoryRepository private constructor(
    private val apiService: ApiService,
    private val dataStoreManager: DataStoreManager
) {

    // Fungsi untuk mendapatkan daftar cerita
    suspend fun getStories(): Flow<StoryResponse> = flow {
        val response = apiService.getStories()
        emit(response.body() ?: StoryResponse())  // Pastikan response tidak null
    }

    // Di dalam StoryRepository
    // Di StoryRepository
    suspend fun getStoryDetail(storyId: String): Flow<StoryDetailResponse> {
        return apiService.getDetailStories(storyId).let { response ->
            if (response.isSuccessful) {
                flowOf(response.body() ?: StoryDetailResponse())
            } else {
                flowOf(StoryDetailResponse(error = true, message = "Failed to load story"))
            }
        }
    }



    companion object {
        @Volatile
        private var instance: StoryRepository? = null

        fun getInstance(apiService: ApiService, dataStoreManager: DataStoreManager): StoryRepository {
            return instance ?: synchronized(this) {
                instance ?: StoryRepository(apiService, dataStoreManager).also { instance = it }
            }
        }
    }
}
