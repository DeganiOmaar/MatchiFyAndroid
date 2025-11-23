package com.example.matchify.ui.missions.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.matchify.ui.alerts.BadgeCountViewModel
import com.example.matchify.ui.alerts.BadgeCountViewModelFactory

/**
 * Material 3 Bottom Navigation Bar
 * 
 * Fully compliant with Material Design 3 guidelines:
 * - Fixed at bottom of screen
 * - Icon + label for each destination
 * - Active item: filled icon + active indicator
 * - Inactive items: outlined icons
 * - Badge support for unread counts
 * - Light/dark mode support
 * - Scroll-to-top on active tab re-selection
 * - Proper spacing and animations
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
    
    // Icon colors: white in dark mode, black in light mode
    // Use MaterialTheme colorScheme to detect current theme (respects user preference from drawer)
    val colorScheme = MaterialTheme.colorScheme
    // Check if current theme is dark by comparing surface color brightness
    // Dark theme surface is typically darker (lower brightness)
    val surfaceColor = colorScheme.surface
    val isDarkMode = (surfaceColor.red + surfaceColor.green + surfaceColor.blue) / 3f < 0.5f
    
    val iconColorSelected = if (isDarkMode) Color.White else Color.Black
    val iconColorUnselected = if (isDarkMode) Color.White.copy(alpha = 0.6f) else Color.Black.copy(alpha = 0.6f)
    val textColorSelected = if (isDarkMode) Color.White else Color.Black
    val textColorUnselected = if (isDarkMode) Color.White.copy(alpha = 0.6f) else Color.Black.copy(alpha = 0.6f)
    
    // Use MD3 surface token for background - automatically adapts to app theme (from drawer settings)
    val backgroundColor = colorScheme.surface
    
    NavigationBar(
        modifier = modifier,
        containerColor = backgroundColor,
        contentColor = iconColorSelected, // Use explicit icon color to ensure proper theming
        tonalElevation = 0.dp
    ) {
        // Missions Tab
        NavigationBarItem(
            icon = {
                NavigationBarIcon(
                    icon = Icons.Filled.Work,
                    outlinedIcon = Icons.Outlined.Work,
                    isSelected = currentRoute == "missions_list",
                    badgeCount = null
                )
            },
            label = {
                Text(
                    text = "Missions",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = if (currentRoute == "missions_list") FontWeight.SemiBold else FontWeight.Normal
                )
            },
            selected = currentRoute == "missions_list",
            onClick = {
                if (currentRoute == "missions_list") {
                    // Re-selecting active tab - scroll to top
                    onScrollToTop?.invoke("missions_list")
                } else {
                    onNavigate("missions_list")
                }
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = iconColorSelected,
                selectedTextColor = textColorSelected,
                indicatorColor = MaterialTheme.colorScheme.secondaryContainer,
                unselectedIconColor = iconColorUnselected,
                unselectedTextColor = textColorUnselected
            )
        )
        
        // Proposals Tab
        NavigationBarItem(
            icon = {
                NavigationBarIcon(
                    icon = Icons.Filled.Description,
                    outlinedIcon = Icons.Outlined.Description,
                    isSelected = currentRoute == "proposals_list",
                    badgeCount = if (proposalsUnreadCount > 0) proposalsUnreadCount.takeIf { it > 0 } else null
                )
            },
            label = {
                Text(
                    text = "Proposals",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = if (currentRoute == "proposals_list") FontWeight.SemiBold else FontWeight.Normal
                )
            },
            selected = currentRoute == "proposals_list",
            onClick = {
                if (currentRoute == "proposals_list") {
                    // Re-selecting active tab - scroll to top
                    onScrollToTop?.invoke("proposals_list")
                } else {
                    onNavigate("proposals_list")
                }
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = iconColorSelected,
                selectedTextColor = textColorSelected,
                indicatorColor = MaterialTheme.colorScheme.secondaryContainer,
                unselectedIconColor = iconColorUnselected,
                unselectedTextColor = textColorUnselected
            )
        )
        
        // Messages Tab
        NavigationBarItem(
            icon = {
                NavigationBarIcon(
                    icon = Icons.AutoMirrored.Filled.Message,
                    outlinedIcon = Icons.AutoMirrored.Outlined.Message,
                    isSelected = currentRoute == "messages_list",
                    badgeCount = if (conversationsUnreadCount > 0) conversationsUnreadCount.takeIf { it > 0 } else null
                )
            },
            label = {
                Text(
                    text = "Messages",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = if (currentRoute == "messages_list") FontWeight.SemiBold else FontWeight.Normal
                )
            },
            selected = currentRoute == "messages_list",
            onClick = {
                if (currentRoute == "messages_list") {
                    // Re-selecting active tab - scroll to top
                    onScrollToTop?.invoke("messages_list")
                } else {
                    onNavigate("messages_list")
                }
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = iconColorSelected,
                selectedTextColor = textColorSelected,
                indicatorColor = MaterialTheme.colorScheme.secondaryContainer,
                unselectedIconColor = iconColorUnselected,
                unselectedTextColor = textColorUnselected
            )
        )
        
        // Alerts Tab
        NavigationBarItem(
            icon = {
                NavigationBarIcon(
                    icon = Icons.Filled.Notifications,
                    outlinedIcon = Icons.Outlined.Notifications,
                    isSelected = currentRoute == "alerts_list",
                    badgeCount = if (alertsUnreadCount > 0) alertsUnreadCount.takeIf { it > 0 } else null
                )
            },
            label = {
                Text(
                    text = "Alerts",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = if (currentRoute == "alerts_list") FontWeight.SemiBold else FontWeight.Normal
                )
            },
            selected = currentRoute == "alerts_list",
            onClick = {
                if (currentRoute == "alerts_list") {
                    // Re-selecting active tab - scroll to top
                    onScrollToTop?.invoke("alerts_list")
                } else {
                    onNavigate("alerts_list")
                }
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = iconColorSelected,
                selectedTextColor = textColorSelected,
                indicatorColor = MaterialTheme.colorScheme.secondaryContainer,
                unselectedIconColor = iconColorUnselected,
                unselectedTextColor = textColorUnselected
            )
        )
    }
}

/**
 * Navigation bar icon with badge support
 * 
 * Material 3 compliant:
 * - Filled icon when selected
 * - Outlined icon when not selected
 * - Badge count (max 4 chars: "99+")
 * - Proper badge positioning
 */
@Composable
private fun NavigationBarIcon(
    icon: ImageVector,
    outlinedIcon: ImageVector,
    isSelected: Boolean,
    badgeCount: Int?
) {
    Box {
        Icon(
            imageVector = if (isSelected) icon else outlinedIcon,
            contentDescription = null,
            modifier = Modifier.size(24.dp)
        )
        
        // Badge
        if (badgeCount != null && badgeCount > 0) {
            val badgeText = if (badgeCount > 99) "99+" else badgeCount.toString()
            
            // Badge color stays red/pink as shown in design
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
