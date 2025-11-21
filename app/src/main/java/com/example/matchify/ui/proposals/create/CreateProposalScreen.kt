package com.example.matchify.ui.proposals.create

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.matchify.ui.components.MD3OutlinedTextField

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateProposalScreen(
    missionId: String,
    missionTitle: String,
    onBack: () -> Unit,
    onSuccess: () -> Unit,
    viewModel: CreateProposalViewModel = viewModel(
        factory = CreateProposalViewModelFactory(missionId, missionTitle)
    )
) {
    val message by viewModel.message.collectAsState()
    val proposedBudget by viewModel.proposedBudget.collectAsState()
    val estimatedDuration by viewModel.estimatedDuration.collectAsState()
    val isSubmitting by viewModel.isSubmitting.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val submissionSuccess by viewModel.submissionSuccess.collectAsState()
    
    LaunchedEffect(submissionSuccess) {
        if (submissionSuccess) {
            onSuccess()
            onBack()
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Create Proposal") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Mission Header
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Mission",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = missionTitle,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold
                    )
                }
            }
            
            // Cover Letter Section
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Cover letter",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold
                )
                
                // Cover Letter - MD3 Outlined Text Field (multi-line)
                MD3OutlinedTextField(
                    value = message,
                    onValueChange = { viewModel.updateMessage(it) },
                    label = "Cover letter",
                    placeholder = "Introduce yourself, highlight experience, explain your approach...",
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp),
                    errorText = null,
                    singleLine = false,
                    minLines = 6,
                    maxLines = 8
                )
            }
            
            // Optional Details Section
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "Optional details",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold
                )
                
                // Proposed Budget - MD3 Outlined Text Field
                MD3OutlinedTextField(
                    value = proposedBudget,
                    onValueChange = { viewModel.updateProposedBudget(it) },
                    label = "Proposed budget",
                    placeholder = "Proposed budget (€)",
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    errorText = null,
                    singleLine = true
                )
                
                // Estimated Duration - MD3 Outlined Text Field
                MD3OutlinedTextField(
                    value = estimatedDuration,
                    onValueChange = { viewModel.updateEstimatedDuration(it) },
                    label = "Estimated duration",
                    placeholder = "Estimated duration (e.g. 8 weeks)",
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                    modifier = Modifier.fillMaxWidth(),
                    errorText = null,
                    singleLine = true
                )
            }
            
            // Error Message
            if (errorMessage != null) {
                Text(
                    text = errorMessage ?: "",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            
            // Send Button
            Button(
                onClick = { viewModel.sendProposal() },
                enabled = viewModel.isFormValid && !isSubmitting,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                if (isSubmitting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text(
                        "Send proposal",
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
            }
        }
    }
}

