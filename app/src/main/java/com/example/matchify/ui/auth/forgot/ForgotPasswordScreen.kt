package com.example.matchify.ui.auth.forgot

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgotPasswordScreen(
    onCodeSent: (String) -> Unit,
    onBackToLogin: () -> Unit,
    viewModel: ForgotPasswordViewModel // Accept ViewModel as a parameter
) {
    val email by viewModel.email.collectAsState()
    val error by viewModel.error.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val codeSent by viewModel.codeSent.collectAsState()

    LaunchedEffect(codeSent) {
        if (codeSent) onCodeSent(email)
    }

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
                    onValueChange = { viewModel.setEmail(it) },
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
                error?.let { errorMessage ->
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = errorMessage,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // CONTINUE BUTTON
            Button(
                onClick = {
                    if (email.isNotEmpty()) viewModel.sendCode()
                },
                enabled = email.isNotEmpty() && !isLoading,
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
                        text = "Continue",
                        color = whiteColor,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // BOTTOM LOGIN TEXT
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 32.dp)
            ) {
                Text(
                    text = "Remember your password? ",
                    color = textColor,
                    fontSize = 14.sp
                )
                Text(
                    text = "Login",
                    color = blueColor,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.clickable { onBackToLogin() }
                )
            }
        }
    }
}

