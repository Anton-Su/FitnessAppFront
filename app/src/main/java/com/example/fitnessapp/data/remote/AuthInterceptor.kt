package com.example.fitnessapp.data.remote

import okhttp3.Interceptor
import okhttp3.Response
import com.example.fitnessapp.data.preferences.TokenManager

class AuthInterceptor(
    private val tokenManager: TokenManager
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val token = tokenManager.getAccessToken()
        val request = chain.request().newBuilder()
        if (!token.isNullOrEmpty()) {
            request.addHeader(
                "Authorization",
                "Bearer $token"
            )
        }
        return chain.proceed(request.build())
    }
}