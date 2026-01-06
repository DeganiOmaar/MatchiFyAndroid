package com.example.matchify.ui.ratings.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun RatingStars(
    rating: Int,
    onRatingChange: (Int) -> Unit,
    enabled: Boolean = true,
    starSize: Dp = 24.dp,
    starSpacing: Dp = 4.dp
) {
    Row {
        for (i in 1..5) {
            Icon(
                imageVector = if (i <= rating) Icons.Filled.Star else Icons.Outlined.Star,
                contentDescription = "$i stars",
                tint = if (i <= rating) Color(0xFFF59E0B) else Color(0xFF9CA3AF), // Amber-500 or Gray-400
                modifier = Modifier
                    .size(starSize)
                    .clickable(enabled = enabled) {
                        onRatingChange(i)
                    }
            )
            if (i < 5) {
                Spacer(modifier = Modifier.width(starSpacing))
            }
        }
    }
}
