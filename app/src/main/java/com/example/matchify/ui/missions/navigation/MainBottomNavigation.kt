package com.example.matchify.ui.missions.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Work
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Work
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
 * - Contains 2-5 destinations (currently 2: Missions, Profile)
 * - All destinations have equal importance
 * - Icon on top, label below
 * - Smooth animations and transitions
 */
@Composable
fun MainBottomNavigation(
    currentRoute: String?,
    profileRoute: String,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val isMissionsSelected = currentRoute == "missions_list"
    val isProfileSelected = currentRoute == profileRoute
    
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
                    imageVector = if (isProfileSelected) Icons.Filled.Person else Icons.Outlined.Person,
                    contentDescription = "Profile"
                )
            },
            label = {
                Text(
                    text = "Profile",
                    style = MaterialTheme.typography.labelMedium
                )
            },
            selected = isProfileSelected,
            onClick = { onNavigate(profileRoute) },
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


