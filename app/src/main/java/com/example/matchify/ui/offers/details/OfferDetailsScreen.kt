package com.example.matchify.ui.offers.details

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.StarOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.example.matchify.data.remote.ApiService
import com.example.matchify.data.remote.ConversationRepository
import com.example.matchify.data.remote.OfferRepository
import com.example.matchify.domain.model.Offer
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OfferDetailsScreen(
    offer: Offer,
    onBack: () -> Unit,
    onNavigateToChat: (String) -> Unit
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val prefs = remember { com.example.matchify.data.local.AuthPreferencesProvider.getInstance().get() }
    val user by prefs.user.collectAsState(initial = null)
    
    // Repositories
    val apiService = remember { ApiService.getInstance() }
    val offerRepository = remember { OfferRepository(apiService.offerApi, prefs) }
    val conversationRepository = remember { ConversationRepository(apiService, prefs) }
    
    // State
    var currentOffer by remember { mutableStateOf(offer) }
    var showReviewDialog by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }
    
    val isRecruiter = user?.role == "recruiter"
    
    // Helper function to handle creating/getting conversation
    fun handleContactTalent() {
        scope.launch {
            try {
                isLoading = true
                val conversation = conversationRepository.createConversation(talentId = currentOffer.talentId)
                isLoading = false
                onNavigateToChat(conversation.conversationId)
            } catch (e: Exception) {
                isLoading = false
                errorMessage = e.localizedMessage ?: "Failed to start conversation"
            }
        }
    }
    
    // Helper function to submit review
    fun handleSubmitReview(rating: Int, message: String) {
        scope.launch {
            try {
                isLoading = true
                val updatedOffer = offerRepository.addReview(currentOffer.id, rating, message)
                currentOffer = updatedOffer
                showReviewDialog = false
                successMessage = "Review submitted successfully"
                isLoading = false
            } catch (e: Exception) {
                isLoading = false
                errorMessage = e.localizedMessage ?: "Failed to submit review"
            }
        }
    }
    
    // New Drawer Content Wrapper
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
        }
    ) {
        Scaffold(
            topBar = {
                com.example.matchify.ui.components.MatchifyTopAppBar(
                    title = "Offer Details",
                    onBack = onBack
                )
            },
            containerColor = Color(0xFF0F172A),
            snackbarHost = {
                if (errorMessage != null || successMessage != null) {
                    SnackbarHost(hostState = remember { SnackbarHostState() }) {
                        Snackbar(
                            containerColor = if (errorMessage != null) MaterialTheme.colorScheme.error else Color(0xFF10B981),
                            contentColor = Color.White
                        ) {
                            Text(errorMessage ?: successMessage ?: "")
                        }
                    }
                }
            }
        ) { paddingValues ->
            Box(modifier = Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFF0F172A))
                        .padding(paddingValues)
                        .verticalScroll(rememberScrollState())
                ) {
                    // Banner Image
                    AsyncImage(
                        model = "http://10.0.2.2:3000/${currentOffer.bannerImage}",
                        contentDescription = "Banner",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(250.dp),
                        contentScale = ContentScale.Crop
                    )
                    
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        // Title and Price
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                text = currentOffer.title,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = currentOffer.formattedPrice,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF3B82F6)
                            )
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color(0xFF1E293B)
                            ) {
                                Text(
                                    text = currentOffer.category,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF9CA3AF),
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                )
                            }
                        }
                        
                        // Action Buttons for Recruiter
                        if (isRecruiter) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Button(
                                    onClick = { handleContactTalent() },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3B82F6)),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Icon(
                                        imageVector = androidx.compose.material.icons.Icons.Default.Star, // Placeholder icon if needed, or use specific message icon
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Contact Talent")
                                }
                                
                                Button(
                                    onClick = { showReviewDialog = true },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF97316)), // Orange
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.Star,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Rate Offer")
                                }
                            }
                        }
                        
                        HorizontalDivider(color = Color(0xFF1E293B), thickness = 1.dp)
                        
                        // Description
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                text = "Description",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = currentOffer.description,
                                fontSize = 14.sp,
                                color = Color(0xFF9CA3AF),
                                lineHeight = 20.sp
                            )
                        }
                        
                        // Reviews Section
                        if (!currentOffer.reviews.isNullOrEmpty()) {
                            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                Text(
                                    text = "Reviews (${currentOffer.reviews!!.size})",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                
                                currentOffer.reviews!!.forEach { review ->
                                    Surface(
                                        color = Color(0xFF1E293B),
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(12.dp),
                                            verticalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(
                                                    text = review.recruiterName,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color.White,
                                                    fontSize = 16.sp
                                                )
                                                
                                                Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                                                    repeat(5) { index ->
                                                        Icon(
                                                            imageVector = if (index < review.rating) Icons.Rounded.Star else Icons.Rounded.StarOutline,
                                                            contentDescription = null,
                                                            tint = if (index < review.rating) Color.Yellow else Color.Gray,
                                                            modifier = Modifier.size(14.dp)
                                                        )
                                                    }
                                                }
                                            }
                                            
                                            Text(
                                                text = review.message,
                                                color = Color(0xFF9CA3AF),
                                                fontSize = 14.sp
                                            )
                                        }
                                    }
                                }
                            }
                        } else {
                            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                Text(
                                    text = "Reviews",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = "No reviews yet.",
                                    color = Color(0xFF9CA3AF),
                                    fontSize = 14.sp
                                )
                            }
                        }

                        // ... Other sections (Keywords, Capabilities, Gallery)
                        
                        // Keywords
                        if (currentOffer.keywords.isNotEmpty()) {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text(
                                    text = "Keywords",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    items(currentOffer.keywords) { keyword ->
                                        Surface(
                                            shape = RoundedCornerShape(12.dp),
                                            color = Color(0xFF1E293B)
                                        ) {
                                            Text(
                                                text = keyword,
                                                fontSize = 13.sp,
                                                color = Color.White,
                                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                        
                        // Capabilities
                        if (currentOffer.capabilities?.isNotEmpty() == true) {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text(
                                    text = "Capabilities",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    items(currentOffer.capabilities!!) { capability ->
                                        Surface(
                                            shape = RoundedCornerShape(12.dp),
                                            color = Color(0xFF3B82F6).copy(alpha = 0.2f)
                                        ) {
                                            Text(
                                                text = capability,
                                                fontSize = 13.sp,
                                                color = Color(0xFF3B82F6),
                                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                        
                        // Gallery Images
                        if (currentOffer.galleryImages?.isNotEmpty() == true) {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text(
                                    text = "Gallery",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                    items(currentOffer.galleryImages!!) { imageUrl ->
                                        AsyncImage(
                                            model = "http://10.0.2.2:3000/$imageUrl",
                                            contentDescription = "Gallery",
                                            modifier = Modifier
                                                .size(150.dp)
                                                .clip(RoundedCornerShape(12.dp)),
                                            contentScale = ContentScale.Crop
                                        )
                                    }
                                }
                            }
                        }
                        
                        // Posted Date
                        Text(
                            text = "Posted on ${currentOffer.formattedDate}",
                            fontSize = 12.sp,
                            color = Color(0xFF6B7280)
                        )
                    }
                }
                
                // Loading Overlay
                if (isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.5f)),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Color.White)
                    }
                }
            }
        }
    }
    
    // Review Dialog
    if (showReviewDialog) {
        AddReviewDialog(
            onDismiss = { showReviewDialog = false },
            onSubmit = { rating, message ->
                handleSubmitReview(rating, message)
            }
        )
    }
    
    // Clear messages after delay
    LaunchedEffect(errorMessage, successMessage) {
        if (errorMessage != null || successMessage != null) {
            kotlinx.coroutines.delay(3000)
            errorMessage = null
            successMessage = null
        }
    }
}

