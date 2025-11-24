package com.example.matchify.ui.auth.reset

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.matchify.R
import com.example.matchify.data.remote.AuthApi
import com.example.matchify.data.remote.AuthRepository
import com.example.matchify.data.remote.dto.auth.*
import com.example.matchify.ui.components.MD3OutlinedTextField

@Composable
fun ResetPasswordScreen(
    onResetSuccess: () -> Unit,
    viewModel: ResetPasswordViewModel // Accept ViewModel as a parameter
) {
    val newPassword by viewModel.newPassword.collectAsState()
    val confirmPassword by viewModel.confirmPassword.collectAsState()
    val showNewPassword by viewModel.showNewPassword.collectAsState()
    val showConfirmPassword by viewModel.showConfirmPassword.collectAsState()
    val error by viewModel.error.collectAsState()
    val loading by viewModel.isLoading.collectAsState()
    val success by viewModel.success.collectAsState()

    LaunchedEffect(success) {
        if (success) onResetSuccess()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        horizontalAlignment = Alignment.Start
    ) {

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Create new password",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "Create your new password. If you forget it, then you have to do forgot password",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 6.dp)
        )

        Spacer(modifier = Modifier.height(30.dp))

        // NEW PASSWORD - MD3 Outlined Text Field
        MD3OutlinedTextField(
            value = newPassword,
            onValueChange = { viewModel.setNewPassword(it) },
            label = "New Password",
            placeholder = "New Password",
            leadingIcon = Icons.Default.Lock,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth(),
            errorText = null,
            isPassword = true,
            showPassword = showNewPassword,
            onPasswordToggle = { viewModel.toggleNewPassword() },
            singleLine = true
        )

        Spacer(modifier = Modifier.height(20.dp))

        // CONFIRM NEW PASSWORD - MD3 Outlined Text Field
        MD3OutlinedTextField(
            value = confirmPassword,
            onValueChange = { viewModel.setConfirmPassword(it) },
            label = "Confirm New Password",
            placeholder = "Confirm password",
            leadingIcon = Icons.Default.Lock,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth(),
            errorText = if (error != null && (newPassword != confirmPassword || confirmPassword.isEmpty())) error else null,
            isPassword = true,
            showPassword = showConfirmPassword,
            onPasswordToggle = { viewModel.toggleConfirmPassword() },
            singleLine = true
        )

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = { viewModel.reset() },
            enabled = newPassword.isNotEmpty() && confirmPassword.isNotEmpty() && !loading,
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .padding(bottom = 20.dp),
            shape = RoundedCornerShape(30.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF007AFF),
                disabledContainerColor = Color(0xFFBAD7FF)
            )
        ) {
            if (loading) {
                CircularProgressIndicator(
                    color = Color.White
                )
            } else {
                Text(
                    "Continue",
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}


@SuppressLint("ViewModelConstructorInComposable")
@Preview(showBackground = true)
@Composable
fun PreviewResetPassword() {
    val fakeAuthApi = object : AuthApi {
        override suspend fun login(body: LoginRequest): LoginResponse { TODO("Not yet implemented") }
        override suspend fun signupTalent(body: TalentSignupRequest): LoginResponse { TODO("Not yet implemented") }
        override suspend fun signupRecruiter(body: RecruiterSignupRequest): LoginResponse { TODO("Not yet implemented") }
        override suspend fun forgotPassword(body: ForgotPasswordRequest): ForgotPasswordResponse { TODO("Not yet implemented") }
        override suspend fun verifyResetCode(body: VerifyResetCodeRequest): VerifyResetCodeResponse { TODO("Not yet implemented") }
        override suspend fun resetPassword(body: ResetPasswordRequest): ResetPasswordResponse { TODO("Not yet implemented") }
        override suspend fun logout(): LogoutResponse { TODO("Not yet implemented") }
    }
    val dummyViewModel = ResetPasswordViewModel(AuthRepository(fakeAuthApi))

    ResetPasswordScreen(
        onResetSuccess = {},
        viewModel = dummyViewModel
    )
}