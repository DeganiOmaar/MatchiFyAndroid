package com.example.matchify.ui.conversations

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.InsertDriveFile
import androidx.compose.material.icons.filled.Send
import androidx.compose.foundation.clickable
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage

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
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (isLoading && messages.isEmpty()) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else {
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
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
                                isRecruiter = viewModel.isRecruiter
                            )
                        } else {
                            MessageBubble(
                                message = message,
                                isFromCurrentUser = viewModel.isMessageFromCurrentUser(message)
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
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            }
        }
    )
}

@Composable
private fun ContractMessageBubble(
    message: com.example.matchify.domain.model.Message,
    isFromCurrentUser: Boolean,
    onContractClick: () -> Unit,
    isRecruiter: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isFromCurrentUser) Arrangement.End else Arrangement.Start
    ) {
        Column(
            horizontalAlignment = if (isFromCurrentUser) Alignment.End else Alignment.Start,
            modifier = Modifier.widthIn(max = 300.dp)
        ) {
            // Contract message card - matching iOS design exactly
            Surface(
                shape = RoundedCornerShape(18.dp),
                color = if (isRecruiter && !isFromCurrentUser) {
                    // Recruiter sees contract from talent: secondaryBackground (light gray)
                    MaterialTheme.colorScheme.surfaceVariant
                } else {
                    // Talent sees contract from recruiter OR recruiter's own: primary (blue)
                    MaterialTheme.colorScheme.primary
                },
                onClick = onContractClick,
                modifier = Modifier
                    .fillMaxWidth()
                    // Add border for recruiter view (matching iOS)
                    .then(
                        if (isRecruiter && !isFromCurrentUser) {
                            Modifier.border(
                                width = 0.5.dp,
                                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                                shape = RoundedCornerShape(18.dp)
                            )
                        } else {
                            Modifier
                        }
                    )
            ) {
                Row(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Document icon - matching iOS "doc.text.fill"
                    Icon(
                        Icons.Default.InsertDriveFile,
                        contentDescription = "Contract",
                        modifier = Modifier.size(24.dp),
                        tint = if (isRecruiter && !isFromCurrentUser) {
                            // Recruiter view: primary color
                            MaterialTheme.colorScheme.primary
                        } else {
                            // Talent view or recruiter's own: white
                            Color.White
                        }
                    )
                    
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Contract",
                                style = MaterialTheme.typography.titleMedium,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (isRecruiter && !isFromCurrentUser) {
                                    // Recruiter view: textPrimary
                                    MaterialTheme.colorScheme.onSurface
                                } else {
                                    // Talent view or recruiter's own: white
                                    Color.White
                                }
                            )
                            
                            // Show signed indicator if contract is signed (matching iOS)
                            val signedKeywords = listOf("signed", "signé", "both parties", "Talent signed")
                            val isSigned = signedKeywords.any { message.content.contains(it, ignoreCase = true) } ||
                                         message.pdfUrl != null
                            if (isSigned) {
                                Icon(
                                    Icons.Default.CheckCircle,
                                    contentDescription = "Signed",
                                    modifier = Modifier.size(14.dp),
                                    tint = Color(0xFF4CAF50)
                                )
                            }
                        }
                        
                        // Contract message text - matching iOS size (12sp)
                        Text(
                            text = message.content,
                            style = MaterialTheme.typography.bodySmall,
                            fontSize = 12.sp,
                            color = if (isRecruiter && !isFromCurrentUser) {
                                // Recruiter view: textSecondary
                                MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                            } else {
                                // Talent view or recruiter's own: white with opacity
                                Color.White.copy(alpha = 0.8f)
                            },
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    
                    Spacer(modifier = Modifier.width(0.dp))
                }
            }
            
            // Timestamp below message
            Text(
                text = message.formattedTime,
                style = MaterialTheme.typography.bodySmall,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                modifier = Modifier.padding(horizontal = 4.dp, vertical = 4.dp)
            )
        }
    }
}

@Composable
private fun MessageBubble(
    message: com.example.matchify.domain.model.Message,
    isFromCurrentUser: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isFromCurrentUser) Arrangement.End else Arrangement.Start
    ) {
        Column(
            horizontalAlignment = if (isFromCurrentUser) Alignment.End else Alignment.Start,
            modifier = Modifier.widthIn(max = 280.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(
                    topStart = 18.dp,
                    topEnd = 18.dp,
                    bottomStart = if (isFromCurrentUser) 18.dp else 4.dp,
                    bottomEnd = if (isFromCurrentUser) 4.dp else 18.dp
                ),
                color = if (isFromCurrentUser) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.surfaceVariant
                }
            ) {
                Text(
                    text = message.content,
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (isFromCurrentUser) {
                        MaterialTheme.colorScheme.onPrimary
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    },
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
                )
            }
            
            Text(
                text = message.formattedTime,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 4.dp, vertical = 4.dp)
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
        shadowElevation = 8.dp
    ) {
        Column {
            // Contract button (Recruiter only) - same as iOS
            if (isRecruiter && onContractClick != null) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.Start
                ) {
                    Button(
                        onClick = onContractClick,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF007AFF) // Blue color like iOS
                        ),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp),
                            tint = Color.White
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "Envoyer un contrat",
                            style = MaterialTheme.typography.labelMedium,
                            color = Color.White
                        )
                    }
                }
            }
            
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = messageText,
                    onValueChange = onMessageTextChange,
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Type a message...") },
                    shape = RoundedCornerShape(20.dp),
                    maxLines = 4
                )
                
                IconButton(
                    onClick = onSendClick,
                    enabled = enabled
                ) {
                    if (isSending) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(
                            Icons.Default.Send,
                            contentDescription = "Send",
                            tint = if (enabled) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                            }
                        )
                    }
                }
            }
        }
    }
}

