package com.example.matchify.ui.auth.chooserole

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.matchify.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChooseRoleScreen(
    onTalentSelected: () -> Unit,
    onRecruiterSelected: () -> Unit,
    onBack: () -> Unit = {}
) {
    val viewModel: ChooseRoleViewModel = viewModel()

    val selectedRole by viewModel.selectedRole.collectAsState()
    val goNext by viewModel.goNext.collectAsState()

    // Navigate when Next is triggered
    LaunchedEffect(goNext) {
        if (goNext) {
            when (selectedRole) {
                UserRole.Talent -> onTalentSelected()
                UserRole.Recruiter -> onRecruiterSelected()
                else -> {}
            }
        }
    }

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

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Choose Your Role",
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor)
                .padding(paddingValues)
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

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                // Subtitle
                Text(
                    text = "Select the account type that suits you best.",
                    color = Color.White,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )

                Spacer(modifier = Modifier.height(30.dp))

                // Talent Card
                RoleCard(
                    title = "Talent",
                    subtitle = "For creators, artists, influencers, freelancers.",
                    image = R.drawable.talent,
                    isSelected = selectedRole == UserRole.Talent,
                    onClick = { viewModel.selectRole(UserRole.Talent) }
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Recruiter Card
                RoleCard(
                    title = "Recruiter",
                    subtitle = "For companies or individuals hiring talent.",
                    image = R.drawable.recruiter,
                    isSelected = selectedRole == UserRole.Recruiter,
                    onClick = { viewModel.selectRole(UserRole.Recruiter) }
                )

                Spacer(modifier = Modifier.weight(1f))

                // CONTINUE BUTTON
                Button(
                    onClick = { viewModel.continueNext() },
                    enabled = selectedRole != null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(55.dp),
                    shape = RoundedCornerShape(30.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF007AFF),
                        disabledContainerColor = Color(0xFFBAD7FF)
                    ),
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = 8.dp,
                        pressedElevation = 4.dp
                    )
                ) {
                    Text(
                        text = "Continue",
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = MaterialTheme.typography.titleMedium.fontSize
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun RoleCard(
    title: String,
    subtitle: String,
    image: Int,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = if (isSelected) Color.White else Color.White.copy(alpha = 0.9f)
    val borderColor = if (isSelected) Color(0xFF61A5C2) else Color.Transparent
    val borderWidth = if (isSelected) 2.dp else 0.dp

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 8.dp else 4.dp,
            pressedElevation = 6.dp
        ),
        border = androidx.compose.foundation.BorderStroke(
            width = borderWidth,
            color = borderColor
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Image(
                painter = painterResource(id = image),
                contentDescription = null,
                modifier = Modifier.size(48.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ChooseRolePreview() {
    ChooseRoleScreen(
        onTalentSelected = {},
        onRecruiterSelected = {}
    )
}