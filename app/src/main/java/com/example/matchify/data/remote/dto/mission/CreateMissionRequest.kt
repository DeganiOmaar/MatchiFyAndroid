package com.example.matchify.data.remote.dto.mission

data class CreateMissionRequest(
    val title: String,
    val description: String,
    val duration: String,
    val budget: Int,
    val skills: List<String>
)

