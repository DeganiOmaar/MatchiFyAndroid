package com.example.matchify.ui.missions.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.matchify.domain.model.Mission
import com.example.matchify.domain.model.timePostedText

/**
 * Mission Card - New Design
 * Pixel-perfect match to specifications
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MissionCardNew(
    mission: Mission,
    isFavorite: Boolean,
    onFavoriteToggle: () -> Unit,
    onClick: () -> Unit = {},
    isOwner: Boolean = false,
    isRecruiter: Boolean = false,
    onEdit: (() -> Unit)? = null,
    onDelete: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        // Card container - #1E293B background, 16px corner radius
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClick() },
            shape = RoundedCornerShape(16.dp),
            color = Color(0xFF1E293B),
            tonalElevation = 0.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 18.dp), // 16-20px padding
                verticalArrangement = Arrangement.spacedBy(0.dp)
            ) {
                // Header: Posted time (left), AI Badge, and icon (right)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        // Posted time - #94A3B8, 13px, weight 400
                        Text(
                            text = mission.timePostedText,
                            fontSize = 13.sp,
                            fontWeight = FontWeight(400),
                            color = Color(0xFF94A3B8)
                        )

                        // AI Match Badge
                        if (mission.matchScore != null) {
                            Surface(
                                color = Color(0xFF3B82F6).copy(alpha = 0.1f),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.AutoAwesome,
                                        contentDescription = null,
                                        tint = Color(0xFF3B82F6),
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Text(
                                        text = "AI Match",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color(0xFF3B82F6)
                                    )
                                }
                            }
                        }
                    }
                    
                    // Icon based on role and ownership
                    when {
                        isRecruiter && isOwner -> {
                            // 3-dot menu for recruiter owner - clicking opens edit screen
                            IconButton(
                                onClick = { onEdit?.invoke() },
                                modifier = Modifier.size(32.dp),
                                colors = IconButtonDefaults.iconButtonColors(
                                    contentColor = Color(0xFF94A3B8)
                                )
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.MoreVert,
                                    contentDescription = "Edit Mission",
                                    modifier = Modifier.size(21.dp) // 20-22px
                                )
                            }
                        }
                        !isRecruiter -> {
                            // Heart icon for Talent - 20-22px
                            IconButton(
                                onClick = onFavoriteToggle,
                                modifier = Modifier.size(32.dp),
                                colors = IconButtonDefaults.iconButtonColors(
                                    contentColor = if (isFavorite) {
                                        Color(0xFF3B82F6) // Active: #3B82F6
                                    } else {
                                        Color(0xFF94A3B8) // Inactive: #94A3B8
                                    }
                                )
                            ) {
                                Icon(
                                    imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                                    contentDescription = "Favorite",
                                    modifier = Modifier.size(21.dp) // 20-22px
                                )
                            }
                        }
                        // Recruiter but not owner: no icon
                    }
                }
                
                // Mission Title - #FFFFFF, 16-17px, weight 600, max 2 lines
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = mission.title,
                    fontSize = 16.5.sp, // 16-17px
                    fontWeight = FontWeight(600),
                    color = Color(0xFFFFFFFF),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 20.sp
                )
                
                // Price and Match Score - #3B82F6, 15-16px, weight 600, spacing 4-6px below title
                Spacer(modifier = Modifier.height(5.dp)) // 4-6px
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = mission.formattedBudget,
                        fontSize = 15.5.sp, // 15-16px
                        fontWeight = FontWeight(600),
                        color = Color(0xFF3B82F6)
                    )

                    if (mission.matchScore != null) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "${mission.matchScore}%",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF3B82F6)
                            )
                            Text(
                                text = "match",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Normal,
                                color = Color(0xFF94A3B8)
                            )
                        }
                    }
                }
                
                // Description - #CBD5E1, 14px, weight 400, max 2-3 lines, line height 1.3-1.4
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = mission.description ?: "",
                    fontSize = 14.sp,
                    fontWeight = FontWeight(400),
                    color = Color(0xFFCBD5E1),
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 19.sp // 1.36 (14 * 1.36 ≈ 19)
                )

                // Reasoning
                if (!mission.reasoning.isNullOrEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = mission.reasoning,
                        fontSize = 13.sp,
                        fontWeight = FontWeight(400),
                        color = Color(0xFF3B82F6).copy(alpha = 0.8f),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        lineHeight = 18.sp
                    )
                }
                
                // Skill Tags - background #1E293B, text #93C5FD, 12-13px, weight 500
                mission.skills?.let { skillsList ->
                    if (skillsList.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(7.dp), // 6-8px
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            items(skillsList) { skill ->
                                // Rounded pill tag
                                Surface(
                                    shape = RoundedCornerShape(16.dp),
                                    color = Color(0xFF1E293B),
                                    tonalElevation = 0.dp
                                ) {
                                    Text(
                                        text = skill,
                                        modifier = Modifier.padding(
                                            horizontal = 11.dp, // 10-12px
                                            vertical = 5.dp // 4-6px
                                        ),
                                        fontSize = 12.5.sp, // 12-13px
                                        fontWeight = FontWeight(500),
                                        color = Color(0xFF93C5FD)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
