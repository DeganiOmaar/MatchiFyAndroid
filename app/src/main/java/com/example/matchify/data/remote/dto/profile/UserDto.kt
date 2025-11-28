package com.example.matchify.data.remote.dto.profile

import com.google.gson.annotations.SerializedName

data class UserDto(
    @SerializedName("_id") val id: String? = null,
    @SerializedName("fullName") val fullName: String,
    @SerializedName("email") val email: String,
    @SerializedName("role") val role: String,
    @SerializedName("phone") val phone: String? = null,
    @SerializedName("profileImage") val profileImage: String? = null,
    @SerializedName("bannerImage") val bannerImage: String? = null,
    @SerializedName("location") val location: String? = null,
    @SerializedName("talent") val talent: List<String>? = null,
    @SerializedName("description") val description: String? = null,
    @SerializedName("skills") val skills: List<String>? = null,
    @SerializedName("createdAt") val createdAt: String? = null,
    @SerializedName("updatedAt") val updatedAt: String? = null
)

