package com.example.matchify.data.remote.dto.auth

import com.example.matchify.domain.model.UserModel

data class LoginResponse(
    val message: String,
    val token: String,
    val role: String,
    val user: UserModel
)

