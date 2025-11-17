package com.example.matchify.data.remote.dto.profile

import com.google.gson.annotations.SerializedName

data class RecruiterProfileResponseDto(
    @SerializedName("message") val message: String?,
    @SerializedName("user") val user: RecruiterUserDto
)

