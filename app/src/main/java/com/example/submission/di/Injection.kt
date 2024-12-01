package com.example.submission.di

import android.content.Context
import com.example.submission.api.ApiConfig
import com.example.submission.data.DataStoreManager
import com.example.submission.repository.AuthRepository
import com.example.submission.repository.StoryRepository
import com.example.submission.viewmodel.AuthViewModel
import com.example.submission.viewmodel.StoryViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

object Injection {

    // Fungsi untuk menyediakan AuthRepository
    fun provideAuthRepository(): AuthRepository {
        val apiService = ApiConfig.api // Menggunakan ApiConfig untuk AuthRepository
        return AuthRepository(apiService)
    }

    // Fungsi untuk menyediakan AuthViewModel
    fun provideAuthViewModel(): AuthViewModel {
        val authRepository = provideAuthRepository()
        return AuthViewModel(authRepository)
    }

    // Fungsi untuk menyediakan StoryRepository dengan token dari DataStore
    suspend fun provideStoryRepository(context: Context): StoryRepository {
        val dataStoreManager = DataStoreManager(context)

        // Ambil token secara asynchronous menggunakan withContext(Dispatchers.IO)
        val token = withContext(Dispatchers.IO) { dataStoreManager.token.first() }

        val apiService = if (!token.isNullOrEmpty()) {
            ApiConfig.getApiServiceWithToken(token) // Ambil ApiService dengan token
        } else {
            ApiConfig.api // Jika token kosong, gunakan ApiService tanpa token
        }

        return StoryRepository.getInstance(apiService, dataStoreManager)
    }

    // Fungsi untuk menyediakan StoryViewModel
    suspend fun provideStoryViewModel(context: Context): StoryViewModel {
        // Gunakan coroutine untuk memanggil fungsi provideStoryRepository
        val storyRepository = provideStoryRepository(context)
        return StoryViewModel(storyRepository)
    }
}
