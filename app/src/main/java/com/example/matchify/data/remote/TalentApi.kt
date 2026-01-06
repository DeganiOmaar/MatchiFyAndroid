package com.example.matchify.data.remote

import com.example.matchify.data.remote.dto.profile.TalentProfileResponseDto
import com.example.matchify.data.remote.dto.stats.TalentStatsResponseDto
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

interface TalentApi {

    // Récupérer le profil talent connecté
    @GET("talent/profile")
    suspend fun getTalentProfile(): TalentProfileResponseDto

    // Mettre à jour le profil talent (multipart)
    @Multipart
    @PUT("talent/profile")
    suspend fun updateTalentProfile(
        @Part("fullName") fullName: RequestBody?,
        @Part("email") email: RequestBody?,
        @Part("phone") phone: RequestBody?,
        @Part("location") location: RequestBody?,
        @Part("talent") talent: RequestBody?,
        @Part("description") description: RequestBody?,
        @Part("skills") skills: RequestBody?, // JSON array as string
        @Part profileImage: MultipartBody.Part?
    ): TalentProfileResponseDto

    // Récupérer les statistiques de proposals du talent
    @GET("talent/stats")
    suspend fun getTalentStats(@Query("days") days: Int): TalentStatsResponseDto
    
    // Upload CV (PDF/DOC/DOCX)
    // POST /talent/upload-cv
    // Même endpoint que iOS
    @Multipart
    @POST("talent/upload-cv")
    suspend fun uploadCV(
        @Part file: MultipartBody.Part
    ): TalentProfileResponseDto

    // Récupérer tous les talents
    @GET("users/talents")
    suspend fun getAllTalents(): List<com.example.matchify.data.remote.dto.profile.TalentUserDto>
}

