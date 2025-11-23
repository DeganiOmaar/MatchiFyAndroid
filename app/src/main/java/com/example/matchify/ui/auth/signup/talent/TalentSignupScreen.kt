package com.example.matchify.ui.auth.signup.talent

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.matchify.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TalentSignupScreen(
    onBack: () -> Unit = {},
    onLogin: () -> Unit,
    onSuccess: () -> Unit,
    viewModel: TalentSignupViewModel
) {
    val fullName by viewModel.fullName.collectAsState()
    val email by viewModel.email.collectAsState()
    val phone by viewModel.phone.collectAsState()
    val password by viewModel.password.collectAsState()
    val confirmPassword by viewModel.confirmPassword.collectAsState()
    val showPassword by viewModel.showPassword.collectAsState()
    val showConfirmPassword by viewModel.showConfirmPassword.collectAsState()
    val error by viewModel.error.collectAsState()
    val loading by viewModel.loading.collectAsState()
    val navigateTo by viewModel.navigateTo.collectAsState()

    LaunchedEffect(navigateTo) {
        navigateTo?.let {
            onSuccess()
            viewModel.onNavigationDone()
        }
    }

    val backgroundColor = Color(0xFF61A5C2)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Sign Up Talent",
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = backgroundColor
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .verticalScroll(rememberScrollState())
                .padding(paddingValues)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
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
                text = "Create your talent profile",
                color = Color.Gray,
                fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

        Spacer(modifier = Modifier.height(32.dp))

        // FULL NAME
        OutlinedTextField(
            value = fullName,
            onValueChange = { viewModel.setFullName(it) },
            leadingIcon = {
                Icon(Icons.Default.Person, contentDescription = null, tint = Color.Gray)
            },
            placeholder = { Text("Full Name") },
            singleLine = true,
            shape = RoundedCornerShape(35.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(55.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFFCCCCCC),
                unfocusedBorderColor = Color(0xFFDDDDDD)
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // EMAIL
        OutlinedTextField(
            value = email,
            onValueChange = { viewModel.setEmail(it) },
            leadingIcon = {
                Icon(Icons.Default.Email, contentDescription = null, tint = Color.Gray)
            },
            placeholder = { Text("Email") },
            singleLine = true,
            shape = RoundedCornerShape(35.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(55.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFFCCCCCC),
                unfocusedBorderColor = Color(0xFFDDDDDD)
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // PHONE - Only accept digits
        OutlinedTextField(
            value = phone,
            onValueChange = { newValue ->
                // Filter to only allow digits
                val filtered = newValue.filter { it.isDigit() }
                viewModel.setPhone(filtered)
            },
            leadingIcon = {
                Icon(Icons.Default.Phone, contentDescription = null, tint = Color.Gray)
            },
            placeholder = { Text("Phone") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            shape = RoundedCornerShape(35.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(55.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFFCCCCCC),
                unfocusedBorderColor = Color(0xFFDDDDDD)
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // PASSWORD
        OutlinedTextField(
            value = password,
            onValueChange = { viewModel.setPassword(it) },
            leadingIcon = {
                Icon(Icons.Default.Lock, contentDescription = null, tint = Color.Gray)
            },
            placeholder = { Text("Password") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            visualTransformation =
            if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { viewModel.togglePasswordVisibility() }) {
                    Icon(
                        painter = painterResource(
                            id = if (showPassword) R.drawable.visibility else R.drawable.visibility_off
                        ),
                        contentDescription = null,
                        tint = Color.Gray
                    )
                }
            },
            shape = RoundedCornerShape(35.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(55.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFFCCCCCC),
                unfocusedBorderColor = Color(0xFFDDDDDD)
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // CONFIRM PASSWORD
        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { viewModel.setConfirmPassword(it) },
            leadingIcon = {
                Icon(Icons.Default.Lock, contentDescription = null, tint = Color.Gray)
            },
            placeholder = { Text("Confirm Password") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            visualTransformation =
            if (showConfirmPassword) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { viewModel.toggleConfirmPasswordVisibility() }) {
                    Icon(
                        painter = painterResource(
                            id = if (showConfirmPassword) R.drawable.visibility else R.drawable.visibility_off
                        ),
                        contentDescription = null,
                        tint = Color.Gray
                    )
                }
            },
            shape = RoundedCornerShape(35.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(55.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFFCCCCCC),
                unfocusedBorderColor = Color(0xFFDDDDDD)
            )
        )

        Spacer(modifier = Modifier.height(10.dp))

        // ERROR
        error?.let {
            Text(
                text = it,
                color = Color.Red,
                fontSize = MaterialTheme.typography.bodySmall.fontSize,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Spacer(modifier = Modifier.height(10.dp))

        // SIGNUP BUTTON
        Button(
            onClick = { viewModel.signUp() },
            enabled = !loading && fullName.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty() && confirmPassword.isNotEmpty(),
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
                CircularProgressIndicator(color = Color.White, strokeWidth = 2.dp)
            } else {
                Text("Sign Up", color = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Row {
            Text("Already have an account? ", color = Color.Gray)
            Text(
                "Login",
                color = Color(0xFF007AFF),
                fontWeight = FontWeight.Medium,
                modifier = Modifier.clickable { onLogin() }
            )
        }

        Spacer(modifier = Modifier.height(40.dp))
    }
}
