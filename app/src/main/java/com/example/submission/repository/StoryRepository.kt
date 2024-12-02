package com.example.submission.repository

import android.content.Context
import android.net.Uri
import com.example.submission.api.ApiService
import com.example.submission.data.DataStoreManager
import com.example.submission.response.StoryDetailResponse
import com.example.submission.response.StoryResponse
import com.example.submission.response.UploadResponse
import com.example.submission.utils.uriToFile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Response

class StoryRepository private constructor(
    private val apiService: ApiService,
    private val dataStoreManager: DataStoreManager
) {

    // Fungsi untuk mendapatkan daftar cerita
    suspend fun getStories(): Flow<StoryResponse> = flow {
        val response = apiService.getStories()
        emit(response.body() ?: StoryResponse())  // Pastikan response tidak null
    }

    // Fungsi untuk mendapatkan detail cerita
    suspend fun getStoryDetail(storyId: String): Flow<StoryDetailResponse> {
        return apiService.getDetailStories(storyId).let { response ->
            if (response.isSuccessful) {
                flowOf(response.body() ?: StoryDetailResponse())
            } else {
                flowOf(StoryDetailResponse(error = true, message = "Failed to load story"))
            }
        }
    }

    // Fungsi untuk mengirimkan cerita baru
    suspend fun postStory(description: String, imageUri: Uri, lat: Double?, lon: Double?, context: Context): Response<UploadResponse> {
        return try {
            val imageFile = uriToFile(imageUri, context) // Convert URI menjadi file
            val descriptionBody = description.toRequestBody("text/plain".toMediaType())
            val requestFile = imageFile.asRequestBody("image/jpeg".toMediaType())
            val imagePart = MultipartBody.Part.createFormData("photo", imageFile.name, requestFile)

            // Optional lat dan lon
            val latBody = lat?.toString()?.toRequestBody("text/plain".toMediaType())
            val lonBody = lon?.toString()?.toRequestBody("text/plain".toMediaType())

            // Panggil API untuk post story
            apiService.postStories(
                descriptionBody, // Deskripsi
                imagePart, // Foto
                latBody, // Optional lat
                lonBody // Optional lon
            )
        } catch (e: Exception) {
            // Tangani error jika ada
            throw e
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
