package com.example.matchify.ui.ratings.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Badge compact pour afficher une note moyenne
 * Utilisable sur les cartes de talents/propositions
 */
@Composable
fun RatingBadge(
    averageScore: Double?,
    count: Int = 0,
    modifier: Modifier = Modifier
) {
    if (averageScore != null && count > 0) {
        Surface(
            modifier = modifier,
            shape = RoundedCornerShape(12.dp),
            color = Color(0xFF3B82F6).copy(alpha = 0.15f),
            border = androidx.compose.foundation.BorderStroke(
                1.dp,
                Color(0xFF3B82F6).copy(alpha = 0.3f)
            )
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Star,
                    contentDescription = null,
                    tint = Color(0xFF3B82F6),
                    modifier = Modifier.size(14.dp)
                )
                Text(
                    text = String.format("%.1f", averageScore),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF3B82F6)
                )
                if (count > 0) {
                    Text(
                        text = "($count)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF94A3B8)
                    )
                }
            }
        }
    }
}

