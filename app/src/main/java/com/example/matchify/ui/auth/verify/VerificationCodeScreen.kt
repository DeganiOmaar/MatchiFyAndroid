package com.example.matchify.ui.auth.verify

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VerifyCodeScreenContent(
    email: String,
    code: String,
    loading: Boolean,
    error: String?,
    onCodeChange: (String) -> Unit,
    onVerifyClick: () -> Unit
) {
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

            // TITLE
            Text(
                text = "OTP code verification",
                color = textColor,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            // SUBTITLE
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start
            ) {
                Text(
                    text = "A verification code has been sent to: ",
                    color = textColor,
                    fontSize = 14.sp
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start
            ) {
                Text(
                    text = email,
                    color = blueColor,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = ". Enter the OTP code below to verify",
                    color = textColor,
                    fontSize = 14.sp
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            // OTP BOXES
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                repeat(6) { index ->
                    OtpBox(
                        char = code.getOrNull(index)?.toString() ?: "",
                        textColor = textColor,
                        fieldBackground = fieldBackground
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
                    .alpha(0f)
            )

            error?.let { errorMessage ->
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 12.sp
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // CONTINUE BUTTON
            Button(
                onClick = onVerifyClick,
                enabled = code.length == 6 && !loading,
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
                if (loading) {
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

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun OtpBox(char: String, textColor: Color, fieldBackground: Color) {
    Box(
        modifier = Modifier
            .size(56.dp)
            .background(
                fieldBackground,
                RoundedCornerShape(8.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = char,
            fontSize = 24.sp,
            color = textColor,
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
