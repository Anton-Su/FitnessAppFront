package com.example.fitnessapp.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

val Context.tokenDataStore: DataStore<Preferences> by preferencesDataStore(name = "auth_tokens")

class TokenManager(private val context: Context) {

    private val ACCESS_TOKEN = stringPreferencesKey("access_token")
    private var cachedAccessToken: String? = null

    suspend fun loadTokens() {
        val prefs = context.tokenDataStore.data.first()
        cachedAccessToken = prefs[ACCESS_TOKEN]
    }

    fun getAccessToken(): String? {
        cachedAccessToken?.let { return it }
        return runBlocking {
            val prefs = context.tokenDataStore.data.first()
            prefs[ACCESS_TOKEN].also { cachedAccessToken = it }
        }
    }

    suspend fun saveAccessToken(token: String) {
        cachedAccessToken = token
        context.tokenDataStore.edit { it[ACCESS_TOKEN] = token }
    }


    suspend fun clearTokens() {
        cachedAccessToken = null
        context.tokenDataStore.edit { it.clear() }
    }
}