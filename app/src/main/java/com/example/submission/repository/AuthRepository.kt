package com.example.submission.repository

import com.example.submission.api.ApiService
import com.example.submission.response.LoginResponse
import com.example.submission.response.RegisterResponse

class AuthRepository(private val apiService: ApiService) {

    // Fungsi untuk register user
    suspend fun register(name: String, email: String, password: String): RegisterResponse {
        return apiService.register(name, email, password)
    }

    // Fungsi untuk login user
    suspend fun login(email: String, password: String): LoginResponse {
        return apiService.login(email, password)
    }
}
