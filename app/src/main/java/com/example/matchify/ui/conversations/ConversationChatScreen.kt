package com.example.matchify.ui.conversations

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.DriveFileMoveRtl
import androidx.compose.material.icons.filled.InsertDriveFile
import androidx.compose.material.icons.filled.Send
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.matchify.data.local.AuthPreferencesProvider

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConversationChatScreen(
    conversationId: String,
    onBack: () -> Unit,
    onCreateContractClick: () -> Unit = {},
    onContractReview: (String) -> Unit = {},
    viewModel: ConversationChatViewModel = viewModel(
        factory = ConversationChatViewModelFactory(conversationId)
    )
) {
    val messages by viewModel.messages.collectAsState()
    val conversation by viewModel.conversation.collectAsState()
    val messageText by viewModel.messageText.collectAsState()
    val isSending by viewModel.isSending.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    
    val prefs = remember { AuthPreferencesProvider.getInstance().get() }
    val currentUser by prefs.user.collectAsState(initial = null)
    
    // Get avatar URLs
    val currentUserAvatarUrl = currentUser?.profileImageUrl
    
    val otherUserAvatarUrl = conversation?.getOtherUserProfileImageURL(
        isRecruiter = viewModel.isRecruiter,
        baseURL = "http://10.0.2.2:3000"
    )
    
    val listState = rememberLazyListState()
    
    LaunchedEffect(Unit) {
        viewModel.loadConversation()
        viewModel.loadMessages()
    }
    
    // Reload messages when returning to this screen (e.g., after contract creation)
    LaunchedEffect(conversationId) {
        viewModel.loadMessages()
    }
    
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }
    
    Scaffold(
        topBar = {
            ConversationTopBar(
                conversation = conversation,
                isRecruiter = viewModel.isRecruiter,
                onBack = onBack
            )
        },
        bottomBar = {
            MessageInputBar(
                messageText = messageText,
                onMessageTextChange = { viewModel.updateMessageText(it) },
                onSendClick = { viewModel.sendMessage() },
                isSending = isSending,
                enabled = messageText.trim().isNotEmpty() && !isSending,
                isRecruiter = viewModel.isRecruiter,
                onContractClick = if (viewModel.isRecruiter) onCreateContractClick else null
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            if (isLoading && messages.isEmpty()) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else {
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(messages) { message ->
                        // Check if this is a contract message
                        // Detect contract messages by checking isContractMessage flag OR contractId presence
                        val isContractMessage = message.isContractMessage == true || 
                                               (message.contractId != null && message.contractId.isNotBlank())
                        
                        if (isContractMessage && message.contractId != null) {
                            ContractMessageBubble(
                                message = message,
                                isFromCurrentUser = viewModel.isMessageFromCurrentUser(message),
                                onContractClick = {
                                    message.contractId?.let { contractId ->
                                        onContractReview(contractId)
                                    }
                                },
                                isRecruiter = viewModel.isRecruiter,
                                currentUserAvatarUrl = currentUserAvatarUrl,
                                otherUserAvatarUrl = otherUserAvatarUrl
                            )
                        } else {
                            MessageBubble(
                                message = message,
                                isFromCurrentUser = viewModel.isMessageFromCurrentUser(message),
                                currentUserAvatarUrl = currentUserAvatarUrl,
                                otherUserAvatarUrl = otherUserAvatarUrl
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ConversationTopBar(
    conversation: com.example.matchify.domain.model.Conversation?,
    isRecruiter: Boolean,
    onBack: () -> Unit
) {
    TopAppBar(
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Profile Image
                val imageUrl = conversation?.let { conv ->
                    conv.getOtherUserProfileImageURL(
                        isRecruiter = isRecruiter,
                        baseURL = "http://10.0.2.2:3000"
                    )
                }
                
                AsyncImage(
                    model = imageUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
                    contentScale = androidx.compose.ui.layout.ContentScale.Crop
                )
                
                Text(
                    text = conversation?.getOtherUserName(isRecruiter) ?: "User",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }
        },
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBackIosNew, contentDescription = "Back")
            }
        }
    )
}

@Composable
private fun ContractMessageBubble(
    message: com.example.matchify.domain.model.Message,
    isFromCurrentUser: Boolean,
    onContractClick: () -> Unit,
    isRecruiter: Boolean = false,
    currentUserAvatarUrl: String?,
    otherUserAvatarUrl: String?
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 4.dp),
        horizontalArrangement = if (isFromCurrentUser) Arrangement.End else Arrangement.Start
    ) {
        // Avatar for received messages (left side)
        if (!isFromCurrentUser) {
            MessageAvatar(
                avatarUrl = otherUserAvatarUrl,
                modifier = Modifier
                    .size(36.dp)
                    .padding(end = 8.dp)
            )
        }
        
        Column(
            horizontalAlignment = if (isFromCurrentUser) Alignment.End else Alignment.Start,
            modifier = Modifier.widthIn(max = 280.dp)
        ) {
            // Unified bubble design - same colors as regular messages
            val bubbleColor = if (isFromCurrentUser) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.surfaceVariant
            }
            
            val textColor = if (isFromCurrentUser) {
                MaterialTheme.colorScheme.onPrimary
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            }
            
            // Check if contract is signed for icon display
            val signedKeywords = listOf("signed", "signé", "both parties", "Talent signed")
            val isSigned = signedKeywords.any { message.content.contains(it, ignoreCase = true) } ||
                         message.pdfUrl != null
            
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = bubbleColor,
                onClick = onContractClick,
                modifier = Modifier.shadow(
                    elevation = 1.dp,
                    shape = RoundedCornerShape(20.dp)
                )
            ) {
                Row(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.DriveFileMoveRtl,
                        contentDescription = "Contract",
                        modifier = Modifier.size(28.dp),
                        tint = textColor
                    )
                    
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Contrat",
                                style = MaterialTheme.typography.titleMedium,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = textColor
                            )
                            
                            if (isSigned) {
                                Icon(
                                    Icons.Default.CheckCircle,
                                    contentDescription = "Signed",
                                    modifier = Modifier.size(16.dp),
                                    tint = if (isSystemInDarkTheme()) {
                                        Color(0xFF81C784)
                                    } else {
                                        Color(0xFF4CAF50)
                                    }
                                )
                            }
                        }
                        
                        Text(
                            text = message.content,
                            style = MaterialTheme.typography.bodySmall,
                            fontSize = 12.sp,
                            color = textColor.copy(alpha = 0.8f),
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }
            }
            
            // Timestamp below message
            Text(
                text = message.formattedTime,
                style = MaterialTheme.typography.bodySmall,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }
        
        // Avatar for sent messages (right side)
        if (isFromCurrentUser) {
            MessageAvatar(
                avatarUrl = currentUserAvatarUrl,
                modifier = Modifier
                    .size(36.dp)
                    .padding(start = 8.dp)
            )
        }
    }
}

