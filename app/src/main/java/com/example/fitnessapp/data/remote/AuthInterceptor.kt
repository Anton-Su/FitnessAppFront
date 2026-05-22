package com.example.fitnessapp.data.remote

import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response
import com.example.fitnessapp.data.preferences.TokenManager

class AuthInterceptor(
    private val tokenManager: TokenManager
) : Interceptor {
    companion object {
        private const val TAG = "AuthInterceptor"
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val token = tokenManager.getAccessToken()
        val request = chain.request().newBuilder()
        if (!token.isNullOrEmpty()) {
            Log.d(TAG, "intercept: adding Authorization header with token (length=${token.length}), URL=${chain.request().url}")
            request.addHeader(
                "Authorization",
                "Bearer $token"
            )
        } else {
            Log.w(TAG, "intercept: no token available! URL=${chain.request().url}")
        }
        return chain.proceed(request.build())
    }
}