@Composable
fun AddReviewDialog(
    onDismiss: () -> Unit,
    onSubmit: (Int, String) -> Unit
) {
    var rating by remember { mutableStateOf(5) }
    var message by remember { mutableStateOf("") }
    
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color(0xFF1E293B),
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Rate Offer",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                
                // Stars
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    repeat(5) { index ->
                        val starIndex = index + 1
                        IconButton(onClick = { rating = starIndex }) {
                            Icon(
                                imageVector = if (starIndex <= rating) Icons.Rounded.Star else Icons.Rounded.StarOutline,
                                contentDescription = "$starIndex Stars",
                                tint = if (starIndex <= rating) Color.Yellow else Color.Gray,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }
                }
                
                // Message Field
                OutlinedTextField(
                    value = message,
                    onValueChange = { message = it },
                    placeholder = { Text("Write your review...", color = Color.Gray) },
                    modifier = Modifier.fillMaxWidth().height(120.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color(0xFF3B82F6),
                        unfocusedBorderColor = Color(0xFF334155),
                        cursorColor = Color(0xFF3B82F6),
                        focusedContainerColor = Color(0xFF0F172A),
                        unfocusedContainerColor = Color(0xFF0F172A)
                    )
                )
                
                // Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel", color = Color(0xFF9CA3AF))
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = { onSubmit(rating, message) },
                        enabled = message.isNotEmpty(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3B82F6))
                    ) {
                        Text("Submit")
                    }
                }
            }
        }
    }
}
