package com.example.matchify.ui.auth.forgot

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Spacer(modifier = Modifier.height(40.dp))

        Text(
            text = "Reset your password",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "Please enter your email and we will send an OTP\ncode in the next step to reset your password",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 8.dp)
        )

        Spacer(modifier = Modifier.height(40.dp))

        // EMAIL - MD3 Outlined Text Field
        MD3OutlinedTextField(
            value = email,
            onValueChange = { viewModel.setEmail(it) },
            label = "Email Address",
            placeholder = "Enter your email",
            leadingIcon = Icons.Default.Email,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth(),
            errorText = error,
            singleLine = true
        )

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = {
                if (email.isNotEmpty()) viewModel.sendCode()
            },
            enabled = email.isNotEmpty() && !isLoading,
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .padding(bottom = 30.dp),
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
                    "Continue",
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

