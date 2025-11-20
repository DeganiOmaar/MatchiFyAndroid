package com.example.matchify.data.remote.dto.portfolio

import com.example.matchify.data.remote.dto.portfolio.MediaItemDto
import com.google.gson.annotations.SerializedName

data class ProjectDto(
    @SerializedName("_id") val _id: String? = null,
    @SerializedName("id") val id: String? = null,
    @SerializedName("talentId") val talentId: String? = null,
    @SerializedName("title") val title: String? = null,
    @SerializedName("role") val role: String? = null,
    @SerializedName("media") val media: List<MediaItemDto>? = null,
    @SerializedName("skills") val skills: List<String>? = null,
    @SerializedName("description") val description: String? = null,
    @SerializedName("projectLink") val projectLink: String? = null,
    @SerializedName("createdAt") val createdAt: String? = null,
    @SerializedName("updatedAt") val updatedAt: String? = null
)

