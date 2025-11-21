package com.example.matchify.data.remote.dto.portfolio

import com.google.gson.annotations.SerializedName

data class ProjectResponseDto(
    @SerializedName("message") val message: String? = null,
    @SerializedName("project") val project: ProjectDto
)

