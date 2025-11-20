package com.example.matchify.data.remote

import com.example.matchify.data.remote.dto.profile.GetUserResponseDto
import retrofit2.http.GET
import retrofit2.http.Path

interface UserApi {
    
    @GET("user/{id}")
    suspend fun getUserById(@Path("id") id: String): GetUserResponseDto
}

