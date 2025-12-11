package com.example.matchify.ui.missions.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.matchify.data.local.AuthPreferencesProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.matchify.ui.theme.ThemeViewModel
import com.example.matchify.ui.theme.ThemeViewModelFactory
import com.example.matchify.ui.theme.ThemeType

enum class DrawerMenuItemType {
    PROFILE,
    MY_STATS,
    CHAT_BOT,
    SETTINGS,
    THEME,
    LOG_OUT
}

data class DrawerMenuItem(
    val title: String,
    val icon: ImageVector,
    val type: DrawerMenuItemType
) {
    companion object {
        fun recruiterItems(): List<DrawerMenuItem> = listOf(
            DrawerMenuItem("Profile", Icons.Default.Person, DrawerMenuItemType.PROFILE),
            DrawerMenuItem("Chat Bot", Icons.Default.Message, DrawerMenuItemType.CHAT_BOT),
            DrawerMenuItem("Settings", Icons.Default.Settings, DrawerMenuItemType.SETTINGS),
            DrawerMenuItem("Theme", Icons.Default.DarkMode, DrawerMenuItemType.THEME)
        )
        
        fun talentItems(): List<DrawerMenuItem> = listOf(
            DrawerMenuItem("Profile", Icons.Default.Person, DrawerMenuItemType.PROFILE),
            DrawerMenuItem("My Stats", Icons.Default.Assessment, DrawerMenuItemType.MY_STATS),
            DrawerMenuItem("Chat Bot", Icons.Default.Message, DrawerMenuItemType.CHAT_BOT),
            DrawerMenuItem("Settings", Icons.Default.Settings, DrawerMenuItemType.SETTINGS),
            DrawerMenuItem("Theme", Icons.Default.DarkMode, DrawerMenuItemType.THEME)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewDrawerContent(
    drawerState: DrawerState,
    currentRoute: String?,
    onClose: () -> Unit,
    onMenuItemSelected: (DrawerMenuItemType) -> Unit = {},
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val prefs = remember { AuthPreferencesProvider.getInstance().get() }
    val user by prefs.user.collectAsState(initial = null)
    val role by prefs.role.collectAsState(initial = "recruiter")
    
    val isRecruiter = role == "recruiter"
    val menuItems = if (isRecruiter) {
        DrawerMenuItem.recruiterItems()
    } else {
        DrawerMenuItem.talentItems()
    }
    
    val fullName = user?.fullName ?: "User"
    val talentType = user?.talent?.firstOrNull() ?: ""
    val profileImageUrl = user?.profileImageUrl
    
    // Dark theme colors matching the screenshot exactly
    val backgroundColor = Color(0xFF0F172A) // Very dark blue-gray background (slate-900)
    val selectedItemBackground = Color(0xFF1E3A8A) // Dark blue for selected item
    val textColor = Color(0xFFFFFFFF) // White text
    val subtitleColor = Color(0xFF94A3B8) // Light gray for subtitle (slate-400)
    val profileImageBg = Color(0xFFF5F5DC) // Beige background for profile image
    
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            Surface(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(280.dp),
                color = backgroundColor
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(0.dp)
                ) {
                    // User Profile Header
                    UserProfileHeader(
                        fullName = fullName,
                        talentType = if (!isRecruiter && talentType.isNotEmpty()) talentType else if (isRecruiter) "Recruiter" else "",
                        profileImageUrl = profileImageUrl,
                        profileImageBg = profileImageBg,
                        textColor = textColor,
                        subtitleColor = subtitleColor,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 28.dp, start = 20.dp, end = 20.dp, bottom = 20.dp)
                    )
                    
                    // Divider
                    HorizontalDivider(
                        modifier = Modifier.fillMaxWidth(),
                        color = Color(0xFF1E293B).copy(alpha = 0.6f),
                        thickness = 1.dp
                    )
                    
                    // Menu Items
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .padding(vertical = 4.dp, horizontal = 8.dp)
                    ) {
                        menuItems.forEach { item ->
                            val isSelected = isItemSelected(item.type, currentRoute)
                            
                            MenuItem(
                                item = item,
                                isSelected = isSelected,
                                selectedBackground = selectedItemBackground,
                                textColor = textColor,
                                onClick = {
                                    onMenuItemSelected(item.type)
                                    onClose()
                                },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }
        },
        modifier = modifier,
        scrimColor = Color.Black.copy(alpha = 0.5f)
    ) {
        content()
    }
}

@Composable
private fun UserProfileHeader(
    fullName: String,
    talentType: String,
    profileImageUrl: String?,
    profileImageBg: Color,
    textColor: Color,
    subtitleColor: Color,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Profile Image with beige background
        Surface(
            modifier = Modifier.size(64.dp),
            shape = CircleShape,
            color = profileImageBg
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                if (profileImageUrl != null) {
                    AsyncImage(
                        model = profileImageUrl,
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        modifier = Modifier.size(32.dp),
                        tint = Color(0xFF64748B)
                    )
                }
            }
        }
        
        // Name and Role
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = fullName,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = textColor
            )
            
            if (talentType.isNotEmpty()) {
                Text(
                    text = talentType,
                    fontSize = 14.sp,
                    color = subtitleColor
                )
            }
        }
    }
}

@Composable
private fun MenuItem(
    item: DrawerMenuItem,
    isSelected: Boolean,
    selectedBackground: Color,
    textColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor = if (isSelected) selectedBackground else Color.Transparent
    val iconColor = if (isSelected) Color(0xFF3B82F6) else textColor // Blue icon when selected
    
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(backgroundColor)
            .clickable { onClick() }
            .padding(horizontal = 12.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = item.icon,
                contentDescription = item.title,
                modifier = Modifier.size(20.dp),
                tint = iconColor
            )
            
            if (item.type == DrawerMenuItemType.THEME) {
                // Theme item with toggle switch
                ThemeMenuItemContent(
                    title = item.title,
                    isSelected = isSelected,
                    textColor = textColor,
                    modifier = Modifier.weight(1f)
                )
            } else {
                Text(
                    text = item.title,
                    fontSize = 16.sp,
                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                    color = textColor,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun ThemeMenuItemContent(
    title: String,
    isSelected: Boolean,
    textColor: Color,
    modifier: Modifier = Modifier
) {
    val themeViewModel: ThemeViewModel = viewModel(factory = ThemeViewModelFactory())
    val currentTheme by themeViewModel.currentTheme.collectAsState()
    
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            fontSize = 16.sp,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
            color = textColor
        )
        
        // Toggle Switch - shows dark mode state
        val isDarkMode = currentTheme == ThemeType.DARK
        Switch(
            checked = isDarkMode,
            onCheckedChange = { 
                themeViewModel.setTheme(if (it) ThemeType.DARK else ThemeType.LIGHT)
            },
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = Color(0xFF3B82F6),
                uncheckedThumbColor = Color(0xFF94A3B8),
                uncheckedTrackColor = Color(0xFF475569)
            )
        )
    }
}

private fun isItemSelected(
    itemType: DrawerMenuItemType,
    currentRoute: String?
): Boolean {
    return when (itemType) {
        DrawerMenuItemType.PROFILE -> {
            currentRoute == "recruiter_profile" || currentRoute == "talent_profile"
        }
        DrawerMenuItemType.MY_STATS -> currentRoute == "my_stats"
        DrawerMenuItemType.CHAT_BOT -> currentRoute == "chatbot"
        DrawerMenuItemType.SETTINGS -> currentRoute == "settings"
        DrawerMenuItemType.THEME -> currentRoute == "theme"
        DrawerMenuItemType.LOG_OUT -> false // Log out is never selected
    }
}

