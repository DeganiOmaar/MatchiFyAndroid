package com.example.matchify.ui.messages

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.matchify.data.local.AuthPreferencesProvider
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun MessagesScreen(
    onConversationClick: (String) -> Unit = {},
    onMessagesViewed: () -> Unit = {},
    viewModel: MessagesViewModel = viewModel(factory = MessagesViewModelFactory())
) {
    val conversations by viewModel.conversations.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    
    // Search state
    var searchText by remember { androidx.compose.runtime.mutableStateOf("") }
    
    // Get current user profile image
    val prefs = remember { AuthPreferencesProvider.getInstance().get() }
    val currentUser by prefs.user.collectAsState(initial = null)
    val currentUserProfileImage = currentUser?.profileImageUrl
    
    LaunchedEffect(Unit) {
        viewModel.loadConversations()
        // Marquer les messages comme vus quand l'utilisateur ouvre l'écran
        viewModel.markMessagesAsViewed()
        // Rafraîchir le compteur de badge
        onMessagesViewed()
    }
    
    // Filter conversations by talent name
    val filteredConversations = remember(conversations, searchText, viewModel.isRecruiter) {
        if (searchText.isBlank()) {
            conversations
        } else {
            conversations.filter { conversation ->
                val otherUserName = conversation.getOtherUserName(viewModel.isRecruiter)
                otherUserName.contains(searchText, ignoreCase = true)
            }
        }
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F7FA))
    ) {
        // Header with gradient background, wavy patterns, and wave bottom
        HeaderSection(
            currentUserProfileImage = currentUserProfileImage,
            searchText = searchText,
            onSearchTextChange = { searchText = it }
        )
        
        // Recent section
        RecentSection()
        
        // Conversations list
        when {
            isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            filteredConversations.isEmpty() -> {
                EmptyMessagesView()
            }
            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredConversations) { conversation ->
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
}

@Composable
private fun HeaderSection(
    currentUserProfileImage: String?,
    searchText: String,
    onSearchTextChange: (String) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .background(Color(0xFFF5F7FA))
    ) {
        // Content - Only profile image and search bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(top = 50.dp)
                .align(Alignment.TopCenter),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Profile image on the right
            AsyncImage(
                model = currentUserProfileImage,
                contentDescription = "Profile",
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color.White, CircleShape)
                    .border(
                        width = 2.dp,
                        color = Color.White,
                        shape = CircleShape
                    ),
                contentScale = androidx.compose.ui.layout.ContentScale.Crop
            )
        }
        
        // Search bar centered below profile image
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(top = 110.dp)
                .align(Alignment.TopCenter)
        ) {
            SearchBar(
                searchText = searchText,
                onSearchTextChange = onSearchTextChange
            )
        }
    }
}

@Composable
private fun SearchBar(
    searchText: String,
    onSearchTextChange: (String) -> Unit
) {
    OutlinedTextField(
        value = searchText,
        onValueChange = onSearchTextChange,
        modifier = Modifier.fillMaxWidth(),
        placeholder = {
            Text(
                text = "Search",
                color = Color.Gray
            )
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Filled.Search,
                contentDescription = "Search",
                tint = Color.Gray
            )
        },
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            focusedBorderColor = Color(0xFF61A5C2),
            unfocusedBorderColor = Color.Gray.copy(alpha = 0.3f)
        ),
        singleLine = true
    )
}

@Composable
private fun RecentSection() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Recent",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1A1A1A)
        )
    }
}

@Composable
private fun ConversationRow(
    conversation: com.example.matchify.domain.model.Conversation,
    isRecruiter: Boolean,
    onClick: () -> Unit,
    unreadCount: Int = 0 // Default to 0, can be enhanced later
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Profile image with dashed border
        val imageUrl = conversation.getOtherUserProfileImageURL(
            isRecruiter = isRecruiter,
            baseURL = "http://10.0.2.2:3000"
        )
        
        Box {
            // Profile image
            AsyncImage(
                model = imageUrl,
                contentDescription = null,
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape),
                contentScale = androidx.compose.ui.layout.ContentScale.Crop
            )
            
            // Dashed border using Canvas
            Canvas(
                modifier = Modifier.size(56.dp)
            ) {
                val radius = size.minDimension / 2f - 2.dp.toPx()
                val center = Offset(size.width / 2f, size.height / 2f)
                val strokeWidth = 2.dp.toPx()
                
                drawCircle(
                    color = Color(0xFF8B4513).copy(alpha = 0.6f),
                    radius = radius,
                    center = center,
                    style = Stroke(
                        width = strokeWidth,
                        pathEffect = PathEffect.dashPathEffect(
                            intervals = floatArrayOf(8f, 8f),
                            phase = 0f
                        ),
                        cap = StrokeCap.Round
                    )
                )
            }
        }
        
        // Content
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = conversation.getOtherUserName(isRecruiter),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF1A1A1A),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = conversation.lastMessageText ?: "No messages yet",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        
        // Timestamp and unread badge
        Column(
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = formatTimestamp(conversation.lastMessageAt),
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray,
                fontSize = 12.sp
            )
            
            // Unread badge
            if (unreadCount > 0) {
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.error),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (unreadCount > 99) "99+" else unreadCount.toString(),
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
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
        Text(
            text = "No messages yet!",
            style = MaterialTheme.typography.titleLarge,
            color = Color.Gray
        )
    }
}

private fun formatTimestamp(lastMessageAt: String?): String {
    if (lastMessageAt == null) return ""
    
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
        inputFormat.timeZone = TimeZone.getTimeZone("UTC")
        val date = inputFormat.parse(lastMessageAt) ?: return ""
        
        val now = Date()
        val timeInterval = now.time - date.time
        
        val minutes = (timeInterval / 60000L).toInt()
        val hours = (timeInterval / 3600000L).toInt()
        val days = (timeInterval / 86400000L).toInt()
        
        when {
            minutes < 1 -> "Just now"
            minutes < 60 -> "${minutes}mins"
            hours < 24 -> {
                val hourFormat = SimpleDateFormat("h:mma", Locale.US)
                val formatted = hourFormat.format(date)
                // Format like "3:00PM" instead of "3:00pm"
                formatted.replace("am", "AM").replace("pm", "PM")
            }
            days == 1 -> "Yesterday"
            days < 7 -> {
                val dayFormat = SimpleDateFormat("EEEE", Locale.US)
                dayFormat.format(date)
            }
            else -> {
                val dateFormat = SimpleDateFormat("M/d/yy", Locale.US)
                dateFormat.format(date)
            }
        }
    } catch (e: Exception) {
        ""
    }
}
