package com.example.matchify.ui.contracts

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
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
                    text = "Signatures",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                
                // Recruiter Signature
                SignatureDisplay(
                    label = "Recruiter:",
                    signature = contract.recruiterSignature
                )
                
                // Talent Signature
                if (contract.status == Contract.ContractStatus.SIGNED_BY_BOTH &&
                    !contract.talentSignature.isNullOrEmpty()) {
                    SignatureDisplay(
                        label = "Talent:",
                        signature = contract.talentSignature!!
                    )
                } else if (contract.status != Contract.ContractStatus.SIGNED_BY_BOTH) {
                    Text(
                        text = "Talent: Pending signature",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
        
        // PDF Link
        val pdfUrl = contract.signedPdfUrl ?: contract.pdfUrl
        if (pdfUrl != null) {
            Button(
                onClick = { /* Open PDF */ },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("View PDF")
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
        // Display signature image from base64
        // Note: You'll need to decode base64 and display as image
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp),
            shape = RoundedCornerShape(8.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = androidx.compose.ui.Alignment.Center
            ) {
                Text(
                    text = "Signature Image",
                    style = MaterialTheme.typography.bodySmall
                )
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

