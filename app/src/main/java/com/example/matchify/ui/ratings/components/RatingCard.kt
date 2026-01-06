package com.example.matchify.ui.ratings.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.matchify.domain.model.Rating
import com.example.matchify.ui.ratings.components.RatingStars
import java.text.SimpleDateFormat
import java.util.*

/**
 * Carte moderne pour afficher un rating individuel
 */
@Composable
fun RatingCard(
    rating: Rating,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1E293B)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header avec note et date
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Note avec étoiles
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Badge avec le score
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = getScoreColor(rating.score).copy(alpha = 0.2f)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Star,
                                contentDescription = null,
                                tint = getScoreColor(rating.score),
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = "${rating.score}/5",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = getScoreColor(rating.score)
                            )
                        }
                    }
                    
                    // Étoiles visuelles
                    RatingStars(
                        rating = rating.score,
                        onRatingChange = {},
                        enabled = false,
                        starSize = 14.dp,
                        starSpacing = 2.dp
                    )
                }
                
                // Date
                rating.createdAt?.let { date ->
                    Text(
                        text = formatDate(date),
                        fontSize = 11.sp,
                        color = Color(0xFF94A3B8),
                        fontWeight = FontWeight.Medium
                    )
                }
            }
            
            // Commentaire
            AnimatedVisibility(
                visible = !rating.comment.isNullOrEmpty(),
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                rating.comment?.let { comment ->
                    Text(
                        text = comment,
                        fontSize = 14.sp,
                        color = Color(0xFFE2E8F0),
                        lineHeight = 20.sp,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
            
            // Tags
            AnimatedVisibility(
                visible = rating.tags.isNotEmpty(),
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                if (rating.tags.isNotEmpty()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        rating.tags.forEach { tag ->
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = Color(0xFF3B82F6).copy(alpha = 0.15f),
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    Color(0xFF3B82F6).copy(alpha = 0.3f)
                                )
                            ) {
                                Text(
                                    text = tag,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color(0xFF60A5FA)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Carte moderne pour afficher la note moyenne et les statistiques
 */
@Composable
fun AverageRatingCard(
    averageScore: Double,
    count: Int,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1E293B)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF1E293B),
                            Color(0xFF0F172A)
                        )
                    )
                )
                .padding(24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Note moyenne principale
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.Bottom,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = String.format("%.1f", averageScore),
                            fontSize = 48.sp,
                            fontWeight = FontWeight.Bold,
                            color = getScoreColor(averageScore.toInt()),
                            lineHeight = 48.sp
                        )
                        Text(
                            text = "/5",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF94A3B8),
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }
                    
                    RatingStars(
                        rating = averageScore.toInt(),
                        onRatingChange = {},
                        enabled = false,
                        starSize = 24.dp,
                        starSpacing = 4.dp
                    )
                }
                
                // Statistiques
                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFF3B82F6).copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = "$count",
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF3B82F6)
                        )
                    }
                    Text(
                        text = if (count == 1) "avis" else "avis",
                        fontSize = 12.sp,
                        color = Color(0xFF94A3B8),
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

/**
 * Obtenir la couleur selon le score
 */
private fun getScoreColor(score: Int): Color {
    return when (score) {
        5 -> Color(0xFF10B981) // Vert
        4 -> Color(0xFF3B82F6) // Bleu
        3 -> Color(0xFFF59E0B) // Orange
        2 -> Color(0xFFEF4444) // Rouge
        1 -> Color(0xFFDC2626) // Rouge foncé
        else -> Color(0xFF6B7280) // Gris
    }
}

/**
 * Formater la date pour l'affichage
 */
private fun formatDate(dateString: String): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd MMM yyyy", Locale.FRENCH)
        val date = inputFormat.parse(dateString) ?: return dateString
        outputFormat.format(date)
    } catch (e: Exception) {
        try {
            // Format alternatif
            dateString.substring(0, 10).replace("-", "/")
        } catch (e2: Exception) {
            dateString
        }
    }
}

