package com.example.submission.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.submission.api.ApiService
import com.example.submission.data.DataStoreManager
import com.example.submission.repository.StoryRepository
import com.example.submission.response.UploadResponse
import kotlinx.coroutines.launch
import retrofit2.Response

class AddStoryViewModel(
    application: Application,
    private val apiService: ApiService,
    private val dataStoreManager: DataStoreManager
) : AndroidViewModel(application) {

    val uploadResponse = MutableLiveData<Response<UploadResponse>>()
    val isLoading = MutableLiveData<Boolean>()
    val errorMessage = MutableLiveData<String>()

    // Fungsi untuk mengirim cerita (post story)
    fun postStory(description: String, imageUri: Uri, lat: Double?, lon: Double?) {
        viewModelScope.launch {
            isLoading.value = true
            try {
                val context = getApplication<Application>().applicationContext // Mendapatkan context dari Application

                // Memanggil fungsi postStory di StoryRepository dengan menyuntikkan context
                val repository = StoryRepository.getInstance(apiService, dataStoreManager)
                val response = repository.postStory(description, imageUri, lat, lon, context)

                uploadResponse.value = response
            } catch (e: Exception) {
                errorMessage.value = e.message
            } finally {
                isLoading.value = false
            }
        }
    }
}
