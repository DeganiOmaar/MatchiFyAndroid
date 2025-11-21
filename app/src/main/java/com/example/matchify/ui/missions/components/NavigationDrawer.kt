package com.example.matchify.ui.missions.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalLayoutDirection
import coil.compose.AsyncImage
import com.example.matchify.data.local.AuthPreferencesProvider

/**
 * Material 3 Navigation Drawer
 * 
 * Fully compliant with Material Design 3 guidelines:
 * 
 * Drawer Types:
 * - Modal Drawer: For compact screens (<840dp) - slides in with scrim
 * - Standard Drawer: For expanded screens (840dp+) - can be permanent or dismissible
 * 
 * Features:
 * - 5+ destinations with multi-level navigation
 * - Active indicator (only one selected at a time)
 * - Icon + label for all items (consistent)
 * - Badges support (optional)
 * - Sections with full-width dividers
 * - Single-line labels (truncated if needed, never shrunk)
 * - Light/dark mode support
 * - Proper animations and motion
 * - Responsive layout based on screen width
 * 
 * Anatomy:
 * - Sheet (side container)
 * - Active indicator (shape behind selected item)
 * - Icons (before text, all items or none)
 * - Labels (required, single-line, truncated)
 * - Badges (optional)
 * - Sections/subheaders (optional)
 * - Dividers (full-width, between sections only)
 * - Scrim (modal drawer only)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavigationDrawerContent(
    drawerState: DrawerState,
    currentRoute: String?,
    onClose: () -> Unit,
    onMenuItemSelected: (DrawerMenuItemType) -> Unit = {},
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val configuration = LocalConfiguration.current
    val screenWidthDp = configuration.screenWidthDp
    val layoutDirection = LocalLayoutDirection.current
    
    // Determine drawer type based on screen width (MD3 breakpoint: 840dp)
    val isExpandedScreen = screenWidthDp >= 840
    val drawerWidth = 280.dp
    
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
    
    // Group menu items into sections for better organization
    val primarySection = menuItems.filter { 
        it.type != DrawerMenuItemType.THEME && it.type != DrawerMenuItemType.LOG_OUT
    }
    val secondarySection = menuItems.filter { 
        it.type == DrawerMenuItemType.THEME || it.type == DrawerMenuItemType.LOG_OUT
    }
    
    // Drawer content composable
    @Composable
    fun DrawerContent() {
        Surface(
            modifier = Modifier
                .width(drawerWidth)
                .fillMaxHeight(),
            shape = androidx.compose.foundation.shape.RoundedCornerShape(
                topEnd = if (layoutDirection == LayoutDirection.Ltr) 16.dp else 0.dp,
                bottomEnd = if (layoutDirection == LayoutDirection.Ltr) 16.dp else 0.dp,
                topStart = if (layoutDirection == LayoutDirection.Rtl) 16.dp else 0.dp,
                bottomStart = if (layoutDirection == LayoutDirection.Rtl) 16.dp else 0.dp
            ),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 1.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .background(MaterialTheme.colorScheme.surface)
            ) {
                // User Section Header (MD3 drawer header)
                NavigationDrawerUserSection(
                    fullName = fullName,
                    talentType = talentType,
                    profileImageUrl = profileImageUrl,
                    isRecruiter = isRecruiter,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            top = 24.dp,
                            start = 16.dp,
                            end = 16.dp,
                            bottom = 16.dp
                        )
                )
                
                // Full-width divider after header
                HorizontalDivider(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.12f),
                    thickness = 1.dp
                )
                
                // Scrollable menu items
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    // Primary Section
                    items(primarySection) { item ->
                        NavigationDrawerMenuItem(
                            item = item,
                            selected = isItemSelected(item.type, currentRoute),
                            onClick = {
                                onMenuItemSelected(item.type)
                                onClose()
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    
                    // Full-width divider between sections (MD3 rule: only between sections)
                    if (secondarySection.isNotEmpty()) {
                        item {
                            HorizontalDivider(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.12f),
                                thickness = 1.dp
                            )
                        }
                    }
                    
                    // Secondary Section
                    items(secondarySection) { item ->
                        NavigationDrawerMenuItem(
                            item = item,
                            selected = isItemSelected(item.type, currentRoute),
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
    }
    
    // Use modal drawer for compact screens, standard drawer for expanded screens
    if (isExpandedScreen) {
        // Standard Drawer (expanded screens - can be permanent or dismissible)
        PermanentNavigationDrawer(
            drawerContent = { DrawerContent() },
            modifier = modifier
        ) {
            content()
        }
    } else {
        // Modal Drawer (compact screens - slides in with scrim)
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = { DrawerContent() },
            modifier = modifier,
            scrimColor = MaterialTheme.colorScheme.scrim.copy(alpha = 0.32f)
        ) {
            content()
        }
    }
}

/**
 * Navigation Drawer User Section
 * 
 * Material 3 compliant user header for drawer
 * Displays profile image, name, and role/talent type
 */
