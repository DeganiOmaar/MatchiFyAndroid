package com.example.matchify.ui.alerts

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.Image
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.matchify.R
import com.example.matchify.domain.model.Alert
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
@Composable
fun AlertsScreen(
    onAlertClick: (String) -> Unit = {},
    onDrawerItemSelected: (com.example.matchify.ui.missions.components.DrawerMenuItemType) -> Unit = {},
    viewModel: AlertsViewModel = viewModel(factory = AlertsViewModelFactory())
) {
    val alerts by viewModel.alerts.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val unreadCount by viewModel.unreadCount.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    
    // Get user for avatar
    val context = androidx.compose.ui.platform.LocalContext.current
    val prefs = remember { com.example.matchify.data.local.AuthPreferencesProvider.getInstance().get() }
    val user by prefs.user.collectAsState(initial = null)
    
    // Drawer state
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    
    LaunchedEffect(Unit) {
        viewModel.loadAlerts()
    }
    
    // Dark theme colors - matching Proposals and Messages screens
    val darkBackground = Color(0xFF0F172A) // Same as Proposals and Messages
    val cardBackground = Color(0xFF111827) // Slightly lighter for cards
    val textPrimary = Color(0xFFFFFFFF)
    val textSecondary = Color(0xFFB4B4B4)
    val blueDot = Color(0xFF4A90E2)
    val redDot = Color(0xFFE74C3C)
    
    // Navigation Drawer wraps the entire content
    com.example.matchify.ui.missions.components.NewDrawerContent(
        drawerState = drawerState,
        currentRoute = null,
        onClose = {
            scope.launch {
                drawerState.close()
            }
        },
        onMenuItemSelected = { itemType ->
            scope.launch {
                drawerState.close()
            }
            onDrawerItemSelected(itemType)
        }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(darkBackground)
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Custom AppBar with Mark All Read button
                com.example.matchify.ui.components.CustomAppBar(
                    title = "Alerts",
                    profileImageUrl = user?.profileImageUrl,
                    onProfileClick = {
                        scope.launch {
                            drawerState.open()
                        }
                    },
                    rightAction = if (unreadCount > 0) {
                        {
                            TextButton(
                                onClick = { viewModel.markAllAsRead() },
                                colors = ButtonDefaults.textButtonColors(
                                    contentColor = Color(0xFF4A90E2)
                                )
                            ) {
                                Text(
                                    text = "Mark all",
                                    fontSize = 14.sp
                                )
                            }
                        }
                    } else null
                )
                
            // Content
            when {
                isLoading && alerts.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = Color(0xFF4A90E2)
                        )
                    }
                }
                errorMessage != null -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = errorMessage ?: "Une erreur est survenue",
                            color = Color(0xFFE74C3C)
                        )
                    }
                }
                alerts.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No alerts",
                            color = textSecondary,
                            fontSize = 16.sp
                        )
                    }
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(alerts) { alert ->
                            AlertCard(
                                alert = alert,
                                darkBackground = darkBackground,
                                cardBackground = cardBackground,
                                textPrimary = textPrimary,
                                textSecondary = textSecondary,
                                blueDot = blueDot,
                                redDot = redDot,
                                onClick = {
                                    viewModel.markAsRead(alert.id)
                                    onAlertClick(alert.proposalId)
                                }
                            )
                        }
                    }
                }
                }
            }
        }
    }
}

@Composable
fun AlertCard(
    alert: Alert,
    darkBackground: Color,
    cardBackground: Color,
    textPrimary: Color,
    textSecondary: Color,
    blueDot: Color,
    redDot: Color,
    onClick: () -> Unit
) {
    val isAccepted = alert.type == Alert.AlertType.PROPOSAL_ACCEPTED
    val isRejected = alert.type == Alert.AlertType.PROPOSAL_REFUSED
    val isUnread = !alert.isRead
    
    // Determine dot color based on alert type
    val dotColor = when {
        isAccepted -> blueDot
        isRejected -> redDot
        else -> blueDot
    }
    
    // Determine icon background color based on alert type
    val iconBackgroundColor = when {
        isAccepted -> Color(0xFF2D5A3D) // Dark green (like in screenshot)
        isRejected -> Color(0xFFF5F5DC) // Beige/light yellow (like in screenshot)
        else -> Color(0xFFE8E8D0) // Light beige for other types
    }
    
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = cardBackground
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Unread dot indicator - positioned to the left of icon
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .align(Alignment.CenterVertically)
            ) {
                if (isUnread) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                            .background(dotColor)
                    )
                }
            }
            
            // Recruiter icon
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(iconBackgroundColor),
                contentAlignment = Alignment.Center
            ) {
                if (!alert.recruiterProfileImage.isNullOrEmpty()) {
                    AsyncImage(
                        model = alert.recruiterProfileImage,
                        contentDescription = null,
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop,
                        error = painterResource(id = R.drawable.avatar),
                        placeholder = painterResource(id = R.drawable.avatar)
                    )
                } else {
                    // Use avatar.png as placeholder when no recruiter image
                    Image(
                        painter = painterResource(id = R.drawable.avatar),
                        contentDescription = null,
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                }
            }
            
            // Content
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Title
                Text(
                    text = alert.title,
                    color = textPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )
                
                // Description
                Text(
                    text = alert.message,
                    color = textSecondary,
                    fontSize = 14.sp,
                    maxLines = 2
                )
            }
            
            // Timestamp - aligned to right
            Text(
                text = formatTimestamp(alert.createdAt),
                color = textSecondary,
                fontSize = 12.sp,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    }
}

fun formatTimestamp(dateString: String?): String {
    if (dateString == null) return ""
    
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
        inputFormat.timeZone = TimeZone.getTimeZone("UTC")
        val date = inputFormat.parse(dateString)
        
        if (date == null) return ""
        
        val now = Date()
        val diffMinutes = (now.time - date.time) / (1000 * 60) // diff in minutes
        
        when {
            diffMinutes < 1 -> "Just now"
            diffMinutes < 60 -> "${diffMinutes}m ago"
            diffMinutes < 1440 -> {
                val hours = diffMinutes / 60
                "${hours}h ago"
            }
            diffMinutes < 2880 -> "Yesterday"
            else -> {
                val outputFormat = SimpleDateFormat("MM/dd/yy", Locale.US)
                outputFormat.format(date)
            }
        }
    } catch (e: Exception) {
        ""
    }
}

