package com.example.submission.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.submission.repository.StoryRepository
import com.example.submission.response.ListStoryItem
import com.example.submission.response.StoryDetailResponse
import com.example.submission.response.StoryResponse
import kotlinx.coroutines.launch

class StoryViewModel(private val storyRepository: StoryRepository) : ViewModel() {

    private val _stories = MutableLiveData<StoryResponse>()
    val stories: LiveData<StoryResponse> get() = _stories

    private val _storyDetail = MutableLiveData<StoryDetailResponse>()
    val storyDetail: LiveData<StoryDetailResponse> get() = _storyDetail

    private val _storiesWithLocation = MutableLiveData<List<ListStoryItem>>()
    val storiesWithLocation: LiveData<List<ListStoryItem>> = _storiesWithLocation


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

    fun fetchStoriesWithLocation() {
        viewModelScope.launch {
            try {
                // Panggil repository untuk mendapatkan data
                val response = storyRepository.getStoriesWithLocation()

                // Post data langsung ke LiveData
                _storiesWithLocation.postValue(response.listStory)
            } catch (e: Exception) {
                // Tangani error
                Log.e("StoryViewModel", "Error fetching stories with location", e)
            }
        }
    }
}
