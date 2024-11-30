package com.example.submission.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.submission.Resource
import com.example.submission.repository.AuthRepository
import com.example.submission.response.LoginResponse
import com.example.submission.response.RegisterResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel(private val authRepository: AuthRepository) : ViewModel() {

    private val _registerState = MutableStateFlow<Resource<RegisterResponse>>(Resource.Loading())
    val registerState: StateFlow<Resource<RegisterResponse>> get() = _registerState

    private val _loginState = MutableStateFlow<Resource<LoginResponse>>(Resource.Loading())
    val loginState: StateFlow<Resource<LoginResponse>> get() = _loginState

    fun register(name: String, email: String, password: String) {
        viewModelScope.launch {
            try {
                _registerState.value = Resource.Loading()
                val response = authRepository.register(name, email, password)
                _registerState.value = Resource.Success(response)
            } catch (e: Exception) {
                _registerState.value = Resource.Error(e.message ?: "An error occurred")
            }
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            try {
                _loginState.value = Resource.Loading()
                val response = authRepository.login(email, password)
                _loginState.value = Resource.Success(response)
            } catch (e: Exception) {
                _loginState.value = Resource.Error(e.message ?: "An error occurred")
            }
        }
    }
}
