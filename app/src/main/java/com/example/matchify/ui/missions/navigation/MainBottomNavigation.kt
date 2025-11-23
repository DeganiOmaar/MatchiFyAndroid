package com.example.matchify.ui.missions.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Modern Material 3 Bottom Navigation Bar
 * 
 * Features:
 * - Official Material 3 NavigationBar component
 * - Material default elevation, spacing, shape, ripple, and animations
 * - Dynamic color support for light and dark themes
 * - Icons and labels follow Material guidelines
 * - Active destination uses highlight color and shape
 * - Inactive destinations use subdued colors
 * 
 * Material 3 Guidelines:
 * - Contains 2-5 destinations (currently 4: Missions, Proposals, Alert, Messages)
 * - All destinations have equal importance
 * - Icon on top, label below
 * - Smooth animations and transitions
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainBottomNavigation(
    currentRoute: String?,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val isMissionsSelected = currentRoute == "missions_list"
    val isProposalsSelected = currentRoute == "proposals_list"
    val isAlertsSelected = currentRoute == "alerts_list"
    val isMessagesSelected = currentRoute == "messages_list"
    
    // Couleur de fond et navbar
    val backgroundColor = Color(0xFF61A5C2)
    
    NavigationBar(
        modifier = modifier,
        containerColor = backgroundColor,
        contentColor = Color.White,
        tonalElevation = 0.dp
    ) {
        // Navigation items avec espacement optimal
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            NavigationBarItem(
                icon = {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .then(
                                if (isMissionsSelected) {
                                    Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(Color.White.copy(alpha = 0.2f))
                                } else {
                                    Modifier
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isMissionsSelected) Icons.Filled.Work else Icons.Outlined.Work,
                            contentDescription = "Missions",
                            modifier = Modifier.size(22.dp)
                        )
                    }
                },
                label = {
                    Text(
                        text = "Missions",
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 11.sp
                    )
                },
                selected = isMissionsSelected,
                onClick = { onNavigate("missions_list") },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color.White,
                    selectedTextColor = Color.White,
                    unselectedIconColor = Color.White.copy(alpha = 0.75f),
                    unselectedTextColor = Color.White.copy(alpha = 0.75f),
                    indicatorColor = Color.Transparent
                ),
                modifier = Modifier.weight(1f)
            )

            NavigationBarItem(
                icon = {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .then(
                                if (isProposalsSelected) {
                                    Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(Color.White.copy(alpha = 0.2f))
                                } else {
                                    Modifier
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isProposalsSelected) Icons.Filled.Description else Icons.Outlined.Description,
                            contentDescription = "Proposals",
                            modifier = Modifier.size(22.dp)
                        )
                    }
                },
                label = {
                    Text(
                        text = "Proposals",
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 11.sp
                    )
                },
                selected = isProposalsSelected,
                onClick = { onNavigate("proposals_list") },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color.White,
                    selectedTextColor = Color.White,
                    unselectedIconColor = Color.White.copy(alpha = 0.75f),
                    unselectedTextColor = Color.White.copy(alpha = 0.75f),
                    indicatorColor = Color.Transparent
                ),
                modifier = Modifier.weight(1f)
            )
            
            NavigationBarItem(
                icon = {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .then(
                                if (isAlertsSelected) {
                                    Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(Color.White.copy(alpha = 0.2f))
                                } else {
                                    Modifier
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isAlertsSelected) Icons.Filled.Notifications else Icons.Outlined.Notifications,
                            contentDescription = "Alert",
                            modifier = Modifier.size(22.dp)
                        )
                    }
                },
                label = {
                    Text(
                        text = "Alert",
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 11.sp
                    )
                },
                selected = isAlertsSelected,
                onClick = { onNavigate("alerts_list") },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color.White,
                    selectedTextColor = Color.White,
                    unselectedIconColor = Color.White.copy(alpha = 0.75f),
                    unselectedTextColor = Color.White.copy(alpha = 0.75f),
                    indicatorColor = Color.Transparent
                ),
                modifier = Modifier.weight(1f)
            )
            
            NavigationBarItem(
                icon = {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .then(
                                if (isMessagesSelected) {
                                    Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(Color.White.copy(alpha = 0.2f))
                                } else {
                                    Modifier
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isMessagesSelected) Icons.Filled.Message else Icons.Outlined.Message,
                            contentDescription = "Messages",
                            modifier = Modifier.size(22.dp)
                        )
                    }
                },
                label = {
                    Text(
                        text = "Messages",
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 11.sp
                    )
                },
                selected = isMessagesSelected,
                onClick = { onNavigate("messages_list") },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color.White,
                    selectedTextColor = Color.White,
                    unselectedIconColor = Color.White.copy(alpha = 0.75f),
                    unselectedTextColor = Color.White.copy(alpha = 0.75f),
                    indicatorColor = Color.Transparent
                ),
                modifier = Modifier.weight(1f)
            )
        }
    }
}


