package com.example.matchify.ui.missions.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.matchify.data.local.AuthPreferencesProvider
import com.example.matchify.domain.model.UserModel

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
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val type: DrawerMenuItemType
) {
    companion object {
        fun recruiterItems(): List<DrawerMenuItem> = listOf(
            DrawerMenuItem("Profile", Icons.Default.Person, DrawerMenuItemType.PROFILE),
            DrawerMenuItem("Chat Bot", Icons.Default.Message, DrawerMenuItemType.CHAT_BOT),
            DrawerMenuItem("Settings", Icons.Default.Settings, DrawerMenuItemType.SETTINGS),
            DrawerMenuItem("Theme", Icons.Default.Palette, DrawerMenuItemType.THEME),
            DrawerMenuItem("Log out", Icons.Default.ExitToApp, DrawerMenuItemType.LOG_OUT)
        )
        
        fun talentItems(): List<DrawerMenuItem> = listOf(
            DrawerMenuItem("Profile", Icons.Default.Person, DrawerMenuItemType.PROFILE),
            DrawerMenuItem("My Stats", Icons.Default.Assessment, DrawerMenuItemType.MY_STATS),
            DrawerMenuItem("Chat Bot", Icons.Default.Message, DrawerMenuItemType.CHAT_BOT),
            DrawerMenuItem("Settings", Icons.Default.Settings, DrawerMenuItemType.SETTINGS),
            DrawerMenuItem("Theme", Icons.Default.Palette, DrawerMenuItemType.THEME),
            DrawerMenuItem("Log out", Icons.Default.ExitToApp, DrawerMenuItemType.LOG_OUT)
        )
    }
}

@Composable
fun ProfileDrawer(
    onClose: () -> Unit,
    onMenuItemSelected: (DrawerMenuItemType) -> Unit = {},
    modifier: Modifier = Modifier
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
    val talentType = user?.talent?.firstOrNull()
    val profileImageUrl = user?.profileImageUrl
    
    Surface(
        modifier = modifier
            .fillMaxHeight()
            .width(280.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 4.dp,
        shape = RoundedCornerShape(topEnd = 20.dp, bottomEnd = 20.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // User Section
            UserSection(
                fullName = fullName,
                talentType = talentType,
                profileImageUrl = profileImageUrl,
                isRecruiter = isRecruiter,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 40.dp, start = 20.dp, end = 20.dp, bottom = 24.dp)
            )
            
            Divider(
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                thickness = 1.dp
            )
            
            // Menu Items
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(top = 8.dp),
                verticalArrangement = Arrangement.spacedBy(0.dp)
            ) {
                itemsIndexed(menuItems) { index, item ->
                    MenuItemRow(
                        item = item,
                        onClick = {
                            onMenuItemSelected(item.type)
                        }
                    )
                    
                    if (index < menuItems.size - 1) {
                        Divider(
                            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                            modifier = Modifier.padding(start = 56.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun UserSection(
    fullName: String,
    talentType: String?,
    profileImageUrl: String?,
    isRecruiter: Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Profile Image
        AsyncImage(
            model = profileImageUrl,
            contentDescription = null,
            modifier = Modifier
                .size(80.dp)
                .background(
                    MaterialTheme.colorScheme.surfaceContainerHighest,
                    CircleShape
                )
                .clip(CircleShape),
            contentScale = androidx.compose.ui.layout.ContentScale.Crop
        )
        
        // Name and Talent Type
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = fullName,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            if (!isRecruiter && talentType != null) {
                Text(
                    text = talentType,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun MenuItemRow(
    item: DrawerMenuItem,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        color = MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = item.icon,
                contentDescription = item.title,
                modifier = Modifier.size(24.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Text(
                text = item.title,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f)
            )
        }
    }
}
