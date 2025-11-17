package com.example.matchify.ui.auth.reset

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.matchify.R
import com.example.matchify.data.remote.AuthApi
import com.example.matchify.data.remote.AuthRepository
import com.example.matchify.data.remote.dto.auth.*

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
            .padding(24.dp),
        horizontalAlignment = Alignment.Start
    ) {

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Create new password",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "Create your new password. If you forget it, then you have to do forgot password",
            color = Color.Gray,
            modifier = Modifier.padding(top = 6.dp)
        )

        Spacer(modifier = Modifier.height(30.dp))

        Text("New Password", color = Color.Gray)
        Spacer(modifier = Modifier.height(6.dp))

        PasswordInputField(
            value = newPassword,
            placeholder = "New Password",
            visible = showNewPassword,
            onValueChanged = viewModel::setNewPassword,
            onToggleVisibility = viewModel::toggleNewPassword
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text("Confirm New Password", color = Color.Gray)
        Spacer(modifier = Modifier.height(6.dp))

        PasswordInputField(
            value = confirmPassword,
            placeholder = "Confirm password",
            visible = showConfirmPassword,
            onValueChanged = viewModel::setConfirmPassword,
            onToggleVisibility = viewModel::toggleConfirmPassword
        )

        if (error != null) {
            Spacer(modifier = Modifier.height(10.dp))
            Text(error ?: "", color = Color.Red)
        }

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
                CircularProgressIndicator(color = Color.White)
            } else {
                Text("Continue", color = Color.White, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
fun PasswordInputField(
    value: String,
    placeholder: String,
    visible: Boolean,
    onValueChanged: (String) -> Unit,
    onToggleVisibility: () -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChanged,
        placeholder = { Text(placeholder, color = Color.Gray) },
        singleLine = true,
        leadingIcon = {
            Image(
                painter = painterResource(R.drawable.ic_lock),
                contentDescription = null
            )
        },
        trailingIcon = {
            IconButton(onClick = onToggleVisibility) {
                Icon(
                    painterResource(id = if (visible) R.drawable.visibility_off else R.drawable.visibility),
                    contentDescription = null,
                    tint = Color.Gray
                )
            }
        },
        visualTransformation = if (visible) VisualTransformation.None else PasswordVisualTransformation(),
        shape = RoundedCornerShape(35.dp),
        modifier = Modifier.fillMaxWidth()
    )
}

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
    }
    val dummyViewModel = ResetPasswordViewModel(AuthRepository(fakeAuthApi))

    ResetPasswordScreen(
        onResetSuccess = {},
        viewModel = dummyViewModel
    )
}