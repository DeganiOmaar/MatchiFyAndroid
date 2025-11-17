package com.example.matchify.data.remote

import com.example.matchify.data.remote.dto.auth.*
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {

    @POST("auth/login")
    suspend fun login(@Body body: LoginRequest): LoginResponse

    @POST("auth/signup/talent")
    suspend fun signupTalent(@Body body: TalentSignupRequest): LoginResponse

    @POST("auth/signup/recruiter")
    suspend fun signupRecruiter(@Body body: RecruiterSignupRequest): LoginResponse

    @POST("auth/password/forgot")
    suspend fun forgotPassword(@Body body: ForgotPasswordRequest): ForgotPasswordResponse

    @POST("auth/password/verify")
    suspend fun verifyResetCode(@Body body: VerifyResetCodeRequest): VerifyResetCodeResponse

    @POST("auth/password/reset")
    suspend fun resetPassword(@Body body: ResetPasswordRequest): ResetPasswordResponse
}