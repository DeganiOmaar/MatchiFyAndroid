package com.example.matchify.ui.auth.verify

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.draw.alpha
import com.example.matchify.data.remote.AuthApi
import com.example.matchify.data.remote.AuthRepository
import com.example.matchify.data.remote.dto.auth.*

@Composable
fun VerifyCodeScreen(
    email: String,
    onVerified: () -> Unit,
    viewModel: VerifyCodeViewModel // Accept ViewModel as a parameter
) {
    val code by viewModel.code.collectAsState()
    val loading by viewModel.loading.collectAsState()
    val success by viewModel.success.collectAsState()
    val error by viewModel.error.collectAsState()

    LaunchedEffect(success) {
        if (success) onVerified()
    }

    VerifyCodeScreenContent(
        email = email,
        code = code,
        loading = loading,
        error = error,
        onCodeChange = { viewModel.updateCode(it) },
        onVerifyClick = { viewModel.verify() }
    )
}

@Composable
fun VerifyCodeScreenContent(
    email: String,
    code: String,
    loading: Boolean,
    error: String?,
    onCodeChange: (String) -> Unit,
    onVerifyClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.Start
    ) {

        Spacer(modifier = Modifier.height(40.dp))

        // TITLE
        Text(
            text = "OTP code verification",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        // SUBTITLE
        Text(
            text = "A verification code has been sent to: ",
            color = Color.Gray
        )

        Text(
            text = email,
            color = Color(0xFF007AFF),
            fontWeight = FontWeight.Medium
        )

        Text(
            text = " Enter the OTP code below to verify",
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(40.dp))

        // OTP BOXES
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            repeat(6) { index ->
                OtpBox(
                    char = code.getOrNull(index)?.toString() ?: ""
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // HIDDEN TEXT FIELD (receives keyboard input)
        OutlinedTextField(
            value = code,
            onValueChange = { onCodeChange(it.filter { c -> c.isDigit() }) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            visualTransformation = VisualTransformation.None,
            modifier = Modifier
                .width(1.dp)
                .height(0.dp)
                .alpha(0.5f)
        )

        if (error != null) {
            Spacer(modifier = Modifier.height(10.dp))
            Text(error, color = Color.Red)
        }

        Spacer(modifier = Modifier.weight(1f))

        // CONTINUE BUTTON (iOS Blue)
        Button(
            onClick = onVerifyClick,
            enabled = code.length == 6 && !loading,
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
            if (loading) {
                CircularProgressIndicator(color = Color.White, strokeWidth = 2.dp)
            } else {
                Text("Continue", color = Color.White, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
fun OtpBox(char: String) {
    Box(
        modifier = Modifier
            .size(48.dp)
            .background(Color(0xFFF3F3F7), RoundedCornerShape(10.dp))
            .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(10.dp)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = char,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Medium
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewVerifyCode() {
    VerifyCodeScreenContent(
        email = "amroush123@gmail.com",
        code = "123",
        loading = false,
        error = "Invalid code",
        onCodeChange = {},
        onVerifyClick = {}
    )
}
