package com.example.matchify.ui.auth.login

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

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
@OptIn(ExperimentalMaterial3Api::class)
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
    
    // Design colors from reference
    val darkBackground = Color(0xFF0F172A)
    val fieldBackground = Color(0xFF1E293B)
    val textColor = Color(0xFFCBD5E1)
    val blueColor = Color(0xFF3B82F6)
    val whiteColor = Color(0xFFFFFFFF)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(darkBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            // EMAIL FIELD
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Email",
                    color = textColor,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                TextField(
                    value = email,
                    onValueChange = onEmailChange,
                    placeholder = {
                        Text(
                            text = "Enter your email",
                            color = textColor.copy(alpha = 0.6f)
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = TextFieldDefaults.colors(
                        focusedTextColor = textColor,
                        unfocusedTextColor = textColor,
                        focusedPlaceholderColor = textColor.copy(alpha = 0.6f),
                        unfocusedPlaceholderColor = textColor.copy(alpha = 0.6f),
                        focusedContainerColor = fieldBackground,
                        unfocusedContainerColor = fieldBackground,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent
                    ),
                    shape = RoundedCornerShape(8.dp),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    textStyle = androidx.compose.ui.text.TextStyle(
                        color = textColor,
                        fontSize = 16.sp
                    )
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // PASSWORD FIELD
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Password",
                    color = textColor,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                TextField(
                    value = password,
                    onValueChange = onPasswordChange,
                    placeholder = {
                        Text(
                            text = "Enter your password",
                            color = textColor.copy(alpha = 0.6f)
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = TextFieldDefaults.colors(
                        focusedTextColor = textColor,
                        unfocusedTextColor = textColor,
                        focusedPlaceholderColor = textColor.copy(alpha = 0.6f),
                        unfocusedPlaceholderColor = textColor.copy(alpha = 0.6f),
                        focusedContainerColor = fieldBackground,
                        unfocusedContainerColor = fieldBackground,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent,
                        focusedTrailingIconColor = textColor,
                        unfocusedTrailingIconColor = textColor
                    ),
                    shape = RoundedCornerShape(8.dp),
                    singleLine = true,
                    visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    trailingIcon = {
                        IconButton(onClick = onTogglePasswordVisibility) {
                            Icon(
                                imageVector = if (showPassword) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                                contentDescription = if (showPassword) "Hide password" else "Show password",
                                tint = textColor
                            )
                        }
                    },
                    textStyle = androidx.compose.ui.text.TextStyle(
                        color = textColor,
                        fontSize = 16.sp
                    )
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // REMEMBER ME + RESET PASSWORD ROW
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { rememberMe = !rememberMe }
                ) {
                    Checkbox(
                        checked = rememberMe,
                        onCheckedChange = { rememberMe = it },
                        colors = CheckboxDefaults.colors(
                            checkedColor = blueColor,
                            uncheckedColor = textColor.copy(alpha = 0.6f)
                        )
                    )
                    Text(
                        text = "Remember me",
                        color = textColor,
                        fontSize = 14.sp
                    )
                }

                Text(
                    text = "Reset Password",
                    color = blueColor,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.clickable { onForgotPassword() }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ERROR MESSAGE
            if (error != null) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFFDC2626).copy(alpha = 0.1f)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = error,
                            color = Color(0xFFEF4444),
                            fontSize = 14.sp,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // LOGIN BUTTON
            Button(
                onClick = { onLoginClick(rememberMe) },
                enabled = !isLoading && email.isNotEmpty() && password.isNotEmpty(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = blueColor,
                    disabledContainerColor = blueColor.copy(alpha = 0.5f),
                    contentColor = whiteColor,
                    disabledContentColor = whiteColor.copy(alpha = 0.7f)
                )
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = whiteColor,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(20.dp)
                    )
                } else {
                    Text(
                        text = "Login",
                        color = whiteColor,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // BOTTOM SIGN UP TEXT
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 32.dp)
            ) {
                Text(
                    text = "Don't have an account? ",
                    color = textColor,
                    fontSize = 14.sp
                )
                Text(
                    text = "Sign Up",
                    color = blueColor,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.clickable { onSignUp() }
                )
            }
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
