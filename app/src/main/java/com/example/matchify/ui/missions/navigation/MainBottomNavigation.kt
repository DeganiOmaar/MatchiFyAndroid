package com.example.matchify.ui.missions.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MainBottomNavigation(
    currentRoute: String?,
    onNavigate: (String) -> Unit
) {
    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 8.dp,
        modifier = androidx.compose.ui.Modifier
    ) {
        NavigationBarItem(
            icon = {
                Icon(
                    imageVector = Icons.Default.Work,
                    contentDescription = "Missions"
                )
            },
            label = { 
                Text(
                    "Missions",
                    fontSize = 12.sp,
                    fontWeight = if (currentRoute == "missions_list") FontWeight.SemiBold else FontWeight.Normal
                ) 
            },
            selected = currentRoute == "missions_list",
            onClick = { onNavigate("missions_list") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFF007AFF),
                selectedTextColor = Color(0xFF007AFF),
                indicatorColor = Color(0xFF007AFF).copy(alpha = 0.1f),
                unselectedIconColor = Color(0xFF8E8E93),
                unselectedTextColor = Color(0xFF8E8E93)
            )
        )

        NavigationBarItem(
            icon = {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Profile"
                )
            },
            label = { 
                Text(
                    "Profile",
                    fontSize = 12.sp,
                    fontWeight = if (currentRoute == "recruiter_profile") FontWeight.SemiBold else FontWeight.Normal
                ) 
            },
            selected = currentRoute == "recruiter_profile",
            onClick = { onNavigate("recruiter_profile") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFF007AFF),
                selectedTextColor = Color(0xFF007AFF),
                indicatorColor = Color(0xFF007AFF).copy(alpha = 0.1f),
                unselectedIconColor = Color(0xFF8E8E93),
                unselectedTextColor = Color(0xFF8E8E93)
            )
        )
    }
}


