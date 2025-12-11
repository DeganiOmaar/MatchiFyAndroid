package com.example.matchify.data.remote.dto.auth

data class ResetPasswordRequest(
    val newPassword: String,
    val confirmPassword: String
)

