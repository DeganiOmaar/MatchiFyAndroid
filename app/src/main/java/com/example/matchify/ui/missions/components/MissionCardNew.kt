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
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Delete
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
    var showMenuSheet by remember { mutableStateOf(false) }
    
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
                // Header: Posted time (left) and icon (right)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Posted time - #94A3B8, 13px, weight 400
                    Text(
                        text = mission.timePostedText,
                        fontSize = 13.sp,
                        fontWeight = FontWeight(400),
                        color = Color(0xFF94A3B8),
                        modifier = Modifier.weight(1f)
                    )
                    
                    // Icon based on role and ownership
                    when {
                        isRecruiter && isOwner -> {
                            // 3-dot menu for recruiter owner - 20-22px, #94A3B8
                            IconButton(
                                onClick = { showMenuSheet = true },
                                modifier = Modifier.size(32.dp),
                                colors = IconButtonDefaults.iconButtonColors(
                                    contentColor = Color(0xFF94A3B8)
                                )
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.MoreVert,
                                    contentDescription = "Menu",
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
                
                // Price - #3B82F6, 15-16px, weight 600, spacing 4-6px below title
                Spacer(modifier = Modifier.height(5.dp)) // 4-6px
                Text(
                    text = mission.formattedBudget,
                    fontSize = 15.5.sp, // 15-16px
                    fontWeight = FontWeight(600),
                    color = Color(0xFF3B82F6)
                )
                
                // Description - #CBD5E1, 14px, weight 400, max 2-3 lines, line height 1.3-1.4
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = mission.description,
                    fontSize = 14.sp,
                    fontWeight = FontWeight(400),
                    color = Color(0xFFCBD5E1),
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 19.sp // 1.36 (14 * 1.36 ≈ 19)
                )
                
                // Skill Tags - background #1E293B, text #93C5FD, 12-13px, weight 500
                if (mission.skills.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(7.dp), // 6-8px
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        items(mission.skills) { skill ->
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
        
        // Bottom Sheet Menu for recruiter owner
        if (showMenuSheet && isRecruiter && isOwner) {
            val sheetState = rememberModalBottomSheetState(
                skipPartiallyExpanded = false
            )
            ModalBottomSheet(
                onDismissRequest = { showMenuSheet = false },
                sheetState = sheetState,
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                containerColor = MaterialTheme.colorScheme.surface,
                dragHandle = {
                    Box(
                        modifier = Modifier
                            .width(40.dp)
                            .height(4.dp)
                            .padding(vertical = 12.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f))
                    )
                }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 32.dp)
                ) {
                    // Edit Mission
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                showMenuSheet = false
                                onEdit?.invoke()
                            }
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        color = Color.Transparent
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                modifier = Modifier.size(48.dp),
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                            ) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier.fillMaxSize()
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.Edit,
                                        contentDescription = null,
                                        modifier = Modifier.size(24.dp),
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                            
                            Text(
                                text = "Edit Mission",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                    
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                    )
                    
                    // Delete Mission
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                showMenuSheet = false
                                onDelete?.invoke()
                            }
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        color = Color.Transparent
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                modifier = Modifier.size(48.dp),
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.error.copy(alpha = 0.15f)
                            ) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier.fillMaxSize()
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.Delete,
                                        contentDescription = null,
                                        modifier = Modifier.size(24.dp),
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                            
                            Text(
                                text = "Delete Mission",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }
    }
}
