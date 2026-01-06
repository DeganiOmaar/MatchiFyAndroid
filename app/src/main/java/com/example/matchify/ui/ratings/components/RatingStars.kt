package com.example.matchify.ui.ratings.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Composant moderne pour afficher et sélectionner une note avec des étoiles
 * Avec animations et effets visuels
 */
@Composable
fun RatingStars(
    rating: Int,
    onRatingChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    starSize: androidx.compose.ui.unit.Dp = 40.dp,
    starSpacing: androidx.compose.ui.unit.Dp = 8.dp
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(starSpacing),
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (i in 1..5) {
            var isPressed by remember { mutableStateOf(false) }
            val scale by animateFloatAsState(
                targetValue = if (isPressed && enabled) 1.15f else 1f,
                animationSpec = spring(
                    dampingRatio = 0.6f,
                    stiffness = 300f
                ),
                label = "starScale"
            )
            
            val isFilled = i <= rating
            val starColor = when {
                isFilled -> when (rating) {
                    5 -> Color(0xFF10B981) // Vert pour 5 étoiles
                    4 -> Color(0xFF3B82F6) // Bleu pour 4 étoiles
                    3 -> Color(0xFFF59E0B) // Orange pour 3 étoiles
                    2 -> Color(0xFFEF4444) // Rouge pour 2 étoiles
                    1 -> Color(0xFFDC2626) // Rouge foncé pour 1 étoile
                    else -> Color(0xFFF59E0B)
                }
                else -> Color(0xFF475569) // Gris foncé pour les étoiles vides
            }
            
            val coroutineScope = rememberCoroutineScope()
            
            Surface(
                modifier = Modifier
                    .size(starSize)
                    .scale(scale)
                    .then(
                        if (enabled) {
                            Modifier.clickable { 
                                isPressed = true
                                onRatingChange(i)
                                coroutineScope.launch {
                                    delay(150)
                                    isPressed = false
                                }
                            }
                        } else {
                            Modifier
                        }
                    ),
                shape = CircleShape,
                color = Color.Transparent
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isFilled) Icons.Filled.Star else Icons.Outlined.Star,
                        contentDescription = "$i étoiles",
                        modifier = Modifier.fillMaxSize(),
                        tint = starColor
                    )
                }
            }
        }
    }
}

