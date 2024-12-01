package com.example.submission.repository

import com.example.submission.api.ApiService
import com.example.submission.data.DataStoreManager
import com.example.submission.response.StoryResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class StoryRepository private constructor(
    private val apiService: ApiService,
    private val dataStoreManager: DataStoreManager
) {

    suspend fun getStories(): Flow<StoryResponse> = flow {
        val response = apiService.getStories()
        emit(response.body() ?: StoryResponse())  // Pastikan response tidak null
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

