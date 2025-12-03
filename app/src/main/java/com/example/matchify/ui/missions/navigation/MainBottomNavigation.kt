package com.example.matchify.ui.missions.navigation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Message
import androidx.compose.material.icons.automirrored.outlined.Message
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.matchify.ui.alerts.BadgeCountViewModel
import com.example.matchify.ui.alerts.BadgeCountViewModelFactory

/**
 * Bottom Navigation Bar - New Design
 * Matching specifications exactly
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainBottomNavigation(
    currentRoute: String?,
    onNavigate: (String) -> Unit,
    onScrollToTop: ((String) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    // Get badge counts
    val badgeViewModel: BadgeCountViewModel = viewModel(
        factory = BadgeCountViewModelFactory()
    )
    val alertsUnreadCount by badgeViewModel.alertsUnreadCount.collectAsState()
    val proposalsUnreadCount by badgeViewModel.proposalsUnreadCount.collectAsState()
    val conversationsUnreadCount by badgeViewModel.conversationsWithUnreadCount.collectAsState()
    
    // Colors matching specifications
    val backgroundColor = Color(0xFF0F172A) // Dark navy background
    val iconColorActive = Color(0xFF3B82F6) // Active icon: #3B82F6
    val iconColorInactive = Color(0xFF94A3B8) // Inactive icon: #94A3B8
    val textColorActive = Color(0xFF3B82F6) // Active text: #3B82F6
    val textColorInactive = Color(0xFF94A3B8) // Inactive text: #94A3B8
    
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(75.dp), // 70-80px height
        color = backgroundColor,
        tonalElevation = 0.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Missions Tab
            Box(modifier = Modifier.weight(1f)) {
                NavigationTabItem(
                    icon = Icons.Filled.Explore,
                    outlinedIcon = Icons.Outlined.Explore,
                    label = "Missions",
                    isSelected = currentRoute == "missions_list",
                    badgeCount = null,
                    iconColorActive = iconColorActive,
                    iconColorInactive = iconColorInactive,
                    textColorActive = textColorActive,
                    textColorInactive = textColorInactive,
                    onClick = {
                        if (currentRoute == "missions_list") {
                            onScrollToTop?.invoke("missions_list")
                        } else {
                            onNavigate("missions_list")
                        }
                    }
                )
            }
            
            // Proposals Tab
            Box(modifier = Modifier.weight(1f)) {
                NavigationTabItem(
                    icon = Icons.Filled.Description,
                    outlinedIcon = Icons.Outlined.Description,
                    label = "Proposals",
                    isSelected = currentRoute == "proposals_list",
                    badgeCount = if (proposalsUnreadCount > 0) proposalsUnreadCount.takeIf { it > 0 } else null,
                    iconColorActive = iconColorActive,
                    iconColorInactive = iconColorInactive,
                    textColorActive = textColorActive,
                    textColorInactive = textColorInactive,
                    onClick = {
                        if (currentRoute == "proposals_list") {
                            onScrollToTop?.invoke("proposals_list")
                        } else {
                            onNavigate("proposals_list")
                        }
                    }
                )
            }
            
            // Messages Tab
            Box(modifier = Modifier.weight(1f)) {
                NavigationTabItem(
                    icon = Icons.AutoMirrored.Filled.Message,
                    outlinedIcon = Icons.AutoMirrored.Outlined.Message,
                    label = "Messages",
                    isSelected = currentRoute == "messages_list",
                    badgeCount = if (conversationsUnreadCount > 0) conversationsUnreadCount.takeIf { it > 0 } else null,
                    iconColorActive = iconColorActive,
                    iconColorInactive = iconColorInactive,
                    textColorActive = textColorActive,
                    textColorInactive = textColorInactive,
                    onClick = {
                        if (currentRoute == "messages_list") {
                            onScrollToTop?.invoke("messages_list")
                        } else {
                            onNavigate("messages_list")
                        }
                    }
                )
            }
            
            // Alerts Tab
            Box(modifier = Modifier.weight(1f)) {
                NavigationTabItem(
                    icon = Icons.Filled.Notifications,
                    outlinedIcon = Icons.Outlined.Notifications,
                    label = "Alerts",
                    isSelected = currentRoute == "alerts_list",
                    badgeCount = if (alertsUnreadCount > 0) alertsUnreadCount.takeIf { it > 0 } else null,
                    iconColorActive = iconColorActive,
                    iconColorInactive = iconColorInactive,
                    textColorActive = textColorActive,
                    textColorInactive = textColorInactive,
                    onClick = {
                        if (currentRoute == "alerts_list") {
                            onScrollToTop?.invoke("alerts_list")
                        } else {
                            onNavigate("alerts_list")
                        }
                    }
                )
            }
        }
    }
}

/**
 * Custom navigation tab item
 */
@Composable
private fun NavigationTabItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    outlinedIcon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    isSelected: Boolean,
    badgeCount: Int?,
    iconColorActive: Color,
    iconColorInactive: Color,
    textColorActive: Color,
    textColorInactive: Color,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box {
            Icon(
                imageVector = if (isSelected) icon else outlinedIcon,
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = if (isSelected) iconColorActive else iconColorInactive
            )
            
            // Badge
            if (badgeCount != null && badgeCount > 0) {
                val badgeText = if (badgeCount > 99) "99+" else badgeCount.toString()
                
                Badge(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(x = 8.dp, y = (-4).dp),
                    containerColor = Color(0xFFFF6B9D), // Pink/red badge color
                    contentColor = Color.White
                ) {
                    Text(
                        text = badgeText,
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(4.dp))
        
        Text(
            text = label,
            fontSize = 12.5.sp,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
            color = if (isSelected) textColorActive else textColorInactive
        )
    }
}
