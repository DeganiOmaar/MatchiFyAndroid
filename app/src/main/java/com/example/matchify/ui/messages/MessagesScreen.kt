package com.example.matchify.ui.messages

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.matchify.R
import kotlinx.coroutines.launch

@Composable
fun MessagesScreen(
    onConversationClick: (String) -> Unit = {},
    onDrawerItemSelected: (com.example.matchify.ui.missions.components.DrawerMenuItemType) -> Unit = {},
    viewModel: MessagesViewModel = viewModel(factory = MessagesViewModelFactory())
) {
    val conversations by viewModel.conversations.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    
    var searchText by remember { mutableStateOf("") }
    
    // Get user for avatar
    val context = androidx.compose.ui.platform.LocalContext.current
    val prefs = remember { com.example.matchify.data.local.AuthPreferencesProvider.getInstance().get() }
    val user by prefs.user.collectAsState(initial = null)
    
    // Drawer state
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    
    // Load conversations on first appearance
    LaunchedEffect(Unit) {
        viewModel.loadConversations()
    }
    
    // Background color: #0F172A
    val backgroundColor = Color(0xFF0F172A)
    
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
                .background(backgroundColor)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                // Custom AppBar
                com.example.matchify.ui.components.CustomAppBar(
                    title = "Messages",
                    profileImageUrl = user?.profileImageUrl,
                    onProfileClick = {
                        scope.launch {
                            drawerState.open()
                        }
                    }
                )
                
            // Search Bar
            SearchBar(
                searchText = searchText,
                onSearchTextChange = { searchText = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp)
            )
            
            // Divider under search
            HorizontalDivider(
                color = Color(0xFF1E293B),
                thickness = 1.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            )
            
            // Message List
            when {
                isLoading && conversations.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = Color.White
                        )
                    }
                }
                conversations.isEmpty() -> {
                    EmptyMessagesView()
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(
                            horizontal = 16.dp,
                            vertical = 8.dp
                        ),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(conversations) { conversation ->
                            ConversationRow(
                                conversation = conversation,
                                isRecruiter = viewModel.isRecruiter,
                                onClick = { onConversationClick(conversation.conversationId) }
                            )
                            // Divider between conversations
                            HorizontalDivider(
                                color = Color(0xFF1E293B),
                                thickness = 1.dp,
                                modifier = Modifier.padding(horizontal = 16.dp)
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
private fun SearchBar(
    searchText: String,
    onSearchTextChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    // Search bar container: height 48dp, background #1E293B, border radius 24dp
    Box(
        modifier = modifier
            .height(48.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(Color(0xFF1E293B))
    ) {
        // TextField for input
        BasicTextField(
            value = searchText,
            onValueChange = onSearchTextChange,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            textStyle = androidx.compose.ui.text.TextStyle(
                fontSize = 14.sp,
                color = Color.White
            ),
            singleLine = true,
            decorationBox = { innerTextField ->
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(start = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ) {
                    // Search icon - always on the left
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        modifier = Modifier.size(20.dp),
                        tint = Color(0xFF9CA3AF)
                    )
                    
                    Spacer(modifier = Modifier.width(12.dp))
                    
                    // Text input or placeholder
                    Box(modifier = Modifier.weight(1f)) {
                        if (searchText.isEmpty()) {
                            Text(
                                text = "Search",
                                fontSize = 14.sp,
                                fontWeight = FontWeight(400),
                                color = Color(0xFF9CA3AF)
                            )
                        }
                        innerTextField()
                    }
                }
            }
        )
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
    
    // Row height: 72-80dp, padding 16dp horizontal
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 72.dp, max = 80.dp)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Avatar with online indicator
        Box(
            modifier = Modifier.size(48.dp)
        ) {
            // Avatar - diameter 48dp, circle, no border
            AsyncImage(
                model = imageUrl ?: "",
                contentDescription = null,
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop,
                error = painterResource(id = R.drawable.avatar),
                placeholder = painterResource(id = R.drawable.avatar)
            )
            
            // Online indicator - green dot if online (for now, we'll hide it as we don't have online status)
            // Uncomment and add online status logic when available
            /*
            if (isOnline) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF22C55E))
                        .align(Alignment.BottomEnd)
                        .offset(x = (-2).dp, y = (-2).dp)
                )
            }
            */
        }
        
        // Middle Column - Name + Last Message
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            // Name - 16sp, weight 600, color #FFFFFF, single line
            Text(
                text = userName,
                fontSize = 16.sp,
                fontWeight = FontWeight(600),
                color = Color.White,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            
            // Last message preview - 14sp, weight 400, color #9CA3AF, max 1 line
            Text(
                text = lastMessageText,
                fontSize = 14.sp,
                fontWeight = FontWeight(400),
                color = Color(0xFF9CA3AF),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        
        // Right Column - Time + Unread Badge
        Column(
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // Time label - 12sp, weight 400, color #9CA3AF
            if (timestamp.isNotEmpty()) {
                Text(
                    text = timestamp,
                    fontSize = 12.sp,
                    fontWeight = FontWeight(400),
                    color = Color(0xFF9CA3AF),
                    maxLines = 1
                )
            }
            
            // Unread count badge - only show when unreadCount > 0
            if (unreadCount > 0) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .background(Color(0xFF2563EB), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (unreadCount > 99) "99+" else unreadCount.toString(),
                        fontSize = 11.sp,
                        fontWeight = FontWeight(600),
                        color = Color.White
                    )
                }
            }
        }
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
                imageVector = Icons.Default.Search,
                contentDescription = null,
                modifier = Modifier.size(60.dp),
                tint = Color(0xFF9CA3AF).copy(alpha = 0.5f)
            )
            Text(
                text = "No Messages",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )
            Text(
                text = "You have no conversations yet.",
                fontSize = 14.sp,
                color = Color(0xFF9CA3AF)
            )
        }
    }
}
