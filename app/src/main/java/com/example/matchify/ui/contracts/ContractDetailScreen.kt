package com.example.matchify.ui.contracts

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F172A))
    ) {
        // Custom Header
        DetailHeader(onBack = onBack)

        Box(modifier = Modifier.weight(1f)) {
            when {
                isLoading && contract == null -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Color(0xFF3B82F6))
                    }
                }
                errorMessage != null -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = errorMessage ?: "Une erreur est survenue",
                            color = Color(0xFFEF4444)
                        )
                    }
                }
                contract != null -> {
                    ContractDetailContent(
                        contract = contract!!,
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun DetailHeader(onBack: () -> Unit) {
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
            text = "Contract Details",
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight(600)
        )
    }
}

@Composable
private fun ContractDetailContent(
    contract: Contract,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(20.dp)) {
        
        // Status Badge
        StatusBadge(status = contract.status)
        
        // Title Field
        DarkReadOnlyField(
            label = "Contract Title",
            value = contract.title
        )
        
        // Content Field
        DarkReadOnlyField(
            label = "Contract Terms",
            value = contract.content,
            singleLine = false
        )
        
        // Payment Details
        if (!contract.paymentDetails.isNullOrEmpty()) {
            DarkReadOnlyField(
                label = "Payment Details",
                value = contract.paymentDetails!!
            )
        }
        
        // Dates Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (contract.startDate != null) {
                Box(modifier = Modifier.weight(1f)) {
                    DarkReadOnlyField(
                        label = "Start Date",
                        value = formatDate(contract.startDate!!)
                    )
                }
            }
            
            if (contract.endDate != null) {
                Box(modifier = Modifier.weight(1f)) {
                    DarkReadOnlyField(
                        label = "End Date",
                        value = formatDate(contract.endDate!!)
                    )
                }
            }
        }
        
        // Signatures Section
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Signatures",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight(600),
                modifier = Modifier.padding(bottom = 12.dp, start = 4.dp)
            )
            
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFF1E293B),
                border = BorderStroke(1.dp, Color(0xFF374151))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Recruiter Signature
                    if (contract.recruiterSignature.isNotEmpty()) {
                        SignatureDisplay(
                            label = "Recruiter:",
                            signature = contract.recruiterSignature
                        )
                    }
                    
                    // Talent Signature
                    if (contract.status == Contract.ContractStatus.SIGNED_BY_BOTH &&
                        !contract.talentSignature.isNullOrEmpty()) {
                        SignatureDisplay(
                            label = "Talent:",
                            signature = contract.talentSignature!!
                        )
                    } else if (contract.status != Contract.ContractStatus.SIGNED_BY_BOTH) {
                        // Pending signature placeholder
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                text = "Talent:",
                                color = Color(0xFF9CA3AF),
                                fontSize = 14.sp,
                                fontWeight = FontWeight(500)
                            )
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(80.dp),
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFF0F172A),
                                border = BorderStroke(1.dp, Color(0xFF374151))
                            ) {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.CenterStart
                                ) {
                                    Text(
                                        text = "Pending signature",
                                        color = Color(0xFF64748B),
                                        fontSize = 14.sp,
                                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                                        modifier = Modifier.padding(horizontal = 16.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
        
        // PDF Link
        val pdfUrl = contract.signedPdfUrl ?: contract.pdfUrl
        if (pdfUrl != null) {
            val fullUrl = if (pdfUrl.startsWith("http")) {
                pdfUrl
            } else {
                "http://10.0.2.2:3000$pdfUrl"
            }
            
            Button(
                onClick = {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(fullUrl))
                    context.startActivity(intent)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF3B82F6).copy(alpha = 0.1f)
                ),
                shape = RoundedCornerShape(24.dp)
            ) {
                Text(
                    "View PDF Document",
                    color = Color(0xFF3B82F6),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
        
        Spacer(modifier = Modifier.height(20.dp))
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
private fun StatusBadge(status: Contract.ContractStatus) {
    val (text, color) = when (status) {
        Contract.ContractStatus.SENT_TO_TALENT -> "Sent to Talent" to Color(0xFFFF9800)
        Contract.ContractStatus.DECLINED_BY_TALENT -> "Declined" to Color(0xFFEF4444)
        Contract.ContractStatus.SIGNED_BY_BOTH -> "Signed by Both Parties" to Color(0xFF10B981)
    }
    
    Surface(
        color = color.copy(alpha = 0.15f),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, color.copy(alpha = 0.3f))
    ) {
        Text(
            text = text,
            fontSize = 14.sp,
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
            color = Color(0xFF9CA3AF),
            fontSize = 14.sp,
            fontWeight = FontWeight(500)
        )
        
        val signatureBitmap = decodeBase64ToBitmap(signature)
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp),
            shape = RoundedCornerShape(8.dp),
            color = Color.White // White background for signature visibility
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
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Invalid signature",
                        color = Color.Gray,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

private fun decodeBase64ToBitmap(base64String: String): android.graphics.Bitmap? {
    return try {
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

