package com.example.matchify.ui.auth.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.matchify.R
import com.example.matchify.ui.components.MD3OutlinedTextField

/**
 * Stateful Composable: Manages state and passes it to the stateless UI.
 */
@Composable
fun LoginScreen(
    onForgotPassword: () -> Unit,
    onSignUp: () -> Unit,
    onLoginSuccess: (String) -> Unit,
    viewModel: LoginViewModel // Accepts ViewModel from the NavGraph
) {
    val email by viewModel.email.collectAsState()
    val password by viewModel.password.collectAsState()
    val showPassword by viewModel.showPassword.collectAsState()
    val error by viewModel.error.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val navigateTo by viewModel.navigateTo.collectAsState()

    LaunchedEffect(navigateTo) {
        navigateTo?.let {
            onLoginSuccess(it)
            viewModel.onNavigationDone()
        }
    }

    LoginScreenContent(
        email = email,
        password = password,
        showPassword = showPassword,
        isLoading = isLoading,
        error = error,
        onEmailChange = { viewModel.setEmail(it) },
        onPasswordChange = { viewModel.setPassword(it) },
        onTogglePasswordVisibility = { viewModel.togglePasswordVisibility() },
        onLoginClick = { rememberMe -> viewModel.login(rememberMe) },
        onForgotPassword = onForgotPassword,
        onSignUp = onSignUp
    )
}

/**
 * Stateless Composable: Pure UI, receives all state and events as parameters.
 * This makes it easy to preview and reuse.
 */
@Composable
fun LoginScreenContent(
    email: String,
    password: String,
    showPassword: Boolean,
    isLoading: Boolean,
    error: String?,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onTogglePasswordVisibility: () -> Unit,
    onLoginClick: (Boolean) -> Unit,
    onForgotPassword: () -> Unit,
    onSignUp: () -> Unit
) {
    var rememberMe by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(modifier = Modifier.height(80.dp))

        // LOGO
        Image(
            painter = painterResource(id = R.drawable.matchifylogo),
            contentDescription = "App Logo",
            modifier = Modifier
                .size(170.dp)
                .padding(top = 10.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Connecting Talent With Opportunity",
            fontWeight = FontWeight.SemiBold,
            fontSize = MaterialTheme.typography.titleLarge.fontSize,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Left,
            modifier = Modifier.padding(top = 4.dp).fillMaxWidth()
        )

        Text(
            text = "Please sign in to continue",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = MaterialTheme.typography.bodyMedium.fontSize,
            textAlign = TextAlign.Left,
            modifier = Modifier.padding(top = 4.dp).fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(32.dp))

        // EMAIL - MD3 Outlined Text Field
        MD3OutlinedTextField(
            value = email,
            onValueChange = onEmailChange,
            label = "Email",
            placeholder = "Email",
            leadingIcon = Icons.Default.Email,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth(),
            errorText = null,
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        // PASSWORD - MD3 Outlined Text Field with password toggle
        MD3OutlinedTextField(
            value = password,
            onValueChange = onPasswordChange,
            label = "Password",
            placeholder = "Password",
            leadingIcon = Icons.Default.Lock,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth(),
            errorText = error,
            isPassword = true,
            showPassword = showPassword,
            onPasswordToggle = onTogglePasswordVisibility,
            singleLine = true
        )

        Spacer(modifier = Modifier.height(10.dp))

        // REMEMBER + PASSWORD RESET
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable { rememberMe = !rememberMe }
            ) {
                Checkbox(
                    checked = rememberMe,
                    onCheckedChange = { rememberMe = it }
                )
                Text(
                    "Remember Me",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = "Reset Password",
                color = Color(0xFF007AFF),
                fontWeight = FontWeight.Medium,
                modifier = Modifier.clickable { onForgotPassword() }
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // LOGIN BUTTON
        Button(
            onClick = { onLoginClick(rememberMe) },
            enabled = !isLoading && email.isNotEmpty() && password.isNotEmpty(),
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
                CircularProgressIndicator(
                    color = Color.White,
                    strokeWidth = 2.dp
                )
            } else {
                Text(
                    "Login",
                    color = Color.White
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Row {
            Text(
                "Don't have an account? ",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                "Sign Up",
                color = Color(0xFF007AFF),
                fontWeight = FontWeight.Medium,
                modifier = Modifier.clickable { onSignUp() }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    // Call the stateless composable with dummy data for the preview.
    LoginScreenContent(
        email = "hello@world.com",
        password = "12345",
        showPassword = false,
        isLoading = false,
        error = null,
        onEmailChange = {},
        onPasswordChange = {},
        onTogglePasswordVisibility = {},
        onLoginClick = {},
        onForgotPassword = {},
        onSignUp = {}
    )
}
