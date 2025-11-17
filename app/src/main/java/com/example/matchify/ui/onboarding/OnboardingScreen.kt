package com.example.matchify.ui.onboarding

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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
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
                    .background(Color.White)
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(20.dp))

                // Dot Indicators
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(bottom = 24.dp)
                ) {
                    repeat(3) { index ->
                        DotIndicator(
                            isSelected = index == currentPage
                        )
                    }
                }

                // Navigation Buttons
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 40.dp),
                    horizontalArrangement = if (viewModel.isFirstPage) {
                        Arrangement.Center
                    } else {
                        Arrangement.spacedBy(16.dp)
                    }
                ) {
                    // Previous Button
                    if (!viewModel.isFirstPage) {
                        OutlinedButton(
                            onClick = {
                                viewModel.previousPage()
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(55.dp),
                            shape = RoundedCornerShape(30.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = Color.Gray
                            ),
                            border = ButtonDefaults.outlinedButtonBorder.copy(
                                width = 1.dp
                            )
                        ) {
                            Text(
                                "Previous",
                                color = Color.Gray,
                                fontWeight = FontWeight.Medium,
                                fontSize = 16.sp
                            )
                        }
                    }

                    // Next/Start Button
                    Button(
                        onClick = {
                            if (viewModel.isLastPage) {
                                viewModel.completeOnboarding()
                                onComplete()
                            } else {
                                viewModel.nextPage()
                            }
                        },
                        modifier = Modifier
                            .then(
                                if (viewModel.isFirstPage) {
                                    Modifier.fillMaxWidth()
                                } else {
                                    Modifier.weight(1f)
                                }
                            )
                            .height(55.dp),
                        shape = RoundedCornerShape(30.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF007AFF),
                            disabledContainerColor = Color(0xFFBAD7FF)
                        )
                    ) {
                        Text(
                            if (viewModel.isLastPage) "Start" else "Next",
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 16.sp
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
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Image
        Image(
            painter = painterResource(id = imageRes),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentScale = ContentScale.Fit
        )

        // Title
        Text(
            text = title,
            fontSize = 24.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.Black,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp)
                .padding(top = 40.dp)
                .padding(bottom = 20.dp)
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

