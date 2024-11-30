package com.example.submission.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Extension function untuk context (DataStore)
val Context.dataStore by preferencesDataStore(name = "user_preferences")

class DataStoreManager(context: Context) {

    // Menggunakan context.dataStore yang sudah didefinisikan sebelumnya
    private val dataStore = context.dataStore

    private val TOKEN_KEY = stringPreferencesKey("token")

    suspend fun saveToken(token: String) {
        dataStore.edit { preferences ->
            preferences[TOKEN_KEY] = token
        }
    }

    // Properti untuk mengambil token menggunakan Flow
    val token: Flow<String?> = dataStore.data
        .map { preferences -> preferences[TOKEN_KEY] }
}
