package com.example.matchify.data.remote

import com.example.matchify.data.remote.dto.auth.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AuthRepository(private val api: AuthApi) {

    suspend fun login(email: String, password: String): LoginResponse =
        withContext(Dispatchers.IO) {
            api.login(LoginRequest(email, password))
        }

    suspend fun signupTalent(request: TalentSignupRequest): LoginResponse =
        withContext(Dispatchers.IO) {
            api.signupTalent(request)
        }

    suspend fun signupRecruiter(request: RecruiterSignupRequest): LoginResponse =
        withContext(Dispatchers.IO) {
            api.signupRecruiter(request)
        }

    suspend fun forgotPassword(email: String): ForgotPasswordResponse =
        withContext(Dispatchers.IO) {
            api.forgotPassword(ForgotPasswordRequest(email))
        }

    suspend fun verifyResetCode(code: String): VerifyResetCodeResponse =
        withContext(Dispatchers.IO) {
            api.verifyResetCode(VerifyResetCodeRequest(code))
        }

    suspend fun resetPassword(newPassword: String, confirmPassword: String): ResetPasswordResponse =
        withContext(Dispatchers.IO) {
            api.resetPassword(
                ResetPasswordRequest(newPassword, confirmPassword)
            )
        }

    suspend fun logout(): LogoutResponse =
        withContext(Dispatchers.IO) {
            api.logout()
        }
}