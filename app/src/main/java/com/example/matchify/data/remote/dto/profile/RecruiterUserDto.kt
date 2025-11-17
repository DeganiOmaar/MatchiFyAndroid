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
    @SerializedName("talent") val talent: String?,
    @SerializedName("description") val description: String?,
    @SerializedName("createdAt") val createdAt: String?,
    @SerializedName("updatedAt") val updatedAt: String?
)