@Composable
private fun MessageBubble(
    message: com.example.matchify.domain.model.Message,
    isFromCurrentUser: Boolean,
    currentUserAvatarUrl: String?,
    otherUserAvatarUrl: String?
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 4.dp),
        horizontalArrangement = if (isFromCurrentUser) Arrangement.End else Arrangement.Start
    ) {
        // Avatar for received messages (left side)
        if (!isFromCurrentUser) {
            MessageAvatar(
                avatarUrl = otherUserAvatarUrl,
                modifier = Modifier
                    .size(36.dp)
                    .padding(end = 8.dp)
            )
        }
        
        Column(
            horizontalAlignment = if (isFromCurrentUser) Alignment.End else Alignment.Start,
            modifier = Modifier.widthIn(max = 280.dp)
        ) {
            // Modern chat bubble with large rounded corners
            val bubbleColor = if (isFromCurrentUser) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.surfaceVariant
            }
            
            val textColor = if (isFromCurrentUser) {
                MaterialTheme.colorScheme.onPrimary
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            }
            
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = bubbleColor,
                modifier = Modifier.shadow(
                    elevation = 1.dp,
                    shape = RoundedCornerShape(20.dp)
                )
            ) {
                Text(
                    text = message.content,
                    style = MaterialTheme.typography.bodyLarge,
                    color = textColor,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                )
            }
            
            // Timestamp below message
            Text(
                text = message.formattedTime,
                style = MaterialTheme.typography.bodySmall,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }
        
        // Avatar for sent messages (right side)
        if (isFromCurrentUser) {
            MessageAvatar(
                avatarUrl = currentUserAvatarUrl,
                modifier = Modifier
                    .size(36.dp)
                    .padding(start = 8.dp)
            )
        }
    }
}

@Composable
private fun MessageAvatar(
    avatarUrl: String?,
    modifier: Modifier = Modifier
) {
    if (avatarUrl != null) {
        AsyncImage(
            model = avatarUrl,
            contentDescription = "Avatar",
            modifier = modifier
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape),
            contentScale = ContentScale.Crop
        )
    } else {
        // Placeholder icon or initial
        Box(
            modifier = modifier
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.Description,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
            )
        }
    }
}

@Composable
private fun MessageInputBar(
    messageText: String,
    onMessageTextChange: (String) -> Unit,
    onSendClick: () -> Unit,
    isSending: Boolean,
    enabled: Boolean,
    isRecruiter: Boolean = false,
    onContractClick: (() -> Unit)? = null
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 8.dp
    ) {
        Column {
            // Contract button (Recruiter only) - Modern design
            if (isRecruiter && onContractClick != null) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.Start
                ) {
                    FilledTonalButton(
                        onClick = onContractClick,
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    ) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "Envoyer un contrat",
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                }
            }
            
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Modern Material 3 Filled Text Field
                OutlinedTextField(
                    value = messageText,
                    onValueChange = onMessageTextChange,
                    modifier = Modifier.weight(1f),
                    placeholder = { 
                        Text(
                            "Tapez un message...",
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                        ) 
                    },
                    shape = RoundedCornerShape(24.dp),
                    maxLines = 4,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface
                    ),
                    textStyle = MaterialTheme.typography.bodyLarge
                )
                
                // Modern send button with FAB-like design
                FloatingActionButton(
                    onClick = onSendClick,
                    modifier = Modifier.size(48.dp),
                    containerColor = if (enabled) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.surfaceVariant
                    },
                    contentColor = if (enabled) {
                        MaterialTheme.colorScheme.onPrimary
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                    },
                    shape = CircleShape
                ) {
                    if (isSending) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Icon(
                            Icons.Default.Send,
                            contentDescription = "Envoyer",
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }
    }
}

