package com.example.matchify.ui.onboarding

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.matchify.R
import com.example.matchify.data.local.AuthPreferencesProvider
import kotlinx.coroutines.launch

@Composable
fun OnboardingScreen(
    onComplete: () -> Unit,
    viewModel: OnboardingViewModel = viewModel(
        factory = OnboardingViewModelFactory(
            AuthPreferencesProvider.getInstance().get()
        )
    )
) {
    val currentPage by viewModel.currentPage.collectAsState()
    val pagerState = rememberPagerState(pageCount = { 3 }, initialPage = 0)
    val coroutineScope = rememberCoroutineScope()

    // Sync ViewModel with pager state (when user swipes)
    LaunchedEffect(pagerState.currentPage) {
        if (pagerState.currentPage != currentPage) {
            viewModel.setCurrentPage(pagerState.currentPage)
        }
    }
    
    // Sync pager state with ViewModel (when navigating via buttons)
    LaunchedEffect(currentPage) {
        if (pagerState.currentPage != currentPage) {
            pagerState.animateScrollToPage(currentPage)
        }
    }

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
    
    val circle3X by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(5000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "circle3_x"
    )
    
    val circle1Scale by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "circle1_scale"
    )
    
    val circle2Scale by infiniteTransition.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "circle2_scale"
    )
    
    val circle3Scale by infiniteTransition.animateFloat(
        initialValue = 0.7f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "circle3_scale"
    )
    
    val circle1Alpha by infiniteTransition.animateFloat(
        initialValue = 0.15f,
        targetValue = 0.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "circle1_alpha"
    )
    
    val circle2Alpha by infiniteTransition.animateFloat(
        initialValue = 0.1f,
        targetValue = 0.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "circle2_alpha"
    )
    
    val circle3Alpha by infiniteTransition.animateFloat(
        initialValue = 0.12f,
        targetValue = 0.28f,
        animationSpec = infiniteRepeatable(
            animation = tween(1900, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "circle3_alpha"
    )
    
    val backgroundColor = Color(0xFF61A5C2)
    
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Background animé avec cercles flottants
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor)
        ) {
            // Cercle 1 - En haut à gauche, se déplace verticalement
            Box(
                modifier = Modifier
                    .offset(
                        x = (-50).dp,
                        y = (circle1Y * 200).dp - 50.dp
                    )
                    .size((150 * circle1Scale).dp)
                    .background(
                        color = Color.White.copy(alpha = circle1Alpha),
                        shape = CircleShape
                    )
            )
            
            // Cercle 2 - En haut à droite, se déplace verticalement
            Box(
                modifier = Modifier
                    .offset(
                        x = 300.dp,
                        y = (circle2Y * 180).dp - 40.dp
                    )
                    .size((120 * circle2Scale).dp)
                    .background(
                        color = Color.White.copy(alpha = circle2Alpha),
                        shape = CircleShape
                    )
            )
            
            // Cercle 3 - Au milieu, se déplace horizontalement
            Box(
                modifier = Modifier
                    .offset(
                        x = (circle3X * 250).dp + 50.dp,
                        y = 300.dp
                    )
                    .size((100 * circle3Scale).dp)
                    .background(
                        color = Color.White.copy(alpha = circle3Alpha),
                        shape = CircleShape
                    )
            )
            
            // Cercle 4 - En bas à gauche, rotation et scale
            Box(
                modifier = Modifier
                    .offset(x = 20.dp, y = 500.dp)
                    .size((80 * circle1Scale).dp)
                    .background(
                        color = Color.White.copy(alpha = circle2Alpha),
                        shape = CircleShape
                    )
            )
            
            // Cercle 5 - En bas à droite
            Box(
                modifier = Modifier
                    .offset(x = 280.dp, y = 600.dp)
                    .size((90 * circle3Scale).dp)
                    .background(
                        color = Color.White.copy(alpha = circle1Alpha),
                        shape = CircleShape
                    )
            )
        }
        
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Pager Content
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f)
            ) { page ->
                OnboardingPage(
                    pageIndex = page,
                    imageRes = when (page) {
                        0 -> R.drawable.onboarding1
                        1 -> R.drawable.onboarding2
                        2 -> R.drawable.onboarding3
                        else -> R.drawable.onboarding1
                    },
                    title = when (page) {
                        0 -> "Discover the best opportunities based on your talent."
                        1 -> "Smart matching that connects you with the right recruiters."
                        2 -> "Start your journey and apply for missions effortlessly."
                        else -> ""
                    }
                )
            }

            // Bottom Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Transparent)
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 40.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(20.dp))

                // Dot Indicators
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(bottom = 32.dp)
                ) {
                    repeat(3) { index ->
                        DotIndicator(
                            isSelected = index == currentPage
                        )
                    }
                }

                // Start Button - Affiché uniquement sur la dernière page
                if (viewModel.isLastPage) {
                    Button(
                        onClick = {
                            viewModel.completeOnboarding()
                            onComplete()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(0.dp), // Forme carrée
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF61A5C2),
                            contentColor = Color.White
                        ),
                        elevation = ButtonDefaults.buttonElevation(
                            defaultElevation = 8.dp,
                            pressedElevation = 4.dp
                        )
                    ) {
                        Text(
                            "Start",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun OnboardingPage(
    pageIndex: Int,
    imageRes: Int,
    title: String
) {
    // Animation pour l'image (pulse subtil)
    val infiniteTransition = rememberInfiniteTransition(label = "image_animation")
    
    val imageScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "image_scale"
    )
    
    val imageAlpha by infiniteTransition.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "image_alpha"
    )
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(40.dp))
        
        // Image avec effets visuels améliorés et animations
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 32.dp, vertical = 20.dp),
            contentAlignment = Alignment.Center
        ) {
            // Ombre portée pour effet de profondeur
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .scale(imageScale * 0.98f)
                    .shadow(
                        elevation = 24.dp,
                        shape = RoundedCornerShape(30.dp),
                        spotColor = Color.Black.copy(alpha = 0.3f),
                        ambientColor = Color.Black.copy(alpha = 0.2f)
                    )
                    .background(
                        color = Color.White.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(30.dp)
                    )
            )
            
            // Image principale avec animation
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .scale(imageScale)
                    .alpha(imageAlpha)
                    .shadow(
                        elevation = 16.dp,
                        shape = RoundedCornerShape(20.dp),
                        spotColor = Color.White.copy(alpha = 0.5f),
                        ambientColor = Color.White.copy(alpha = 0.3f)
                    ),
                contentScale = ContentScale.Fit
            )
        }

        // Title avec meilleur espacement
        Text(
            text = title,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp)
                .padding(top = 20.dp)
                .padding(bottom = 20.dp)
                .shadow(
                    elevation = 8.dp,
                    spotColor = Color.Black.copy(alpha = 0.3f),
                    ambientColor = Color.Black.copy(alpha = 0.2f)
                )
        )
    }
}

@Composable
fun DotIndicator(isSelected: Boolean) {
    Box(
        modifier = Modifier.size(10.dp),
        contentAlignment = Alignment.Center
    ) {
        if (isSelected) {
            // Filled blue circle
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .background(
                        color = Color(0xFF007AFF),
                        shape = CircleShape
                    )
            )
        } else {
            // Empty circle with blue border
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .background(
                        color = Color.Transparent,
                        shape = CircleShape
                    )
            ) {
                androidx.compose.foundation.Canvas(
                    modifier = Modifier.fillMaxSize()
                ) {
                    drawCircle(
                        color = Color(0xFF007AFF),
                        radius = size.minDimension / 2,
                        style = androidx.compose.ui.graphics.drawscope.Stroke(width = 1.5.dp.toPx())
                    )
                }
            }
        }
    }
}

