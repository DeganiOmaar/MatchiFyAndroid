package com.example.matchify.ui.contracts

import android.graphics.Bitmap
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.matchify.domain.model.Contract
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContractReviewScreen(
    contract: Contract,
    onBack: () -> Unit,
    onSigned: () -> Unit,
    onDeclined: () -> Unit,
    viewModel: ContractReviewViewModel = viewModel(
        factory = ContractReviewViewModelFactory(contract.id)
    )
) {
    val loadedContract by viewModel.contract.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    
    var showSignaturePad by remember { mutableStateOf(false) }
    var signatureBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var hasSigned by remember { mutableStateOf(false) }
    var isSending by remember { mutableStateOf(false) }
    var showSuccessMessage by remember { mutableStateOf(false) }
    
    val currentContract = loadedContract ?: contract
    
    LaunchedEffect(Unit) {
        viewModel.loadContract()
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F172A))
    ) {
        // Custom Header
        ReviewHeader(
            onBack = onBack,
            canSign = currentContract.status != Contract.ContractStatus.SIGNED_BY_BOTH && !hasSigned,
            onSignClick = { showSignaturePad = true }
        )

        Box(modifier = Modifier.weight(1f)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Title Field
                DarkReadOnlyField(
                    label = "Contract Title",
                    value = currentContract.title
                )
                
                // Content Field
                DarkReadOnlyField(
                    label = "Contract Terms",
                    value = currentContract.content,
                    singleLine = false
                )
                
                // Payment Details
                if (!currentContract.paymentDetails.isNullOrEmpty()) {
                    DarkReadOnlyField(
                        label = "Payment Details",
                        value = currentContract.paymentDetails
                    )
                }
                
                // Dates Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    if (currentContract.startDate != null) {
                        Box(modifier = Modifier.weight(1f)) {
                            DarkReadOnlyField(
                                label = "Start Date",
                                value = formatDate(currentContract.startDate!!)
                            )
                        }
                    }
                    
                    if (currentContract.endDate != null) {
                        Box(modifier = Modifier.weight(1f)) {
                            DarkReadOnlyField(
                                label = "End Date",
                                value = formatDate(currentContract.endDate!!)
                            )
                        }
                    }
                }
                
                // Recruiter Signature
                if (currentContract.recruiterSignature.isNotEmpty()) {
                    SignatureSection(
                        title = "Recruiter Signature",
                        signatureBitmap = null, // We'll need to decode if we want to show it, but for now placeholder text or decode logic
                        isReadOnly = true,
                        placeholderText = "Signed by Recruiter"
                    )
                }
                
                // Talent Signature
                if (signatureBitmap != null) {
                    SignatureSection(
                        title = "Your Signature",
                        signatureBitmap = signatureBitmap,
                        onSignClick = { showSignaturePad = true }
                    )
                } else if (currentContract.talentSignature != null && currentContract.talentSignature!!.isNotEmpty()) {
                     SignatureSection(
                        title = "Your Signature",
                        signatureBitmap = null,
                        isReadOnly = true,
                        placeholderText = "Signed by You"
                    )
                } else {
                    // Not signed yet
                     SignatureSection(
                        title = "Your Signature",
                        signatureBitmap = null,
                        onSignClick = { showSignaturePad = true },
                        placeholderText = "Tap to sign"
                    )
                }
                
                // Error Message
                if (errorMessage != null) {
                    Text(
                        text = errorMessage ?: "",
                        color = Color(0xFFEF4444),
                        fontSize = 14.sp,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )
                }
                
                // Success Message
                if (showSuccessMessage) {
                    Text(
                        text = "Contract signed and sent successfully!",
                        color = Color(0xFF10B981),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))
            }
        }
        
        // Bottom Action Buttons
        if (!hasSigned && currentContract.status != Contract.ContractStatus.SIGNED_BY_BOTH) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF1E293B))
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Decline Button
                Button(
                    onClick = {
                        viewModel.declineContract(
                            onSuccess = {
                                onDeclined()
                                onBack()
                            },
                            onError = { }
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent
                    ),
                    border = BorderStroke(1.dp, Color(0xFFEF4444)),
                    shape = RoundedCornerShape(24.dp),
                    enabled = !isLoading
                ) {
                     if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = Color(0xFFEF4444)
                        )
                    } else {
                        Text(
                            "Decline Contract",
                            color = Color(0xFFEF4444),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        } else if (hasSigned && !showSuccessMessage) {
             Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF1E293B))
                    .padding(16.dp)
            ) {
                Button(
                    onClick = {
                        if (signatureBitmap != null) {
                            isSending = true
                            viewModel.signContract(
                                signature = signatureBitmap!!,
                                onSuccess = {
                                    isSending = false
                                    showSuccessMessage = true
                                    kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.Main).launch {
                                        kotlinx.coroutines.delay(2000)
                                        onSigned()
                                        onBack()
                                    }
                                },
                                onError = { isSending = false }
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF3B82F6),
                        disabledContainerColor = Color(0xFF3B82F6).copy(alpha = 0.5f)
                    ),
                    shape = RoundedCornerShape(24.dp),
                    enabled = !isSending
                ) {
                    if (isSending) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color.White
                        )
                    } else {
                        Text(
                            "Submit Signed Contract",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
    
    // Signature Pad
    if (showSignaturePad) {
        SignaturePadView(
            onSignatureCaptured = { bitmap ->
                signatureBitmap = bitmap
                hasSigned = true
                showSignaturePad = false
            },
            onDismiss = { showSignaturePad = false }
        )
    }
}

@Composable
private fun ReviewHeader(
    onBack: () -> Unit,
    canSign: Boolean,
    onSignClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .background(Color(0xFF1E293B))
            .padding(horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBack) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = Color(0xFF3B82F6),
                modifier = Modifier.size(24.dp)
            )
        }
        
        Spacer(modifier = Modifier.width(8.dp))
        
        Text(
            text = "Review Contract",
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight(600),
            modifier = Modifier.weight(1f)
        )
        
        if (canSign) {
            TextButton(onClick = onSignClick) {
                Text(
                    text = "Sign",
                    color = Color(0xFF3B82F6),
                    fontSize = 16.sp,
                    fontWeight = FontWeight(600)
                )
            }
        }
    }
}

