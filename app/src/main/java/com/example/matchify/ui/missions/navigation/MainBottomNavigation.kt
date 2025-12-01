package com.example.matchify.ui.missions.navigation

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
    
    NavigationBar(
        modifier = modifier
            .height(75.dp), // 70-80px height
        containerColor = backgroundColor,
        contentColor = iconColorActive,
        tonalElevation = 0.dp
    ) {
        // Missions Tab
        NavigationBarItem(
            icon = {
                NavigationBarIcon(
                    icon = Icons.Filled.Explore,
                    outlinedIcon = Icons.Outlined.Explore,
                    isSelected = currentRoute == "missions_list",
                    badgeCount = null,
                    iconColorActive = iconColorActive,
                    iconColorInactive = iconColorInactive
                )
            },
            label = {
                Text(
                    text = "Missions",
                    fontSize = 12.5.sp, // 12-13px
                    fontWeight = if (currentRoute == "missions_list") FontWeight.SemiBold else FontWeight.Normal,
                    color = if (currentRoute == "missions_list") textColorActive else textColorInactive
                )
            },
            selected = currentRoute == "missions_list",
            onClick = {
                if (currentRoute == "missions_list") {
                    onScrollToTop?.invoke("missions_list")
                } else {
                    onNavigate("missions_list")
                }
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = iconColorActive,
                selectedTextColor = textColorActive,
                indicatorColor = Color.Transparent,
                unselectedIconColor = iconColorInactive,
                unselectedTextColor = textColorInactive
            )
        )
        
        // Proposals Tab
        NavigationBarItem(
            icon = {
                NavigationBarIcon(
                    icon = Icons.Filled.Description,
                    outlinedIcon = Icons.Outlined.Description,
                    isSelected = currentRoute == "proposals_list",
                    badgeCount = if (proposalsUnreadCount > 0) proposalsUnreadCount.takeIf { it > 0 } else null,
                    iconColorActive = iconColorActive,
                    iconColorInactive = iconColorInactive
                )
            },
            label = {
                Text(
                    text = "Proposals",
                    fontSize = 12.5.sp, // 12-13px
                    fontWeight = if (currentRoute == "proposals_list") FontWeight.SemiBold else FontWeight.Normal,
                    color = if (currentRoute == "proposals_list") textColorActive else textColorInactive
                )
            },
            selected = currentRoute == "proposals_list",
            onClick = {
                if (currentRoute == "proposals_list") {
                    onScrollToTop?.invoke("proposals_list")
                } else {
                    onNavigate("proposals_list")
                }
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = iconColorActive,
                selectedTextColor = textColorActive,
                indicatorColor = Color.Transparent,
                unselectedIconColor = iconColorInactive,
                unselectedTextColor = textColorInactive
            )
        )
        
        // Messages Tab
        NavigationBarItem(
            icon = {
                NavigationBarIcon(
                    icon = Icons.AutoMirrored.Filled.Message,
                    outlinedIcon = Icons.AutoMirrored.Outlined.Message,
                    isSelected = currentRoute == "messages_list",
                    badgeCount = if (conversationsUnreadCount > 0) conversationsUnreadCount.takeIf { it > 0 } else null,
                    iconColorActive = iconColorActive,
                    iconColorInactive = iconColorInactive
                )
            },
            label = {
                Text(
                    text = "Messages",
                    fontSize = 12.5.sp, // 12-13px
                    fontWeight = if (currentRoute == "messages_list") FontWeight.SemiBold else FontWeight.Normal,
                    color = if (currentRoute == "messages_list") textColorActive else textColorInactive
                )
            },
            selected = currentRoute == "messages_list",
            onClick = {
                if (currentRoute == "messages_list") {
                    onScrollToTop?.invoke("messages_list")
                } else {
                    onNavigate("messages_list")
                }
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = iconColorActive,
                selectedTextColor = textColorActive,
                indicatorColor = Color.Transparent,
                unselectedIconColor = iconColorInactive,
                unselectedTextColor = textColorInactive
            )
        )
        
        // Alerts Tab
        NavigationBarItem(
            icon = {
                NavigationBarIcon(
                    icon = Icons.Filled.Notifications,
                    outlinedIcon = Icons.Outlined.Notifications,
                    isSelected = currentRoute == "alerts_list",
                    badgeCount = if (alertsUnreadCount > 0) alertsUnreadCount.takeIf { it > 0 } else null,
                    iconColorActive = iconColorActive,
                    iconColorInactive = iconColorInactive
                )
            },
            label = {
                Text(
                    text = "Alerts",
                    fontSize = 12.5.sp, // 12-13px
                    fontWeight = if (currentRoute == "alerts_list") FontWeight.SemiBold else FontWeight.Normal,
                    color = if (currentRoute == "alerts_list") textColorActive else textColorInactive
                )
            },
            selected = currentRoute == "alerts_list",
            onClick = {
                if (currentRoute == "alerts_list") {
                    onScrollToTop?.invoke("alerts_list")
                } else {
                    onNavigate("alerts_list")
                }
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = iconColorActive,
                selectedTextColor = textColorActive,
                indicatorColor = Color.Transparent,
                unselectedIconColor = iconColorInactive,
                unselectedTextColor = textColorInactive
            )
        )
    }
}

/**
 * Navigation bar icon with badge support
 * Icon size: 22-26px
 */
@Composable
private fun NavigationBarIcon(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    outlinedIcon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    badgeCount: Int?,
    iconColorActive: Color,
    iconColorInactive: Color
) {
    Box {
        Icon(
            imageVector = if (isSelected) icon else outlinedIcon,
            contentDescription = null,
            modifier = Modifier.size(24.dp), // 22-26px (middle value)
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
}
