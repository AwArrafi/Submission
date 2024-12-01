package com.example.submission.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.submission.repository.StoryRepository
import com.example.submission.response.StoryDetailResponse
import com.example.submission.response.StoryResponse
import kotlinx.coroutines.launch

class StoryViewModel(private val storyRepository: StoryRepository) : ViewModel() {

    private val _stories = MutableLiveData<StoryResponse>()
    val stories: LiveData<StoryResponse> get() = _stories

    private val _storyDetail = MutableLiveData<StoryDetailResponse>()
    val storyDetail: LiveData<StoryDetailResponse> get() = _storyDetail

    // Fungsi untuk mendapatkan daftar cerita
    fun getStories() {
        viewModelScope.launch {
            try {
                storyRepository.getStories().collect { response ->
                    _stories.postValue(response)
                }
            } catch (e: Exception) {
                _stories.postValue(StoryResponse(emptyList()))  // Handle error
            }
        }
    }

    // Fungsi untuk mendapatkan detail cerita
    fun getStoryDetail(storyId: String) {
        viewModelScope.launch {
            try {
                storyRepository.getStoryDetail(storyId).collect { response ->
                    _storyDetail.postValue(response)
                }
            } catch (e: Exception) {
                _storyDetail.postValue(StoryDetailResponse())  // Handle error
            }
        }
    }
}
