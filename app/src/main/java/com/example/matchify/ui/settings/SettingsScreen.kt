package com.example.matchify.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onBack: () -> Unit,
    onLogoutSuccess: () -> Unit
) {
    val isLoggingOut by viewModel.isLoggingOut.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.logoutEvents.collect {
            onLogoutSuccess()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F172A))
    ) {
        // App Bar
        com.example.matchify.ui.components.MatchifyTopAppBar(
            title = "Settings",
            onBack = onBack
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Settings Section Title
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Settings",
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.SemiBold
                )
                
                Text(
                    text = "Manage your preferences and account settings.",
                    color = Color(0xFF9CA3AF),
                    fontSize = 15.sp
                )
            }

            // Settings Menu Items
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                color = Color(0xFF1E293B),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF374151)),
                shadowElevation = 4.dp
            ) {
                Column(
                    modifier = Modifier.padding(vertical = 8.dp)
                ) {
                    SettingsMenuItem(
                        icon = Icons.Rounded.Person,
                        title = "Contact Info",
                        subtitle = "Manage your contact details",
                        onClick = { /* TODO: Navigate to Contact Info */ }
                    )
                    
                    SettingsDivider()
                    
                    SettingsMenuItem(
                        icon = Icons.Rounded.Group,
                        title = "My Teams",
                        subtitle = "Manage your teams",
                        onClick = { /* TODO: Navigate to My Teams */ }
                    )
                    
                    SettingsDivider()
                    
                    SettingsMenuItem(
                        icon = Icons.Rounded.Lock,
                        title = "Password & Security",
                        subtitle = "Secure your account",
                        onClick = { /* TODO: Navigate to Password & Security */ }
                    )
                    
                    SettingsDivider()
                    
                    SettingsMenuItem(
                        icon = Icons.Rounded.Notifications,
                        title = "Notifications Settings",
                        subtitle = "Manage your notifications",
                        onClick = { /* TODO: Navigate to Notifications Settings */ }
                    )
                    
                    SettingsDivider()
                    
                    SettingsMenuItem(
                        icon = Icons.Rounded.Help,
                        title = "App Support",
                        subtitle = "Get help",
                        onClick = { /* TODO: Navigate to App Support */ }
                    )
                    
                    SettingsDivider()
                    
                    SettingsMenuItem(
                        icon = Icons.Rounded.Feedback,
                        title = "Feedback",
                        subtitle = "Share your feedback",
                        onClick = { /* TODO: Navigate to Feedback */ }
                    )
                }
            }

            // Account Section Title
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Account",
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.SemiBold
                )
                
                Text(
                    text = "Manage your session and ensure your data is secure.",
                    color = Color(0xFF9CA3AF),
                    fontSize = 15.sp
                )
            }

            // Logout Card
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                color = Color(0xFF1E293B),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF374151)),
                shadowElevation = 4.dp
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Icon Circle
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .background(
                                    color = Color(0xFFEF4444).copy(alpha = 0.12f),
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Logout,
                                contentDescription = null,
                                tint = Color(0xFFEF4444),
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        
                        // Text Content
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "Logout",
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            
                            Text(
                                text = "End your current session.",
                                color = Color(0xFF9CA3AF),
                                fontSize = 14.sp
                            )
                        }
                    }
                    
                    // Logout Button
                    Button(
                        onClick = { viewModel.logout() },
                        enabled = !isLoggingOut,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFEF4444),
                            contentColor = Color.White,
                            disabledContainerColor = Color(0xFFEF4444).copy(alpha = 0.5f)
                        )
                    ) {
                        if (isLoggingOut) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                text = "Logout",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                    
                    if (!errorMessage.isNullOrBlank()) {
                        Text(
                            text = errorMessage ?: "",
                            color = Color(0xFFEF4444),
                            fontSize = 14.sp,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SettingsMenuItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Icon
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = Color(0xFF3B82F6),
            modifier = Modifier.size(24.dp)
        )
        
        // Text Content
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = title,
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
            
            Text(
                text = subtitle,
                color = Color(0xFF9CA3AF),
                fontSize = 13.sp
            )
        }
        
        // Arrow Icon
        Icon(
            imageVector = Icons.Rounded.ChevronRight,
            contentDescription = null,
            tint = Color(0xFF64748B),
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
private fun SettingsDivider() {
    HorizontalDivider(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        color = Color(0xFF374151).copy(alpha = 0.5f),
        thickness = 1.dp
    )
}

