package com.example.matchify.data.remote.dto

import com.google.gson.annotations.SerializedName
import com.example.matchify.domain.model.UserModel

// -------------------------------------------------------------
// AUTH
// -------------------------------------------------------------

data class LoginRequest(
    val email: String,
    val password: String
)

data class LoginResponse(
    val message: String,
    val token: String,
    val role: String,
    val user: UserModel
)

data class TalentSignupRequest(
    val fullName: String,
    val email: String,
    val password: String,
    val confirmPassword: String,
    val phone: String,
    val profileImage: String,
    val location: String,
    val talent: String
)

data class RecruiterSignupRequest(
    val fullName: String,
    val email: String,
    val password: String,
    val confirmPassword: String
)

data class ForgotPasswordRequest(val email: String)
data class ForgotPasswordResponse(val message: String, val expiresIn: String?)

data class VerifyResetCodeRequest(val code: String)
data class VerifyResetCodeResponse(val message: String, val verified: Boolean)

data class ResetPasswordRequest(
    val newPassword: String,
    val confirmPassword: String
)

data class ResetPasswordResponse(val message: String)


// -------------------------------------------------------------
// RECRUITER PROFILE (GET / PUT)
// -------------------------------------------------------------

data class RecruiterUserDto(
    @SerializedName("_id") val id: String?,
    @SerializedName("fullName") val fullName: String,
    @SerializedName("email") val email: String,
    @SerializedName("role") val role: String,
    @SerializedName("phone") val phone: String?,
    @SerializedName("profileImage") val profileImage: String?,
    @SerializedName("location") val location: String?,
    @SerializedName("talent") val talent: String?,

    // 🔥 NOUVEAU CHAMP – DOIT ABSOLUMENT ÊTRE ICI
    @SerializedName("description") val description: String?,

    @SerializedName("createdAt") val createdAt: String?,
    @SerializedName("updatedAt") val updatedAt: String?
)

data class RecruiterProfileResponseDto(
    @SerializedName("message") val message: String?,
    @SerializedName("user") val user: RecruiterUserDto
)