package com.example.matchify.ui.talent.filtering.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.matchify.domain.model.TalentCandidate

/**
 * Carte pour afficher un candidat talent avec son score IA
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TalentCandidateCard(
    candidate: TalentCandidate,
    onClick: () -> Unit = {},
    ratingPercentage: Double? = null,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFF1E293B),
        tonalElevation = 0.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header: Nom, score et icône favori
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Nom du talent
                Text(
                    text = candidate.fullName,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.weight(1f)
                )
                
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Badge de score IA
                    Surface(
                        color = Color(candidate.scoreColor).copy(alpha = 0.15f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.AutoAwesome,
                                contentDescription = null,
                                tint = Color(candidate.scoreColor),
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = "${candidate.score}%",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(candidate.scoreColor)
                            )
                        }
                    }
                    
                    // Badge de rating (pourcentage)
                    ratingPercentage?.let { rating ->
                        Surface(
                            color = Color(0xFF10B981).copy(alpha = 0.15f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "${rating.toInt()}%",
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF10B981)
                            )
                        }
                    }
                }
            }
            
            // Photo de profil et informations
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.Top
            ) {
                // Photo de profil
                AsyncImage(
                    model = candidate.profileImage,
                    contentDescription = "Profile",
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF3B82F6).copy(alpha = 0.2f)),
                    contentScale = androidx.compose.ui.layout.ContentScale.Crop
                )
                
                // Informations
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // Email
                    Text(
                        text = candidate.email,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color(0xFF94A3B8)
                    )
                    
                    // Localisation
                    candidate.location?.let { location ->
                        Text(
                            text = location,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Normal,
                            color = Color(0xFF94A3B8)
                        )
                    }
                    
                    // Description
                    candidate.description?.let { description ->
                        Text(
                            text = description,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Normal,
                            color = Color(0xFFCBD5E1),
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
            
            // Compétences
            candidate.skills.takeIf { it.isNotEmpty() }?.let { skills ->
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    items(skills.take(5)) { skill ->
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFF3B82F6).copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = skill,
                                modifier = Modifier.padding(
                                    horizontal = 10.dp,
                                    vertical = 4.dp
                                ),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF93C5FD)
                            )
                        }
                    }
                }
            }
            
            // Raisons du score
            candidate.reasons?.let { reasons ->
                Surface(
                    color = Color(0xFF3B82F6).copy(alpha = 0.1f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = reasons,
                        modifier = Modifier.padding(12.dp),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color(0xFF93C5FD),
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            
            // Match Breakdown (détails du matching)
            candidate.matchBreakdown?.let { breakdown ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    breakdown.skillsMatch?.let { match ->
                        MatchBreakdownItem("Compétences", match)
                    }
                    breakdown.experienceMatch?.let { match ->
                        MatchBreakdownItem("Expérience", match)
                    }
                    breakdown.locationMatch?.let { match ->
                        MatchBreakdownItem("Localisation", match)
                    }
                }
            }
        }
    }
}

@Composable
private fun MatchBreakdownItem(label: String, value: Double) {
    Surface(
        shape = RoundedCornerShape(6.dp),
        color = Color(0xFF1E293B)
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "${value.toInt()}%",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF3B82F6)
            )
            Text(
                text = label,
                fontSize = 10.sp,
                fontWeight = FontWeight.Normal,
                color = Color(0xFF94A3B8)
            )
        }
    }
}

