package com.example.matchify.ui.auth.signup.recruiter

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.matchify.R
import com.example.matchify.data.local.AuthPreferences
import com.example.matchify.data.remote.AuthApi
import com.example.matchify.data.remote.AuthRepository
import com.example.matchify.data.remote.dto.*

@Composable
fun RecruiterSignupScreen(
    onLoginClick: () -> Unit,
    onSignupSuccess: () -> Unit,
    viewModel: RecruiterSignupViewModel // Accept ViewModel as a parameter
) {
    // Observables
    val fullName by viewModel.fullName.collectAsState()
    val email by viewModel.email.collectAsState()
    val password by viewModel.password.collectAsState()
    val confirmPassword by viewModel.confirmPassword.collectAsState()

    val showPassword by viewModel.showPassword.collectAsState()
    val showConfirmPassword by viewModel.showConfirmPassword.collectAsState()

    val error by viewModel.error.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val navigateTo by viewModel.navigateTo.collectAsState()

    // Navigate when signup succeeds
    LaunchedEffect(navigateTo) {
        navigateTo?.let {
            onSignupSuccess()
            viewModel.onNavigationDone()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.Start
    ) {

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Sign Up Recruiter",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "Create your recruiter profile",
            color = Color.Gray,
            modifier = Modifier.padding(top = 6.dp)
        )

        Spacer(modifier = Modifier.height(30.dp))

        // FULL NAME
        InputField(
            value = fullName,
            onValueChange = viewModel::setFullName,
            placeholder = "Full Name",
            leading = R.drawable.ic_person
        )

        Spacer(modifier = Modifier.height(16.dp))

        // EMAIL
        InputField(
            value = email,
            onValueChange = viewModel::setEmail,
            placeholder = "Email",
            leading = R.drawable.ic_email
        )

        Spacer(modifier = Modifier.height(16.dp))

        // PASSWORD
        InputField(
            value = password,
            onValueChange = viewModel::setPassword,
            placeholder = "Password",
            leading = R.drawable.ic_lock,
            isPassword = true,
            visible = showPassword,
            onToggleVisibility = viewModel::toggleShowPassword
        )

        Spacer(modifier = Modifier.height(16.dp))

        // CONFIRM PASSWORD
        InputField(
            value = confirmPassword,
            onValueChange = viewModel::setConfirmPassword,
            placeholder = "Confirm Password",
            leading = R.drawable.ic_lock,
            isPassword = true,
            visible = showConfirmPassword,
            onToggleVisibility = viewModel::toggleShowConfirmPassword
        )

        if (error != null) {
            Spacer(modifier = Modifier.height(10.dp))
            Text(error ?: "", color = Color.Red)
        }

        Spacer(modifier = Modifier.height(30.dp))

        // SIGNUP BUTTON
        Button(
            onClick = { viewModel.signUp() },
            enabled = !isLoading,
            modifier = Modifier
                .fillMaxWidth()
                .height(55.dp),
            shape = RoundedCornerShape(30.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF007AFF),
                disabledContainerColor = Color(0xFFBAD7FF)
            )
        ) {
            if (isLoading) {
                CircularProgressIndicator(color = Color.White)
            } else {
                Text("Sign Up", color = Color.White, fontWeight = FontWeight.SemiBold)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Already have account
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Already have an account?", color = Color.Gray)
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "Login",
                color = Color(0xFF007AFF),
                modifier = Modifier.clickable { onLoginClick() }
            )
        }
    }
}

@Composable
fun InputField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    leading: Int,
    isPassword: Boolean = false,
    visible: Boolean = false,
    onToggleVisibility: (() -> Unit)? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(placeholder, color = Color.Gray) },
        singleLine = true,
        leadingIcon = {
            Image(
                painter = painterResource(id = leading),
                contentDescription = null
            )
        },
        trailingIcon = if (isPassword) {
            {
                IconButton(onClick = { onToggleVisibility?.invoke() }) {
                    Icon(
                        painter = painterResource(
                            id = if (visible) R.drawable.visibility_off else R.drawable.visibility
                        ),
                        contentDescription = null,
                        tint = Color.Gray
                    )
                }
            }
        } else null,
        visualTransformation =
            if (isPassword && !visible) PasswordVisualTransformation()
            else VisualTransformation.None,
        shape = RoundedCornerShape(35.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(55.dp)
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewRecruiterSignup() {
    val context = LocalContext.current
    val fakeAuthApi = object : AuthApi {
        override suspend fun login(body: LoginRequest): LoginResponse { TODO("Not yet implemented") }
        override suspend fun signupTalent(body: TalentSignupRequest): LoginResponse { TODO("Not yet implemented") }
        override suspend fun signupRecruiter(body: RecruiterSignupRequest): LoginResponse { TODO("Not yet implemented") }
        override suspend fun forgotPassword(body: ForgotPasswordRequest): ForgotPasswordResponse { TODO("Not yet implemented") }
        override suspend fun verifyResetCode(body: VerifyResetCodeRequest): VerifyResetCodeResponse { TODO("Not yet implemented") }
        override suspend fun resetPassword(body: ResetPasswordRequest): ResetPasswordResponse { TODO("Not yet implemented") }
    }
    val dummyViewModel = RecruiterSignupViewModel(
        AuthRepository(fakeAuthApi),
        AuthPreferences(context)
    )

    RecruiterSignupScreen(
        onLoginClick = {},
        onSignupSuccess = {},
        viewModel = dummyViewModel
    )
}