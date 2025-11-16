package com.example.matchify.data.remote

import android.util.Log
import com.example.matchify.data.local.AuthPreferences
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// Change to a class that can be initialized with dependencies
class ApiService(private val authPreferences: AuthPreferences) {

    // 🔥 IMPORTANT: localhost pour Android
    private val baseUrl = "http://10.0.2.2:3000/"

    // -----------------------------------------------------------
    // 🔐 Interceptor pour ajouter automatiquement le Bearer Token
    // -----------------------------------------------------------
    private val authInterceptor = Interceptor { chain ->
        val token = authPreferences.currentAccessToken.value

        val originalRequest = chain.request()
        val newRequest: Request = if (!token.isNullOrBlank()) {
            originalRequest.newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
        } else {
            originalRequest
        }

        Log.d("ApiService", "Sending request: ${newRequest.url}")
        Log.d("ApiService", "Authorization: ${newRequest.header("Authorization")}")

        chain.proceed(newRequest)
    }

    // -----------------------------------------------------------
    // 🌐 Client HTTP
    // -----------------------------------------------------------
    private val client = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .build()

    // -----------------------------------------------------------
    // 🔧 Instance Retrofit unique
    // -----------------------------------------------------------
    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // -----------------------------------------------------------
    // 🔐 AUTH API
    // -----------------------------------------------------------
    val authApi: AuthApi by lazy {
        retrofit.create(AuthApi::class.java)
    }

    // -----------------------------------------------------------
    // 👔 RECRUITER API (Profile + Edit + Multipart)
    // -----------------------------------------------------------
    val recruiterApi: RecruiterApi by lazy {
        retrofit.create(RecruiterApi::class.java)
    }

    companion object {
        @Volatile
        private var INSTANCE: ApiService? = null

        fun initialize(authPreferences: AuthPreferences) {
            if (INSTANCE == null) {
                synchronized(this) {
                    if (INSTANCE == null) {
                        INSTANCE = ApiService(authPreferences)
                    }
                }
            }
        }

        fun getInstance(): ApiService {
            return INSTANCE ?: throw IllegalStateException("ApiService has not been initialized")
        }
    }
}