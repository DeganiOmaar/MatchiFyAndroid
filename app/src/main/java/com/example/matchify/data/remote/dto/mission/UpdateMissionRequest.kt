package com.example.matchify.data.remote.dto.mission

data class UpdateMissionRequest(
    val title: String? = null,
    val description: String? = null,
    val duration: String? = null,
    val budget: Int? = null,
    val skills: List<String>? = null
)

