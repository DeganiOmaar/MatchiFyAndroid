package com.example.matchify.ui.conversations

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.zIndex
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.matchify.R
import com.example.matchify.data.local.AuthPreferencesProvider

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
    val shouldShowApproveButton by viewModel.shouldShowApproveButton.collectAsState()
    val mission by viewModel.mission.collectAsState()
    
    val prefs = remember { AuthPreferencesProvider.getInstance().get() }
    val currentUser by prefs.user.collectAsState(initial = null)
    
    // Payment Sheet State
    var showPaymentSheet by remember { androidx.compose.runtime.mutableStateOf(false) }
    
    // Deliverable Sheet State
    var showDeliverableSheet by remember { androidx.compose.runtime.mutableStateOf(false) }

    // Pending Approval State
    var pendingApprovalDeliverableId by remember { androidx.compose.runtime.mutableStateOf<String?>(null) }

    // Payment ViewModel (using first non-null mission ID available)
    val currentMissionIdForVm = mission?.missionId
    val paymentViewModel: com.example.matchify.ui.payment.MissionPaymentViewModel? = if (currentMissionIdForVm != null) {
        androidx.lifecycle.viewmodel.compose.viewModel(
            key = "payment_$currentMissionIdForVm",
            factory = com.example.matchify.ui.payment.MissionPaymentViewModelFactory(currentMissionIdForVm)
        )
    } else null

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
        viewModel.markAsRead()
    }
    
    LaunchedEffect(conversationId) {
        viewModel.loadMessages()
    }
    
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }
    
    // Full screen background color: #0F172A (dark navy)
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F172A))
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Header
            ChatHeader(
                conversation = conversation,
                isRecruiter = viewModel.isRecruiter,
                onBack = onBack
            )

            // Payment Banner (Recruiter Only)
            if (shouldShowApproveButton) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF2E7D32)) // Green for payment action
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Mission Action",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "Pay & Complete Mission",
                            color = Color.White.copy(alpha = 0.9f),
                            fontSize = 12.sp
                        )
                    }
                    
                    Button(
                        onClick = { 
                            // Launch payment screen
                            if (mission != null) {
                                showPaymentSheet = true
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White,
                            contentColor = Color(0xFF2E7D32)
                        ),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        modifier = Modifier.height(36.dp)
                    ) {
                        Text("Approve & Pay", fontSize = 13.sp)
                    }
                }
            }
            
            // Messages List
            Box(modifier = Modifier.weight(1f)) {
                if (isLoading && messages.isEmpty()) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = Color.White
                    )
                } else {
                    val context = androidx.compose.ui.platform.LocalContext.current
                    LazyColumn(
                        state = listState,
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(
                            start = 16.dp,
                            top = 16.dp,
                            end = 16.dp,
                            bottom = 90.dp
                        ),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(messages) { message ->
                            val isContractMessage = message.isContractMessage == true ||
                                                   (message.contractId != null && message.contractId.isNotBlank())
                            val isDeliverableMessage = message.isDeliverableMessage
                            
                            when {
                                isContractMessage && message.contractId != null -> {
                                    ContractMessageBubble(
                                        message = message,
                                        isFromCurrentUser = viewModel.isMessageFromCurrentUser(message),
                                        onContractClick = {
                                            message.contractId?.let { contractId ->
                                                onContractReview(contractId)
                                            }
                                        },
                                        currentUserAvatarUrl = currentUserAvatarUrl,
                                        otherUserAvatarUrl = otherUserAvatarUrl
                                    )
                                }
                                isDeliverableMessage -> {
                                    DeliverableMessageBubble(
                                        message = message,
                                        isFromCurrentUser = viewModel.isMessageFromCurrentUser(message),
                                        isRecruiter = viewModel.isRecruiter,
                                        onApprove = { deliverableId ->
                                            // Store the deliverable ID and show payment sheet
                                            // Approval will happen on successful payment
                                            pendingApprovalDeliverableId = deliverableId
                                            if (mission != null) {
                                                android.util.Log.d("ConversationChatScreen", "Showing payment sheet for mission: ${mission?.missionId}")
                                               // android.widget.Toast.makeText(context, "Opening Payment...", android.widget.Toast.LENGTH_SHORT).show()
                                                showPaymentSheet = true
                                            } else {
                                                 android.util.Log.e("ConversationChatScreen", "Mission is null, cannot show payment sheet. Triggering refresh.")
                                                 android.widget.Toast.makeText(context, "Mission data missing. Refreshing...", android.widget.Toast.LENGTH_SHORT).show()
                                                 viewModel.refreshMission()
                                            }
                                        },
                                        onRequestChanges = { deliverableId, reason ->
                                            viewModel.requestChanges(deliverableId, reason)
                                        },
                                        currentUserAvatarUrl = currentUserAvatarUrl,
                                        otherUserAvatarUrl = otherUserAvatarUrl
                                    )
                                }
                                else -> {
                                    TextMessageBubble(
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
            
            // Input Bar
            MessageInputBar(
                messageText = messageText,
                onMessageTextChange = { viewModel.updateMessageText(it) },
                onSendClick = { viewModel.sendMessage() },
                isSending = isSending,
                enabled = messageText.trim().isNotEmpty() && !isSending,
                isRecruiter = viewModel.isRecruiter,
                onContractClick = {
                    if (viewModel.isRecruiter) {
                        onCreateContractClick()
                    } else {
                        // Talent clicked (+) - show deliverable sheet
                        showDeliverableSheet = true
                    }
                }
            )
            
            // Deliverable Input Sheet
            if (showDeliverableSheet) {
                val context = androidx.compose.ui.platform.LocalContext.current
                DeliverableInputSheet(
                    onDismiss = { showDeliverableSheet = false },
                    onFileSelected = { uri, fileName, mimeType ->
                        viewModel.uploadDeliverable(uri, context)
                        showDeliverableSheet = false
                    },
                    onLinkSubmit = { url, title ->
                        viewModel.submitLink(url, title)
                        showDeliverableSheet = false
                    }
                )
            }
            
            // WebView Payment Screen
            if (showPaymentSheet && paymentViewModel != null) {
                val checkoutUrl by paymentViewModel.checkoutUrl.collectAsState()
                val paymentError by paymentViewModel.errorMessage.collectAsState()
                val context = androidx.compose.ui.platform.LocalContext.current
                
                // Initiate checkout session when shown
                LaunchedEffect(showPaymentSheet) {
                    if (showPaymentSheet) {
                        android.util.Log.d("ConversationChatScreen", "Initiating WebView payment from top-level VM...")
                        paymentViewModel.initiateWebViewPayment()
                    }
                }
                
                // Show error if payment initialization failed
                paymentError?.let { error ->
                    LaunchedEffect(error) {
                        android.util.Log.e("ConversationChatScreen", "Payment error: $error")
                        android.widget.Toast.makeText(context, error, android.widget.Toast.LENGTH_LONG).show()
                        showPaymentSheet = false
                    }
                }
                
                // Show WebView when checkout URL is available
                checkoutUrl?.let { url ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black)
                            .zIndex(10f)
                    ) {
                        com.example.matchify.ui.payment.PaymentWebViewScreen(
                            checkoutUrl = url,
                            onPaymentSuccess = {
                                android.util.Log.d("ConversationChatScreen", "Payment success callback")
                                showPaymentSheet = false
                                android.widget.Toast.makeText(
                                    context,
                                    "Payment successful!",
                                    android.widget.Toast.LENGTH_SHORT
                                ).show()
                                // Reload mission and messages
                                viewModel.refreshMission()
                                viewModel.loadMessages()
                            },
                            onPaymentCancel = {
                                android.util.Log.d("ConversationChatScreen", "Payment cancel callback")
                                showPaymentSheet = false
                            },
                            onError = { error ->
                                android.util.Log.e("ConversationChatScreen", "WebView error: $error")
                                android.widget.Toast.makeText(context, error, android.widget.Toast.LENGTH_LONG).show()
                            },
                            onBack = {
                                android.util.Log.d("ConversationChatScreen", "Payment back callback")
                                showPaymentSheet = false
                            }
                        )
                    }
                }
            }
        }
    }
}

// Header Component
@Composable
private fun ChatHeader(
    conversation: com.example.matchify.domain.model.Conversation?,
    isRecruiter: Boolean,
    onBack: () -> Unit
) {
    // Container: Height 64dp, Background #1E293B (darker to separate from screen)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .background(Color(0xFF1E293B))
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Left Icon - Back arrow: Size 22dp, Color #3B82F6 (blue)
        IconButton(
            onClick = onBack,
            modifier = Modifier.size(48.dp)
        ) {
            Icon(
                Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                modifier = Modifier.size(22.dp),
                tint = Color(0xFF3B82F6)
            )
        }
        
        // Center Content - Avatar and Username (horizontal layout)
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            val imageUrl = conversation?.let { conv ->
                conv.getOtherUserProfileImageURL(
                    isRecruiter = isRecruiter,
                    baseURL = "http://10.0.2.2:3000"
                )
            }
            
            // Avatar - diameter 42dp
            AsyncImage(
                model = imageUrl,
                contentDescription = null,
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop,
                error = painterResource(id = R.drawable.avatar),
                placeholder = painterResource(id = R.drawable.avatar)
            )
            
            Spacer(modifier = Modifier.width(8.dp))
            
            // Username - Font size 16sp, Weight 600, Color #FFFFFF
            Text(
                text = conversation?.getOtherUserName(isRecruiter) ?: "User",
                fontSize = 16.sp,
                fontWeight = FontWeight(600),
                color = Color.White
            )
        }
        
        // Right Side Icon - Phone
        IconButton(
            onClick = { /* TODO: Implement phone call */ },
            modifier = Modifier.size(48.dp)
        ) {
            Icon(
                Icons.Default.Phone,
                contentDescription = "Phone",
                modifier = Modifier.size(22.dp),
                tint = Color(0xFF3B82F6)
            )
        }
    }
}

