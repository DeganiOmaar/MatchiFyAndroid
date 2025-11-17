package com.example.matchify.data.remote

import com.example.matchify.data.remote.dto.profile.TalentProfileResponseDto
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.PUT
import retrofit2.http.Part

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
        @Part("portfolioLink") portfolioLink: RequestBody?,
        @Part profileImage: MultipartBody.Part?
    ): TalentProfileResponseDto
}

