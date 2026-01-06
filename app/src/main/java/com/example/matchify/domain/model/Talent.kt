package com.example.matchify.domain.model

data class Talent(
    val talentId: String,
    val fullName: String?,
    val email: String?,
    val profileImageUrl: String?,
    val skills: List<String>?,
    val description: String?,
    val experienceYears: Int?,
    val availability: String?
)