@Composable
private fun DarkReadOnlyField(
    label: String,
    value: String,
    singleLine: Boolean = true
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            color = Color(0xFF9CA3AF),
            fontSize = 14.sp,
            fontWeight = FontWeight(500),
            modifier = Modifier.padding(bottom = 8.dp, start = 4.dp)
        )
        
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            color = Color(0xFF1E293B),
            border = BorderStroke(1.dp, Color(0xFF374151))
        ) {
            Text(
                text = value,
                color = Color.White,
                fontSize = 15.sp,
                fontWeight = FontWeight(400),
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
                maxLines = if (singleLine) 1 else Int.MAX_VALUE
            )
        }
    }
}

@Composable
private fun SignatureSection(
    title: String,
    signatureBitmap: Bitmap?,
    onSignClick: (() -> Unit)? = null,
    isReadOnly: Boolean = false,
    placeholderText: String = ""
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = title,
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight(600),
            modifier = Modifier.padding(bottom = 12.dp, start = 4.dp)
        )
        
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp),
            shape = RoundedCornerShape(12.dp),
            color = Color(0xFF1E293B),
            border = BorderStroke(1.dp, Color(0xFF374151))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Signature Preview Area
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    color = Color.White
                ) {
                    if (signatureBitmap != null) {
                        Image(
                            bitmap = signatureBitmap.asImageBitmap(),
                            contentDescription = "Signature",
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = placeholderText,
                                color = Color.Gray,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
                
                if (!isReadOnly && onSignClick != null) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = onSignClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(36.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF3B82F6)
                        ),
                        shape = RoundedCornerShape(18.dp),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text(
                            "Sign Here",
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight(600)
                        )
                    }
                }
            }
        }
    }
}

private fun formatDate(dateString: String): String {
    return try {
        val inputFormat = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
        val outputFormat = java.text.SimpleDateFormat("dd MMM yyyy", java.util.Locale.getDefault())
        val date = inputFormat.parse(dateString)
        if (date != null) outputFormat.format(date) else dateString
    } catch (e: Exception) {
        dateString
    }
}

