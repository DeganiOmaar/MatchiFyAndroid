package com.example.matchify.data.remote.dto.profile

import com.example.matchify.data.remote.dto.portfolio.ProjectDto
import com.google.gson.annotations.SerializedName

data class GetUserResponseDto(
    @SerializedName("message") val message: String? = null,
    @SerializedName("user") val user: UserDto,
    @SerializedName("portfolio") val portfolio: List<ProjectDto>? = null
)

