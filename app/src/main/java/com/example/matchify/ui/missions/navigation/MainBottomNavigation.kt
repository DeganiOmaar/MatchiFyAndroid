package com.example.matchify.ui.missions.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

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
 * - Contains 2-5 destinations (currently 3: Missions, Proposals, Messages)
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
    
    NavigationBar(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface
    ) {
        NavigationBarItem(
            icon = {
                Icon(
                    imageVector = if (isMissionsSelected) Icons.Filled.Work else Icons.Outlined.Work,
                    contentDescription = "Missions"
                )
            },
            label = {
                Text(
                    text = "Missions",
                    style = MaterialTheme.typography.labelMedium
                )
            },
            selected = isMissionsSelected,
            onClick = { onNavigate("missions_list") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.primary,
                selectedTextColor = MaterialTheme.colorScheme.primary,
                indicatorColor = MaterialTheme.colorScheme.secondaryContainer,
                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        )

        NavigationBarItem(
            icon = {
                Icon(
                    imageVector = if (isProposalsSelected) Icons.Filled.Description else Icons.Outlined.Description,
                    contentDescription = "Proposals"
                )
            },
            label = {
                Text(
                    text = "Proposals",
                    style = MaterialTheme.typography.labelMedium
                )
            },
            selected = isProposalsSelected,
            onClick = { onNavigate("proposals_list") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.primary,
                selectedTextColor = MaterialTheme.colorScheme.primary,
                indicatorColor = MaterialTheme.colorScheme.secondaryContainer,
                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        )
        
        NavigationBarItem(
            icon = {
                Icon(
                    imageVector = if (isAlertsSelected) Icons.Rounded.Notifications else Icons.Outlined.Notifications,
                    contentDescription = "Alerts"
                )
            },
            label = {
                Text(
                    text = "Alerts",
                    style = MaterialTheme.typography.labelMedium
                )
            },
            selected = isAlertsSelected,
            onClick = { onNavigate("alerts_list") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.primary,
                selectedTextColor = MaterialTheme.colorScheme.primary,
                indicatorColor = MaterialTheme.colorScheme.secondaryContainer,
                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        )
        
        NavigationBarItem(
            icon = {
                Icon(
                    imageVector = if (isMessagesSelected) Icons.Filled.Message else Icons.Outlined.Message,
                    contentDescription = "Messages"
                )
            },
            label = {
                Text(
                    text = "Messages",
                    style = MaterialTheme.typography.labelMedium
                )
            },
            selected = isMessagesSelected,
            onClick = { onNavigate("messages_list") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.primary,
                selectedTextColor = MaterialTheme.colorScheme.primary,
                indicatorColor = MaterialTheme.colorScheme.secondaryContainer,
                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        )
    }
}


