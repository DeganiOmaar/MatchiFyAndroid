package com.example.matchify.ui.messages

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import com.example.matchify.R
import androidx.lifecycle.viewmodel.compose.viewModel

/**
 * Material Design 3 Messages Screen
 * 
 * Fully compliant with MD3 list items specification:
 * - MD3 List Items (two-line pattern)
 * - Circular avatars (56dp)
 * - MD3 typography (Title Medium, Body Medium, Label Small)
 * - MD3 spacing (16dp padding, 16dp between avatar and text)
 * - Red unread badge (errorContainer/error)
 * - Timestamp styling (onSurfaceVariant)
 * - Ripple & interaction states
 * - Light & dark mode adaptive colors
 * - Dividers between items
 * - MD3 scroll behavior
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessagesScreen(
    onConversationClick: (String) -> Unit = {},
    viewModel: MessagesViewModel = viewModel(factory = MessagesViewModelFactory())
) {
    val conversations by viewModel.conversations.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    
    LaunchedEffect(Unit) {
        viewModel.loadConversations()
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Messages",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.surface
    ) { paddingValues ->
        when {
            isLoading && conversations.isEmpty() -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
            conversations.isEmpty() -> {
                EmptyMessagesView(
                    modifier = Modifier.padding(paddingValues)
                )
            }
            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentPadding = PaddingValues(vertical = 0.dp),
                    verticalArrangement = Arrangement.spacedBy(0.dp)
                ) {
                    items(
                        items = conversations,
                        key = { it.conversationId }
                    ) { conversation ->
                        MD3ConversationListItem(
                            conversation = conversation,
                            isRecruiter = viewModel.isRecruiter,
                            onClick = { onConversationClick(conversation.conversationId) }
                        )
                        
                        // Full-width divider (MD3 style) - between items except last
                        if (conversation != conversations.last()) {
                            HorizontalDivider(
                                modifier = Modifier.fillMaxWidth(),
                                color = MaterialTheme.colorScheme.outlineVariant,
                                thickness = 0.5.dp
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Material Design 3 Conversation List Item
 * 
 * Two-line list item pattern following MD3 specifications:
 * - Leading: Circular avatar (56dp) with 16dp padding from edge
 * - Headline: Contact/group name (Title Medium, MD3 type scale)
 * - Supporting: Message preview (Body Medium, truncated to one line)
 * - Trailing: Timestamp (Label Small, onSurfaceVariant) + Unread badge (errorContainer)
 * 
 * MD3 spacing rules:
 * - 16dp outer padding (horizontal)
 * - 16dp between avatar and text column
 * - 4-8dp vertical spacing between headline and supporting text
 * - Trailing column right-aligned
 * 
 * MD3 interaction states:
 * - Ripple effect on click
 * - Hover, focus, pressed states via ListItem
 * - State layers based on surface tonality
 */
@Composable
private fun MD3ConversationListItem(
    conversation: com.example.matchify.domain.model.Conversation,
    isRecruiter: Boolean,
    onClick: () -> Unit
) {
    // Get conversation data
    val userName = conversation.getOtherUserName(isRecruiter)
    val imageUrl = conversation.getOtherUserProfileImageURL(
        isRecruiter = isRecruiter,
        baseURL = "http://10.0.2.2:3000"
    )
    val lastMessageText = conversation.lastMessageText ?: "No messages yet"
    val timestamp = conversation.formattedLastMessageTime
    // TODO: Get unreadCount from backend when available
    val unreadCount = 0
    
    ListItem(
        headlineContent = {
            Text(
                text = userName,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        supportingContent = {
            Text(
                text = lastMessageText,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = 4.dp)
            )
        },
        leadingContent = {
            // Circular Avatar - 56dp (MD3 standard size)
            // 16dp padding from edge is handled by ListItem
            SubcomposeAsyncImage(
                model = imageUrl,
                contentDescription = userName,
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop,
                loading = {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                MaterialTheme.colorScheme.surfaceContainerHighest,
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                error = {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                MaterialTheme.colorScheme.surfaceContainerHighest,
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.avatar),
                            contentDescription = null,
                            modifier = Modifier.size(32.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                        )
                    }
                },
                success = {
                    SubcomposeAsyncImageContent()
                }
            )
        },
        trailingContent = {
            // Trailing column: Timestamp (above) + Unread badge (below)
            // Right-aligned, vertically centered
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.padding(start = 16.dp)
            ) {
                // Timestamp - MD3 Label Small, neutral tone (onSurfaceVariant)
                Text(
                    text = timestamp,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )
                
                // Unread badge - MD3 red error badge
                // Background: errorContainer (light) / error (dark)
                // Text: onErrorContainer
                // Only show badge if there are unread messages
                if (unreadCount > 0) {
                    Badge(
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        contentColor = MaterialTheme.colorScheme.onErrorContainer
                    ) {
                        Text(
                            text = if (unreadCount > 99) "99+" else unreadCount.toString(),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                        )
                    }
                }
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = ListItemDefaults.colors(
            containerColor = MaterialTheme.colorScheme.surface,
            headlineColor = MaterialTheme.colorScheme.onSurface,
            supportingColor = MaterialTheme.colorScheme.onSurfaceVariant
        )
    )
}

/**
 * Empty Messages State - MD3 compliant
 */
@Composable
private fun EmptyMessagesView(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(40.dp)
        ) {
            Surface(
                modifier = Modifier.size(80.dp),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.surfaceContainerHighest,
                tonalElevation = 0.dp
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.avatar),
                        contentDescription = null,
                        modifier = Modifier.size(40.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                }
            }
            
            Text(
                text = "No messages yet",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Text(
                text = "Start a conversation by viewing a proposal",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 20.dp)
            )
        }
    }
}