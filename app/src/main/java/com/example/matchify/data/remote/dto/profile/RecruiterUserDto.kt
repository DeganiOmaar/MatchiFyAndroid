package com.example.matchify.data.remote.dto.profile

import com.google.gson.annotations.SerializedName

data class RecruiterUserDto(
    @SerializedName("_id") val id: String?,
    @SerializedName("fullName") val fullName: String,
    @SerializedName("email") val email: String,
    @SerializedName("role") val role: String,
    @SerializedName("phone") val phone: String?,
    @SerializedName("profileImage") val profileImage: String?,
    @SerializedName("location") val location: String?,
    // Le backend renvoie un tableau pour "talent" (ex: []), on le mappe donc sur une liste
    @SerializedName("talent") val talent: List<String>?,
    @SerializedName("description") val description: String?,
    @SerializedName("createdAt") val createdAt: String?,
    @SerializedName("updatedAt") val updatedAt: String?
)