// Text Message Bubble Component
@Composable
private fun TextMessageBubble(
    message: com.example.matchify.domain.model.Message,
    isFromCurrentUser: Boolean,
    currentUserAvatarUrl: String?,
    otherUserAvatarUrl: String?
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isFromCurrentUser) Arrangement.End else Arrangement.Start,
        verticalAlignment = Alignment.Bottom
    ) {
        // Avatar for received messages (left side) - diameter 36dp
        if (!isFromCurrentUser) {
            AsyncImage(
                model = otherUserAvatarUrl ?: "",
                contentDescription = null,
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop,
                error = painterResource(id = R.drawable.avatar),
                placeholder = painterResource(id = R.drawable.avatar)
            )
            Spacer(modifier = Modifier.width(8.dp))
        }
        
        val screenWidth = LocalConfiguration.current.screenWidthDp.dp
        Column(
            horizontalAlignment = if (isFromCurrentUser) Alignment.End else Alignment.Start,
            modifier = Modifier.widthIn(max = screenWidth * 0.7f)
        ) {
            if (isFromCurrentUser) {
                // Sent Text Message Bubble
                // Background: #3B82F6
                // Border radius: Top-left 16dp, Top-right 16dp, Bottom-left 16dp, Bottom-right 0dp (sharp)
                Surface(
                    shape = RoundedCornerShape(
                        topStart = 16.dp,
                        topEnd = 16.dp,
                        bottomStart = 16.dp,
                        bottomEnd = 0.dp
                    ),
                    color = Color(0xFF3B82F6),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp)
                    ) {
                        // Text: Font size 15sp, Weight 400, Color #FFFFFF
                        Text(
                            text = message.content,
                            fontSize = 15.sp,
                            fontWeight = FontWeight(400),
                            color = Color.White
                        )
                    }
                }
            } else {
                // Received Text Message Bubble
                // Background: #374151
                // Border radius: Top-left 16dp, Top-right 16dp, Bottom-left 0dp (sharp), Bottom-right 16dp
                Surface(
                    shape = RoundedCornerShape(
                        topStart = 16.dp,
                        topEnd = 16.dp,
                        bottomStart = 0.dp,
                        bottomEnd = 16.dp
                    ),
                    color = Color(0xFF374151),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp)
                    ) {
                        // Text: Font size 15sp, Weight 400, Color #FFFFFF
                        Text(
                            text = message.content,
                            fontSize = 15.sp,
                            fontWeight = FontWeight(400),
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

// Contract Message Bubble Component
@Composable
private fun ContractMessageBubble(
    message: com.example.matchify.domain.model.Message,
    isFromCurrentUser: Boolean,
    onContractClick: () -> Unit,
    currentUserAvatarUrl: String?,
    otherUserAvatarUrl: String?
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isFromCurrentUser) Arrangement.End else Arrangement.Start,
        verticalAlignment = Alignment.Bottom
    ) {
        // Avatar for received messages (left side) - diameter 36dp
        if (!isFromCurrentUser) {
            AsyncImage(
                model = otherUserAvatarUrl ?: "",
                contentDescription = null,
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop,
                error = painterResource(id = R.drawable.avatar),
                placeholder = painterResource(id = R.drawable.avatar)
            )
            Spacer(modifier = Modifier.width(8.dp))
        }
        
        val screenWidth = LocalConfiguration.current.screenWidthDp.dp
        Column(
            horizontalAlignment = if (isFromCurrentUser) Alignment.End else Alignment.Start,
            modifier = Modifier.widthIn(max = screenWidth * 0.7f)
        ) {
            val bubbleColor = if (isFromCurrentUser) {
                Color(0xFF3B82F6)
            } else {
                Color(0xFF374151)
            }
            
            val bubbleShape = if (isFromCurrentUser) {
                RoundedCornerShape(
                    topStart = 16.dp,
                    topEnd = 16.dp,
                    bottomStart = 16.dp,
                    bottomEnd = 0.dp
                )
            } else {
                RoundedCornerShape(
                    topStart = 16.dp,
                    topEnd = 16.dp,
                    bottomStart = 0.dp,
                    bottomEnd = 16.dp
                )
            }
            
            // Contract Bubble
            Surface(
                shape = bubbleShape,
                color = bubbleColor,
                onClick = onContractClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp)
                ) {
                    // Contract icon and title row
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Contract icon: Size 20dp, Color #FFFFFF
                        Icon(
                            Icons.Default.Description,
                            contentDescription = "Contract",
                            modifier = Modifier.size(20.dp),
                            tint = Color.White
                        )
                        
                        // Contract title: Font size 15sp, Weight 600, Color #FFFFFF
                        Text(
                            text = message.content,
                            fontSize = 15.sp,
                            fontWeight = FontWeight(600),
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

// Input Bar Component
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
    // Container with darker background to separate from screen
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF1E293B))
            .imePadding()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Left icon (+ button): Visible for recruiters (contracts) and talents (submit work)
        val showAttachmentButton = isRecruiter || (onContractClick != null) // Reusing callback for generic attachment click
        
        if (showAttachmentButton) {
            IconButton(
                onClick = {
                    if (onContractClick != null) {
                        onContractClick()
                    }
                },
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "Attach",
                    modifier = Modifier.size(24.dp),
                    tint = Color(0xFF3B82F6)
                )
            }
        }
        
        // TextField with transparent background and border
        Surface(
            modifier = Modifier.weight(1f),
            color = Color.Transparent,
            shape = RoundedCornerShape(24.dp),
            border = BorderStroke(1.dp, Color(0xFF374151))
        ) {
            TextField(
                value = messageText,
                onValueChange = onMessageTextChange,
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text(
                        text = "Type a message…",
                        fontSize = 15.sp,
                        color = Color(0xFF9CA3AF)
                    )
                },
                textStyle = androidx.compose.ui.text.TextStyle(
                    fontSize = 15.sp,
                    fontWeight = FontWeight(400),
                    color = Color.White
                ),
                colors = TextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedPlaceholderColor = Color(0xFF9CA3AF),
                    unfocusedPlaceholderColor = Color(0xFF9CA3AF),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent
                ),
                maxLines = 4,
                singleLine = false
            )
        }
        
        // Send button (Arrow): Separate icon
        IconButton(
            onClick = onSendClick,
            enabled = enabled,
            modifier = Modifier.size(48.dp)
        ) {
            if (isSending) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    strokeWidth = 2.dp,
                    color = Color(0xFF3B82F6)
                )
            } else {
                Icon(
                    Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Send",
                    modifier = Modifier.size(24.dp),
                    tint = if (enabled) Color(0xFF3B82F6) else Color(0xFF9CA3AF)
                )
            }
        }
    }
}
