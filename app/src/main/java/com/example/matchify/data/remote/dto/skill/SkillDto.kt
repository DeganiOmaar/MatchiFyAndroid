package com.example.matchify.data.remote.dto.skill

import com.google.gson.annotations.SerializedName

data class SkillDto(
    @SerializedName("id") val id: String? = null,
    @SerializedName("_id") val _id: String? = null,
    @SerializedName("name") val name: String,
    @SerializedName("source") val source: String, // "ESCO" or "USER"
    @SerializedName("createdBy") val createdBy: String? = null,
    @SerializedName("createdAt") val createdAt: String? = null,
    @SerializedName("updatedAt") val updatedAt: String? = null
)

