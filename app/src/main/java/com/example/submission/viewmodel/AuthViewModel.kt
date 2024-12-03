package com.example.submission.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.submission.Resource
import com.example.submission.repository.AuthRepository
import com.example.submission.response.ErrorResponse
import com.example.submission.response.LoginResponse
import com.example.submission.response.RegisterResponse
import com.google.gson.Gson
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class AuthViewModel(private val authRepository: AuthRepository) : ViewModel() {

    private val _registerState = MutableStateFlow<Resource<RegisterResponse>>(Resource.Loading())
    val registerState: StateFlow<Resource<RegisterResponse>> get() = _registerState

    private val _loginState = MutableStateFlow<Resource<LoginResponse>>(Resource.Loading())
    val loginState: StateFlow<Resource<LoginResponse>> get() = _loginState

    // Fungsi untuk login
    fun login(email: String, password: String) {
        viewModelScope.launch {
            try {
                _loginState.value = Resource.Loading()
                val response = authRepository.login(email, password)
                _loginState.value = Resource.Success(response)
            } catch (e: HttpException) {
                val errorResponse = parseErrorResponse(e)
                _loginState.value = Resource.Error(errorResponse?.message ?: "Login failed")
            } catch (e: IOException) {
                _loginState.value = Resource.Error("Network error occurred")
            } catch (e: Exception) {
                _loginState.value = Resource.Error("An unknown error occurred")
            }
        }
    }

    // Fungsi untuk parsing error response dengan Gson
    private fun parseErrorResponse(e: HttpException): ErrorResponse? {
        return try {
            val jsonInString = e.response()?.errorBody()?.string()
            Gson().fromJson(jsonInString, ErrorResponse::class.java)
        } catch (jsonException: Exception) {
            null // Jika gagal parsing, return null
        }
    }

    // Fungsi untuk registrasi tetap sama seperti sebelumnya
    fun register(name: String, email: String, password: String) {
        _registerState.value = Resource.Loading()
        viewModelScope.launch {
            try {
                val response = authRepository.register(name, email, password)
                _registerState.value = Resource.Success(response)
            } catch (e: HttpException) {
                val errorResponse = parseErrorResponse(e)
                _registerState.value = Resource.Error(errorResponse?.message ?: "Registration failed")
            } catch (e: IOException) {
                _registerState.value = Resource.Error("Network error occurred")
            } catch (e: Exception) {
                _registerState.value = Resource.Error("An unknown error occurred")
            }
        }
    }
}