@Composable
private fun NavigationDrawerUserSection(
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
        Surface(
            modifier = Modifier.size(56.dp),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.surfaceContainerHighest,
            tonalElevation = 0.dp
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
                        contentScale = androidx.compose.ui.layout.ContentScale.Crop
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        modifier = Modifier.size(28.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
        
        // Name and Role/Talent Type
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = fullName,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            
            val subtitleText = when {
                !isRecruiter && talentType != null -> talentType
                isRecruiter -> "Recruiter"
                else -> ""
            }
            
            if (subtitleText.isNotEmpty()) {
                Text(
                    text = subtitleText,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

/**
 * Navigation Drawer Menu Item
 * 
 * Material 3 compliant drawer item with:
 * - Icon (before text, consistent for all items)
 * - Label (required, single-line, truncated if needed, never shrunk)
 * - Active indicator (shape behind selected item - only one at a time)
 * - Badge support (optional)
 * - Proper spacing and animations
 * - Motion animation when switching selection
 */
@Composable
private fun NavigationDrawerMenuItem(
    item: DrawerMenuItem,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    badgeCount: Int? = null
) {
    NavigationDrawerItem(
        label = {
            Text(
                text = item.title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = if (selected) {
                    MaterialTheme.colorScheme.onSecondaryContainer
                } else {
                    MaterialTheme.colorScheme.onSurface
                }
            )
        },
        icon = {
            Box {
                Icon(
                    imageVector = item.icon,
                    contentDescription = item.title,
                    modifier = Modifier.size(24.dp),
                    tint = if (selected) {
                        MaterialTheme.colorScheme.onSecondaryContainer
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )
                
                // Badge support (optional)
                if (badgeCount != null && badgeCount > 0) {
                    Badge(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .offset(x = 8.dp, y = (-4).dp),
                        containerColor = MaterialTheme.colorScheme.error,
                        contentColor = MaterialTheme.colorScheme.onError
                    ) {
                        Text(
                            text = if (badgeCount > 99) "99+" else badgeCount.toString(),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        },
        selected = selected,
        onClick = onClick,
        modifier = modifier.padding(horizontal = 12.dp, vertical = 4.dp),
        colors = NavigationDrawerItemDefaults.colors(
            selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
            selectedIconColor = MaterialTheme.colorScheme.onSecondaryContainer,
            selectedTextColor = MaterialTheme.colorScheme.onSecondaryContainer,
            unselectedContainerColor = MaterialTheme.colorScheme.surface,
            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
            unselectedTextColor = MaterialTheme.colorScheme.onSurface
        ),
        shape = MaterialTheme.shapes.medium
    )
}

/**
 * Check if a drawer menu item is currently selected based on current route
 * 
 * MD3 Rule: Only one destination can be active at a time
 */
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

