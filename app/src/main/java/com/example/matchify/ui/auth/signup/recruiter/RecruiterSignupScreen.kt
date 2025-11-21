package com.example.matchify.ui.auth.signup.recruiter

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.matchify.R
import com.example.matchify.ui.components.MD3OutlinedTextField

@Composable
fun RecruiterSignupScreen(
    onBack: () -> Unit = {},
    onLoginClick: () -> Unit,
    onSignupSuccess: () -> Unit,
    viewModel: RecruiterSignupViewModel
) {
    val fullName by viewModel.fullName.collectAsState()
    val email by viewModel.email.collectAsState()
    val password by viewModel.password.collectAsState()
    val confirmPassword by viewModel.confirmPassword.collectAsState()
    val showPassword by viewModel.showPassword.collectAsState()
    val showConfirmPassword by viewModel.showConfirmPassword.collectAsState()
    val error by viewModel.error.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val navigateTo by viewModel.navigateTo.collectAsState()

    LaunchedEffect(navigateTo) {
        navigateTo?.let {
            onSignupSuccess()
            viewModel.onNavigationDone()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Back button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

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
            text = "Sign Up Recruiter",
            fontWeight = FontWeight.SemiBold,
            fontSize = MaterialTheme.typography.titleLarge.fontSize,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Left,
            modifier = Modifier.padding(top = 4.dp).fillMaxWidth()
        )

        Text(
            text = "Create your recruiter profile",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = MaterialTheme.typography.bodyMedium.fontSize,
            textAlign = TextAlign.Left,
            modifier = Modifier.padding(top = 4.dp).fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(32.dp))

        // FULL NAME - MD3 Outlined Text Field
        MD3OutlinedTextField(
            value = fullName,
            onValueChange = { viewModel.setFullName(it) },
            label = "Full Name",
            placeholder = "Full Name",
            leadingIcon = Icons.Default.Person,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            modifier = Modifier.fillMaxWidth(),
            errorText = null,
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        // EMAIL - MD3 Outlined Text Field
        MD3OutlinedTextField(
            value = email,
            onValueChange = { viewModel.setEmail(it) },
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
            onValueChange = { viewModel.setPassword(it) },
            label = "Password",
            placeholder = "Password",
            leadingIcon = Icons.Default.Lock,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth(),
            errorText = null,
            isPassword = true,
            showPassword = showPassword,
            onPasswordToggle = { viewModel.toggleShowPassword() },
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        // CONFIRM PASSWORD - MD3 Outlined Text Field with password toggle
        MD3OutlinedTextField(
            value = confirmPassword,
            onValueChange = { viewModel.setConfirmPassword(it) },
            label = "Confirm Password",
            placeholder = "Confirm Password",
            leadingIcon = Icons.Default.Lock,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth(),
            errorText = if (error != null && (password != confirmPassword || confirmPassword.isEmpty())) error else null,
            isPassword = true,
            showPassword = showConfirmPassword,
            onPasswordToggle = { viewModel.toggleShowConfirmPassword() },
            singleLine = true
        )

        Spacer(modifier = Modifier.height(10.dp))

        Spacer(modifier = Modifier.height(10.dp))

        // SIGNUP BUTTON
        Button(
            onClick = { viewModel.signUp() },
            enabled = !isLoading && fullName.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty() && confirmPassword.isNotEmpty(),
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
                    "Sign Up",
                    color = Color.White
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Row {
            Text(
                "Already have an account? ",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                "Login",
                color = Color(0xFF007AFF),
                fontWeight = FontWeight.Medium,
                modifier = Modifier.clickable { onLoginClick() }
            )
        }
    }
}
