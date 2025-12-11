package com.example.matchify.data.remote

import com.example.matchify.data.remote.dto.profile.RecruiterProfileResponseDto
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.PUT
import retrofit2.http.Part

interface RecruiterApi {

    @GET("recruiter/profile")
    suspend fun getRecruiterProfile(): RecruiterProfileResponseDto

    @Multipart
    @PUT("recruiter/profile")
    suspend fun updateRecruiterProfile(
        @Part("fullName") fullName: RequestBody?,
        @Part("email") email: RequestBody?,
        @Part("phone") phone: RequestBody?,
        @Part("location") location: RequestBody?,
        @Part("description") description: RequestBody?,
        @Part profileImage: MultipartBody.Part?
    ): ResponseBody
}