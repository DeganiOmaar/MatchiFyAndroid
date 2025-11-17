package com.example.matchify.data.remote.dto.auth

data class RecruiterSignupRequest(
    val fullName: String,
    val email: String,
    val password: String,
    val confirmPassword: String
)

