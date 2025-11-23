package com.example.matchify.ui.contracts

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import android.graphics.BitmapFactory
import android.util.Base64
import com.example.matchify.domain.model.Contract

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContractDetailScreen(
    contractId: String,
    onBack: () -> Unit,
    viewModel: ContractDetailViewModel = viewModel(
        factory = ContractDetailViewModelFactory(contractId)
    )
) {
    val contract by viewModel.contract.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    
    LaunchedEffect(Unit) {
        viewModel.loadContract()
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Contract Details") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        when {
            isLoading && contract == null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = androidx.compose.ui.Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            errorMessage != null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = androidx.compose.ui.Alignment.Center
                ) {
                    Text(
                        text = errorMessage ?: "Une erreur est survenue",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
            contract != null -> {
                ContractDetailContent(
                    contract = contract!!,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 20.dp, vertical = 16.dp)
                )
            }
        }
    }
}

@Composable
private fun ContractDetailContent(
    contract: Contract,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(24.dp)) {
        // Title Section
        Section(title = "Title") {
            Text(
                text = contract.title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold
            )
        }
        
        // Content Section
        Section(title = "Content") {
            Text(
                text = contract.content,
                style = MaterialTheme.typography.bodyLarge
            )
        }
        
        // Payment Details Section
        if (!contract.paymentDetails.isNullOrEmpty()) {
            Section(title = "Payment Details") {
                Text(
                    text = contract.paymentDetails!!,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
        
        // Dates Section
        if (contract.startDate != null) {
            Section(title = "Start Date") {
                Text(
                    text = formatDate(contract.startDate!!),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
        
        if (contract.endDate != null) {
            Section(title = "End Date") {
                Text(
                    text = formatDate(contract.endDate!!),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
        
        // Status Section
        Section(title = "Status") {
            StatusBadge(status = contract.status)
        }
        
        // Signatures Section
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Signatures:",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                
                // Recruiter Signature - always show if present (matching iOS)
                if (contract.recruiterSignature.isNotEmpty()) {
                    SignatureDisplay(
                        label = "Recruiter:",
                        signature = contract.recruiterSignature
                    )
                }
                
                // Talent Signature - matching iOS logic exactly
                if (contract.status == Contract.ContractStatus.SIGNED_BY_BOTH &&
                    !contract.talentSignature.isNullOrEmpty()) {
                    SignatureDisplay(
                        label = "Talent:",
                        signature = contract.talentSignature!!
                    )
                } else if (contract.status != Contract.ContractStatus.SIGNED_BY_BOTH) {
                    // Show placeholder if contract not yet signed by talent - matching iOS exactly
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "Talent:",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(80.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color.Gray.copy(alpha = 0.1f)
                            )
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = androidx.compose.ui.Alignment.CenterStart
                            ) {
                                Text(
                                    text = "Pending signature",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                                    modifier = Modifier.padding(horizontal = 16.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
        
        // PDF Link - matching iOS behavior exactly
        val pdfUrl = contract.signedPdfUrl ?: contract.pdfUrl
        if (pdfUrl != null) {
            // Build full URL - matching iOS logic
            val fullUrl = if (pdfUrl.startsWith("http")) {
                pdfUrl
            } else {
                "http://10.0.2.2:3000$pdfUrl"
            }
            
            Button(
                onClick = {
                    // Open PDF in browser/viewer - matching iOS Link behavior
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(fullUrl))
                    context.startActivity(intent)
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                )
            ) {
                Text(
                    "View PDF",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun Section(title: String, content: @Composable () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        content()
    }
}

@Composable
private fun StatusBadge(status: Contract.ContractStatus) {
    val (text, color) = when (status) {
        Contract.ContractStatus.SENT_TO_TALENT -> "Envoyé au talent" to Color(0xFFFF9800)
        Contract.ContractStatus.DECLINED_BY_TALENT -> "Refusé" to Color(0xFFF44336)
        Contract.ContractStatus.SIGNED_BY_BOTH -> "Signé par les deux parties" to Color(0xFF4CAF50)
    }
    
    Surface(
        color = color.copy(alpha = 0.15f),
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = color,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
        )
    }
}

@Composable
private fun SignatureDisplay(
    label: String,
    signature: String
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        // Display signature image from base64 - matching iOS imageFromBase64 behavior
        val signatureBitmap = decodeBase64ToBitmap(signature)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp),
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.Gray.copy(alpha = 0.1f)
            )
        ) {
            if (signatureBitmap != null) {
                Image(
                    bitmap = signatureBitmap.asImageBitmap(),
                    contentDescription = "Signature",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit
                )
            } else {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = androidx.compose.ui.Alignment.Center
                ) {
                    Text(
                        text = "Invalid signature",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

/**
 * Decode base64 string to Bitmap - matching iOS imageFromBase64 function
 * Removes data URL prefix if present (e.g., "data:image/png;base64,")
 */
private fun decodeBase64ToBitmap(base64String: String): android.graphics.Bitmap? {
    return try {
        // Remove data URL prefix if present - matching iOS regex behavior
        val base64 = base64String.replace(
            Regex("data:image/[^;]+;base64,"),
            ""
        )
        
        val imageBytes = Base64.decode(base64, Base64.DEFAULT)
        BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
    } catch (e: Exception) {
        null
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

