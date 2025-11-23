package com.example.matchify.ui.messages

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.FilterList
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.matchify.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessagesScreen(
    onConversationClick: (String) -> Unit = {},
    viewModel: MessagesViewModel = viewModel(factory = MessagesViewModelFactory())
) {
    val conversations by viewModel.conversations.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    
    // Search and filter state
    var searchText by remember { mutableStateOf("") }
    var showFilterMenu by remember { mutableStateOf(false) }
    
    // Load conversations on first appearance
    LaunchedEffect(Unit) {
        viewModel.loadConversations()
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Messages") }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Search Bar and Filter Section
                SearchAndFilterSection(
                    searchText = searchText,
                    onSearchTextChange = { searchText = it },
                    showFilterMenu = showFilterMenu,
                    onFilterMenuToggle = { showFilterMenu = !showFilterMenu },
                    onDismissFilterMenu = { showFilterMenu = false }
                )
                
                // Content
                when {
                    isLoading && conversations.isEmpty() -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                    conversations.isEmpty() -> {
                        EmptyMessagesView()
                    }
                    else -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(conversations) { conversation ->
                                ConversationRow(
                                    conversation = conversation,
                                    isRecruiter = viewModel.isRecruiter,
                                    onClick = { onConversationClick(conversation.conversationId) }
                                )
                            }
                        }
                    }
                }
            }
            
            // Filter Menu Popup - positioned above filter button
            FilterMenuPopup(
                show = showFilterMenu,
                onDismiss = { showFilterMenu = false },
                onFilterSelected = { filterType ->
                    showFilterMenu = false
                    // TODO: Implement filter logic
                }
            )
        }
    }
}

@Composable
private fun ConversationRow(
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
    val unreadCount = conversation.unreadCount
    val isUnread = unreadCount > 0
    
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (!isUnread) {
                MaterialTheme.colorScheme.surface
            } else {
                MaterialTheme.colorScheme.surface.copy(alpha = 0.7f)
            }
        ),
        border = if (isUnread) {
            androidx.compose.foundation.BorderStroke(
                1.dp,
                MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
            )
        } else null
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Profile Image - matching alerts/proposals design exactly
            ProfileImage(
                imageUrl = imageUrl,
                modifier = Modifier.size(50.dp),
                isUnread = isUnread
            )
            
            // Content - matching alerts/proposals design exactly
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = userName,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = if (!isUnread) FontWeight.Normal else FontWeight.SemiBold,
                    maxLines = 2
                )
                
                Text(
                    text = lastMessageText,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2
                )
            }
            
            // Time and Unread Badge - matching iOS layout exactly
            // VStack aligned to trailing (right side)
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.padding(start = 8.dp)
            ) {
                // Timestamp - displayed at top right
                if (timestamp.isNotEmpty()) {
                    Text(
                        text = timestamp,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1
                    )
                }
                
                // Unread badge - displayed below timestamp, matching iOS style
                if (unreadCount > 0) {
                    Surface(
                        color = MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(12.dp), // Capsule shape using high corner radius
                        modifier = Modifier
                            .heightIn(min = 18.dp)
                            .widthIn(min = 18.dp)
                    ) {
                        Text(
                            text = if (unreadCount > 99) "99+" else unreadCount.toString(),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ProfileImage(
    imageUrl: String?,
    modifier: Modifier = Modifier,
    isUnread: Boolean = false
) {
    Box(
        modifier = modifier
            .clip(CircleShape)
            .then(
                if (isUnread) {
                    Modifier.border(
                        2.dp,
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                        CircleShape
                    )
                } else {
                    Modifier
                }
            )
    ) {
        AsyncImage(
            model = imageUrl ?: "",
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .clip(CircleShape),
            contentScale = ContentScale.Crop,
            error = painterResource(id = R.drawable.avatar),
            placeholder = painterResource(id = R.drawable.avatar)
        )
    }
}

@Composable
private fun EmptyMessagesView() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Message,
                contentDescription = null,
                modifier = Modifier.size(60.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
            )
            Text(
                text = "No Messages",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "You have no conversations yet.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun SearchAndFilterSection(
    searchText: String,
    onSearchTextChange: (String) -> Unit,
    showFilterMenu: Boolean,
    onFilterMenuToggle: () -> Unit,
    onDismissFilterMenu: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Search Bar - takes remaining space
            OutlinedTextField(
                value = searchText,
                onValueChange = onSearchTextChange,
                modifier = Modifier.weight(1f),
                placeholder = {
                    Text(
                        text = "Search",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = "Search",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                trailingIcon = {
                    if (searchText.isNotEmpty()) {
                        IconButton(onClick = { onSearchTextChange("") }) {
                            Icon(
                                imageVector = Icons.Filled.Close,
                                contentDescription = "Clear",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    focusedBorderColor = MaterialTheme.colorScheme.outline,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                ),
                textStyle = MaterialTheme.typography.bodyMedium,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search)
            )
            
            // Filter Icon Button
            Surface(
                modifier = Modifier.size(48.dp),
                onClick = onFilterMenuToggle,
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surface,
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    MaterialTheme.colorScheme.outline
                )
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.FilterList,
                        contentDescription = "Filter",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun FilterMenuPopup(
    show: Boolean,
    onDismiss: () -> Unit,
    onFilterSelected: (FilterType) -> Unit
) {
    // Overlay to dismiss when clicking outside
    if (show) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) { onDismiss() }
                .zIndex(998f)
        )
    }
    
    // Menu positioned at top-right, aligned with filter button
    // Positioned at 68.dp from top (padding from Scaffold) + 12.dp (section padding) + 48.dp (filter button height) + 8.dp offset
    Box(
        modifier = Modifier
            .fillMaxSize()
            .zIndex(999f),
        contentAlignment = Alignment.TopEnd
    ) {
        AnimatedVisibility(
            visible = show,
            enter = fadeIn(animationSpec = tween(200)) + scaleIn(
                initialScale = 0.85f,
                animationSpec = tween(200)
            ),
            exit = fadeOut(animationSpec = tween(150)) + scaleOut(
                targetScale = 0.85f,
                animationSpec = tween(150)
            ),
            modifier = Modifier.padding(top = 76.dp, end = 16.dp)
        ) {
        Card(
            modifier = Modifier
                .width(180.dp)
                .shadow(
                    elevation = 8.dp,
                    shape = RoundedCornerShape(12.dp)
                ),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                // Unread Filter
                FilterMenuItem(
                    icon = Icons.Outlined.Email,
                    title = "Unread",
                    onClick = { onFilterSelected(FilterType.UNREAD) }
                )
                
                HorizontalDivider(
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f),
                    thickness = 0.5.dp
                )
                
                // Favourite Filter
                FilterMenuItem(
                    icon = Icons.Filled.Star,
                    title = "Favourite",
                    onClick = { onFilterSelected(FilterType.FAVOURITE) }
                )
                
                HorizontalDivider(
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f),
                    thickness = 0.5.dp
                )
                
                // Messages Filter
                FilterMenuItem(
                    icon = Icons.Filled.Message,
                    title = "Messages",
                    onClick = { onFilterSelected(FilterType.MESSAGES) }
                )
            }
        }
        }
    }
}

@Composable
private fun FilterMenuItem(
    icon: ImageVector,
    title: String,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        color = MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 13.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

enum class FilterType {
    UNREAD,
    FAVOURITE,
    MESSAGES
}