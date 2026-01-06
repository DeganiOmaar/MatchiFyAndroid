package com.example.matchify.ui.ratings.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.matchify.domain.model.Rating

@Composable
fun RatingCard(
    rating: Rating,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "${rating.score}/5 - ${rating.recruiterName ?: "Recruiter"}",
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
            if (!rating.comment.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = rating.comment,
                    color = Color(0xFFCBD5E1),
                    fontSize = 14.sp
                )
            }
        }
    }
}
