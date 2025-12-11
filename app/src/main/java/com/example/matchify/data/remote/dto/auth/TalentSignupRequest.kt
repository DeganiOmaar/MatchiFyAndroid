package com.example.matchify.data.remote.dto.auth

data class TalentSignupRequest(
    val fullName: String,
    val email: String,
    val password: String,
    val confirmPassword: String,
    val phone: String,
    val profileImage: String
)

