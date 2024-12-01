package com.example.submission.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.submission.repository.StoryRepository
import com.example.submission.response.StoryResponse
import kotlinx.coroutines.launch

class StoryViewModel(private val storyRepository: StoryRepository) : ViewModel() {
    private val _stories = MutableLiveData<StoryResponse>()
    val stories: LiveData<StoryResponse> get() = _stories

    fun getStories() {
        viewModelScope.launch {
            try {
                // Mendapatkan data dari repository tanpa parameter
                storyRepository.getStories().collect { response ->
                    _stories.postValue(response)
                }
            } catch (e: Exception) {
                // Tangani error jika perlu
                _stories.postValue(StoryResponse(emptyList()))
            }
        }
    }
}
