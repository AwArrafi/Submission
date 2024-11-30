package com.example.submission.di

import com.example.submission.api.RetrofitInstance
import com.example.submission.repository.AuthRepository
import com.example.submission.viewmodel.AuthViewModel

object Injection {

    fun provideAuthRepository(): AuthRepository {
        val apiService = RetrofitInstance.api
        return AuthRepository(apiService)
    }

    fun provideAuthViewModel(): AuthViewModel {
        val authRepository = provideAuthRepository()
        return AuthViewModel(authRepository)
    }
}
