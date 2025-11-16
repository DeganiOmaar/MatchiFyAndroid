package com.example.matchify.ui.auth.signup.talent

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
fun TalentSignupScreen(
    onLogin: () -> Unit,
    onSuccess: () -> Unit,
    viewModel: TalentSignupViewModel // Accept ViewModel as a parameter
) {
    val fullName by viewModel.fullName.collectAsState()
    val email by viewModel.email.collectAsState()
    val phone by viewModel.phone.collectAsState()
    val location by viewModel.location.collectAsState()
    val talent by viewModel.talent.collectAsState()
    val password by viewModel.password.collectAsState()
    val confirmPassword by viewModel.confirmPassword.collectAsState()

    val showPassword by viewModel.showPassword.collectAsState()
    val showConfirmPassword by viewModel.showConfirmPassword.collectAsState()

    val error by viewModel.error.collectAsState()
    val loading by viewModel.loading.collectAsState()
    val navigateTo by viewModel.navigateTo.collectAsState()

    // Navigate when signup succeeds
    LaunchedEffect(navigateTo) {
        navigateTo?.let {
            onSuccess()
            viewModel.onNavigationDone()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            "Sign Up Talent",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Text(
            "Create your talent profile",
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(30.dp))

        // INPUTS
        AuthTextField("Full Name", fullName, Icons.Default.Person) { viewModel.setFullName(it) }
        Spacer(modifier = Modifier.height(16.dp))

        AuthTextField("Email", email, Icons.Default.Email) { viewModel.setEmail(it) }
        Spacer(modifier = Modifier.height(16.dp))

        AuthTextField("Phone", phone, Icons.Default.Phone) { viewModel.setPhone(it) }
        Spacer(modifier = Modifier.height(16.dp))

        AuthTextField("Location", location, Icons.Default.LocationOn) { viewModel.setLocation(it) }
        Spacer(modifier = Modifier.height(16.dp))

        AuthTextField("Talent", talent, Icons.Default.Star) { viewModel.setTalent(it) }
        Spacer(modifier = Modifier.height(16.dp))

        // PASSWORD
        PasswordField(
            label = "Password",
            value = password,
            visible = showPassword,
            onValueChange = { viewModel.setPassword(it) },
            onToggle = { viewModel.togglePasswordVisibility() }
        )

        Spacer(modifier = Modifier.height(16.dp))

        PasswordField(
            label = "Confirm Password",
            value = confirmPassword,
            visible = showConfirmPassword,
            onValueChange = { viewModel.setConfirmPassword(it) },
            onToggle = { viewModel.toggleConfirmPasswordVisibility() }
        )

        Spacer(modifier = Modifier.height(10.dp))

        if (error != null) {
            Text(text = error!!, color = Color.Red)
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = { viewModel.signUp() },
            modifier = Modifier
                .fillMaxWidth()
                .height(55.dp),
            shape = RoundedCornerShape(30.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF007AFF),
                disabledContainerColor = Color(0xFFBAD7FF)
            )
        ) {
            if (loading) {
                CircularProgressIndicator(color = Color.White)
            } else {
                Text("Sign Up", color = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
            Text("Already have an account? ", color = Color.Gray)
            Text(
                "Login",
                color = Color(0xFF007AFF),
                modifier = Modifier.clickable { onLogin() }
            )
        }
    }
}

/* ---------- Reusable Fields ---------- */

@Composable
fun AuthTextField(
    label: String,
    value: String,
    icon: Any,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(label) },
        leadingIcon = {
            when (icon) {
                is Int -> Icon(painterResource(icon), null)
                else -> Icon(icon as androidx.compose.ui.graphics.vector.ImageVector, null)
            }
        },
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(30.dp),
        singleLine = true
    )
}

@Composable
fun PasswordField(
    label: String,
    value: String,
    visible: Boolean,
    onValueChange: (String) -> Unit,
    onToggle: () -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(label) },
        leadingIcon = { Icon(Icons.Default.Lock, null) },
        trailingIcon = {
            IconButton(onClick = onToggle) {
                Icon(
                    painterResource(
                        id = if (visible) R.drawable.visibility_off else R.drawable.visibility
                    ),
                    null
                )
            }
        },
        visualTransformation =
            if (visible) VisualTransformation.None else PasswordVisualTransformation(),
        singleLine = true,
        shape = RoundedCornerShape(30.dp),
        modifier = Modifier.fillMaxWidth()
    )
}

@Preview(showBackground = true)
@Composable
fun TalentSignupPreview() {
    val context = LocalContext.current
    val fakeAuthApi = object : AuthApi {
        override suspend fun login(body: LoginRequest): LoginResponse { TODO("Not yet implemented") }
        override suspend fun signupTalent(body: TalentSignupRequest): LoginResponse { TODO("Not yet implemented") }
        override suspend fun signupRecruiter(body: RecruiterSignupRequest): LoginResponse { TODO("Not yet implemented") }
        override suspend fun forgotPassword(body: ForgotPasswordRequest): ForgotPasswordResponse { TODO("Not yet implemented") }
        override suspend fun verifyResetCode(body: VerifyResetCodeRequest): VerifyResetCodeResponse { TODO("Not yet implemented") }
        override suspend fun resetPassword(body: ResetPasswordRequest): ResetPasswordResponse { TODO("Not yet implemented") }
    }
    val dummyViewModel = TalentSignupViewModel(
        AuthRepository(fakeAuthApi),
        AuthPreferences(context)
    )

    TalentSignupScreen(
        onLogin = {},
        onSuccess = {},
        viewModel = dummyViewModel
    )
}