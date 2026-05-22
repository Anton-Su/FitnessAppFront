package com.example.fitnessapp.data.preferences

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

val Context.tokenDataStore: DataStore<Preferences> by preferencesDataStore(name = "auth_tokens")

class TokenManager(private val context: Context) {

    companion object {
        private const val TAG = "TokenManager"
    }

    private val ACCESS_TOKEN = stringPreferencesKey("access_token")
    private var cachedAccessToken: String? = null

    suspend fun loadTokens() {
        val prefs = context.tokenDataStore.data.first()
        cachedAccessToken = prefs[ACCESS_TOKEN]
        Log.d(TAG, "loadTokens: cachedAccessToken is ${if (cachedAccessToken.isNullOrEmpty()) "empty" else "set (length=${cachedAccessToken?.length})"}")
    }

    fun getAccessToken(): String? {
        cachedAccessToken?.let {
            Log.d(TAG, "getAccessToken: returning cachedAccessToken (length=${it.length})")
            return it
        }
        return runBlocking {
            val prefs = context.tokenDataStore.data.first()
            prefs[ACCESS_TOKEN].also { token ->
                cachedAccessToken = token
                Log.d(TAG, "getAccessToken: loaded from DataStore, token is ${if (token.isNullOrEmpty()) "empty" else "set (length=${token.length})"}")
            }
        }
    }

    suspend fun saveAccessToken(token: String) {
        Log.d(TAG, "saveAccessToken: saving token (length=${token.length})")
        cachedAccessToken = token
        context.tokenDataStore.edit { it[ACCESS_TOKEN] = token }
    }


    suspend fun clearTokens() {
        Log.d(TAG, "clearTokens: clearing cached token and DataStore")
        cachedAccessToken = null
        context.tokenDataStore.edit { it.clear() }
        Log.d(TAG, "clearTokens: verification - cachedAccessToken is now ${if (cachedAccessToken == null) "null" else "NOT null (ERROR!)"}")
    }
}