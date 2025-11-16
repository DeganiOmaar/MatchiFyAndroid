package com.example.matchify.data.remote

import com.example.matchify.data.remote.dto.RecruiterProfileResponseDto
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.PUT
import retrofit2.http.Part

interface RecruiterApi {

    // Récupérer le profil recruteur connecté
    @GET("recruiter/profile") // Changed back to "recruiter/profile" to match backend Controller
    suspend fun getRecruiterProfile(): RecruiterProfileResponseDto

    // Mettre à jour le profil recruteur (multipart comme sur iOS)
    @Multipart
    @PUT("recruiter/profile")
    suspend fun updateRecruiterProfile(
        @Part("fullName") fullName: RequestBody?,
        @Part("email") email: RequestBody?,
        @Part("phone") phone: RequestBody?,
        @Part("location") location: RequestBody?,
        @Part("description") description: RequestBody?,
        @Part profileImage: MultipartBody.Part?
    ): RecruiterProfileResponseDto
}