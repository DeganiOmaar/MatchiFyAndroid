package com.example.matchify.ui.auth.login

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.matchify.R

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

    // Couleur de fond bleu clair
    val backgroundColor = Color(0xFF61A5C2)

    // Animation infinie pour le background
    val infiniteTransition = rememberInfiniteTransition(label = "background_animation")

    // Animation pour les cercles flottants
    val circle1Y by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "circle1_y"
    )

    val circle2Y by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "circle2_y"
    )

    val circle3Y by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(3500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "circle3_y"
    )

    val circle4Y by infiniteTransition.animateFloat(
        initialValue = 0.7f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(4500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "circle4_y"
    )

    val circle5Y by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(3800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "circle5_y"
    )

    // Animation pour la taille des cercles
    val circle1Scale by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "circle1_scale"
    )

    val circle2Scale by infiniteTransition.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "circle2_scale"
    )

    val circle3Scale by infiniteTransition.animateFloat(
        initialValue = 0.7f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "circle3_scale"
    )

    val circle4Scale by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(3200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "circle4_scale"
    )

    val circle5Scale by infiniteTransition.animateFloat(
        initialValue = 0.75f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(2700, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "circle5_scale"
    )

    // Animation pour l'opacité des cercles
    val circle1Alpha by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 0.4f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "circle1_alpha"
    )

    val circle2Alpha by infiniteTransition.animateFloat(
        initialValue = 0.15f,
        targetValue = 0.35f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "circle2_alpha"
    )

    val circle3Alpha by infiniteTransition.animateFloat(
        initialValue = 0.25f,
        targetValue = 0.45f,
        animationSpec = infiniteRepeatable(
            animation = tween(2100, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "circle3_alpha"
    )

    val circle4Alpha by infiniteTransition.animateFloat(
        initialValue = 0.18f,
        targetValue = 0.38f,
        animationSpec = infiniteRepeatable(
            animation = tween(2300, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "circle4_alpha"
    )

    val circle5Alpha by infiniteTransition.animateFloat(
        initialValue = 0.22f,
        targetValue = 0.42f,
        animationSpec = infiniteRepeatable(
            animation = tween(1900, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "circle5_alpha"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        // Background animé avec cercles flottants
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height

            // Cercle 1
            drawCircle(
                color = Color.White.copy(alpha = circle1Alpha),
                radius = (width * 0.15f) * circle1Scale,
                center = Offset(width * 0.2f, height * circle1Y),
                style = Stroke(width = 2.dp.toPx())
            )

            // Cercle 2
            drawCircle(
                color = Color.White.copy(alpha = circle2Alpha),
                radius = (width * 0.12f) * circle2Scale,
                center = Offset(width * 0.8f, height * circle2Y),
                style = Stroke(width = 2.dp.toPx())
            )

            // Cercle 3
            drawCircle(
                color = Color.White.copy(alpha = circle3Alpha),
                radius = (width * 0.1f) * circle3Scale,
                center = Offset(width * 0.5f, height * circle3Y),
                style = Stroke(width = 2.dp.toPx())
            )

            // Cercle 4
            drawCircle(
                color = Color.White.copy(alpha = circle4Alpha),
                radius = (width * 0.13f) * circle4Scale,
                center = Offset(width * 0.15f, height * circle4Y),
                style = Stroke(width = 2.dp.toPx())
            )

            // Cercle 5
            drawCircle(
                color = Color.White.copy(alpha = circle5Alpha),
                radius = (width * 0.11f) * circle5Scale,
                center = Offset(width * 0.85f, height * circle5Y),
                style = Stroke(width = 2.dp.toPx())
            )
        }

        // Card avec le contenu du login
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 20.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 8.dp
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
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
                        text = "Connecting Talent With Opportunity",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = MaterialTheme.typography.titleLarge.fontSize,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Please sign in to continue",
                        color = Color.Gray,
                        fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    // EMAIL
                    OutlinedTextField(
                        value = email,
                        onValueChange = onEmailChange,
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

                    // PASSWORD
                    OutlinedTextField(
                        value = password,
                        onValueChange = onPasswordChange,
                        leadingIcon = {
                            Icon(Icons.Default.Lock, contentDescription = null, tint = Color.Gray)
                        },
                        placeholder = { Text("Password") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        visualTransformation =
                        if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = onTogglePasswordVisibility) {
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
                            Text("Remember Me", color = Color.Gray)
                        }

                        Spacer(modifier = Modifier.weight(1f))

                        Text(
                            text = "Forget Password",
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
                            CircularProgressIndicator(color = Color.White, strokeWidth = 2.dp)
                        } else {
                            Text("Login", color = Color.White)
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Row {
                        Text("Don't have an account? ", color = Color.Gray)
                        Text(
                            "Sign Up",
                            color = Color(0xFF007AFF),
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.clickable { onSignUp() }
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))
                }
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
