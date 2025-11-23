package com.example.matchify.ui.contracts

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
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
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Contract Review") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    // Only show sign button if contract is not already signed by both
                    if (currentContract.status != Contract.ContractStatus.SIGNED_BY_BOTH && !hasSigned) {
                        TextButton(
                            onClick = { showSignaturePad = true }
                        ) {
                            Text("Sign")
                        }
                    } else {
                        TextButton(onClick = onBack) {
                            Text("Close")
                        }
                    }
                }
            )
        },
        bottomBar = {
            if (!hasSigned) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shadowElevation = 8.dp
                ) {
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
                            .padding(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFF44336)
                        ),
                        enabled = !isLoading
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        } else {
                            Text("Decline", color = Color.White)
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Title
            Text(
                text = currentContract.title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            
            // Content
            Text(
                text = currentContract.content,
                style = MaterialTheme.typography.bodyLarge
            )
            
            // Payment Details
            if (!currentContract.paymentDetails.isNullOrEmpty()) {
                Text(
                    text = "Payment details: ${currentContract.paymentDetails}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            
            // Dates
            if (currentContract.startDate != null) {
                Text(
                    text = "Start date: ${formatDate(currentContract.startDate!!)}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            
            if (currentContract.endDate != null) {
                Text(
                    text = "End date: ${formatDate(currentContract.endDate!!)}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            
            // Recruiter Signature
            if (currentContract.recruiterSignature.isNotEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Signature du recruteur:",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        // Display recruiter signature image from base64
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("Recruiter Signature Image")
                            }
                        }
                    }
                }
            } else {
                // Warning if recruiter signature is missing
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFFF9800).copy(alpha = 0.1f)
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            tint = Color(0xFFFF9800)
                        )
                        Text(
                            text = "La signature du recruteur est manquante. Le contrat ne peut pas être signé.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFFFF9800)
                        )
                    }
                }
            }
            
            // Talent Signature
            if (signatureBitmap != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Your signature:",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Image(
                            bitmap = signatureBitmap!!.asImageBitmap(),
                            contentDescription = "Your signature",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp)
                        )
                    }
                }
            } else if (currentContract.talentSignature != null && 
                       currentContract.talentSignature!!.isNotEmpty()) {
                // Show existing talent signature if contract was already signed
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Your signature:",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("Talent Signature Image")
                            }
                        }
                    }
                }
            }
            
            // Success Message
            if (showSuccessMessage) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF4CAF50).copy(alpha = 0.1f)
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = Color(0xFF4CAF50)
                        )
                        Text(
                            text = "Contract signed and sent to recruiter successfully!",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF4CAF50)
                        )
                    }
                }
            }
            
            // Error Message
            if (errorMessage != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFF44336).copy(alpha = 0.1f)
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Error,
                            contentDescription = null,
                            tint = Color(0xFFF44336)
                        )
                        Text(
                            text = errorMessage ?: "",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFFF44336)
                        )
                    }
                }
            }
            
            // Send Button (only if signed but not sent yet)
            if (hasSigned && !showSuccessMessage) {
                val canSend = currentContract.title.isNotEmpty() &&
                             currentContract.content.isNotEmpty() &&
                             currentContract.recruiterSignature.isNotEmpty() &&
                             signatureBitmap != null
                
                Button(
                    onClick = {
                        if (signatureBitmap != null) {
                            isSending = true
                            viewModel.signContract(
                                signature = signatureBitmap!!,
                                onSuccess = {
                                    isSending = false
                                    showSuccessMessage = true
                                    // Auto-dismiss after 2 seconds
                                    kotlinx.coroutines.CoroutineScope(
                                        kotlinx.coroutines.Dispatchers.Main
                                    ).launch {
                                        kotlinx.coroutines.delay(2000)
                                        onSigned()
                                        onBack()
                                    }
                                },
                                onError = { error ->
                                    isSending = false
                                }
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isSending && canSend
                ) {
                    if (isSending) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Send, contentDescription = null)
                            Text("Envoyer au recruteur")
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(20.dp))
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

