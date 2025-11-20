package com.example.matchify.ui.proposals.details

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProposalDetailsScreen(
    proposalId: String,
    onBack: () -> Unit,
    onTalentProfileClick: (String) -> Unit = {},
    onConversationClick: (String) -> Unit = {},
    viewModel: ProposalDetailsViewModel = viewModel(
        factory = ProposalDetailsViewModelFactory(proposalId)
    )
) {
    val proposal by viewModel.proposal.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val isUpdatingStatus by viewModel.isUpdatingStatus.collectAsState()
    val canShowActions by viewModel.canShowActions.collectAsState()
    val showMessageButton by viewModel.showMessageButton.collectAsState()
    
    LaunchedEffect(Unit) {
        viewModel.loadProposal()
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Proposal Details") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        bottomBar = {
            if (viewModel.isRecruiter) {
                when {
                    canShowActions -> {
                        AcceptRefuseButtons(
                            onAccept = { viewModel.acceptProposal() },
                            onRefuse = { viewModel.refuseProposal() },
                            isLoading = isUpdatingStatus
                        )
                    }
                    showMessageButton -> {
                        MessageButton(
                            onClick = {
                                viewModel.conversationId?.let { onConversationClick(it) }
                            }
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        when {
            isLoading && proposal == null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = androidx.compose.ui.Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            proposal != null -> {
                ProposalDetailsContent(
                    proposal = proposal!!,
                    isRecruiter = viewModel.isRecruiter,
                    onTalentClick = {
                        proposal?.talentId?.let { onTalentProfileClick(it) }
                    },
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
private fun ProposalDetailsContent(
    proposal: com.example.matchify.domain.model.Proposal,
    isRecruiter: Boolean,
    onTalentClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(24.dp)) {
        // Mission Section
        Section(title = "Mission") {
            Text(
                text = proposal.missionTitle ?: "Mission",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold
            )
        }
        
        // Talent/Recruiter Section
        Section(title = if (isRecruiter) "Talent" else "Recruiter") {
            if (isRecruiter) {
                TextButton(onClick = onTalentClick) {
                    Text(
                        text = proposal.talentName ?: "Talent",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            } else {
                Text(
                    text = proposal.recruiterName ?: "Recruiter",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
        
        // Message Section
        Section(title = "Proposal Message") {
            Text(
                text = proposal.message,
                style = MaterialTheme.typography.bodyLarge
            )
        }
        
        // Date Section
        Section(title = "Sent") {
            Text(
                text = proposal.formattedDate,
                style = MaterialTheme.typography.bodyLarge
            )
        }
        
        // Status Section
        Section(title = "Status") {
            StatusBadge(status = proposal.status)
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
private fun StatusBadge(status: com.example.matchify.domain.model.ProposalStatus) {
    val (text, color) = when (status) {
        com.example.matchify.domain.model.ProposalStatus.NOT_VIEWED -> "Not viewed" to Color(0xFFFF9800)
        com.example.matchify.domain.model.ProposalStatus.VIEWED -> "Viewed" to Color(0xFF2196F3)
        com.example.matchify.domain.model.ProposalStatus.ACCEPTED -> "Accepted" to Color(0xFF4CAF50)
        com.example.matchify.domain.model.ProposalStatus.REFUSED -> "Refused" to Color(0xFFF44336)
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
private fun AcceptRefuseButtons(
    onAccept: () -> Unit,
    onRefuse: () -> Unit,
    isLoading: Boolean
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = onRefuse,
                enabled = !isLoading,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color(0xFFF44336)
                )
            ) {
                Text("Refuse")
            }
            
            Button(
                onClick = onAccept,
                enabled = !isLoading,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(16.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Accept")
                }
            }
        }
    }
}

@Composable
private fun MessageButton(onClick: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shadowElevation = 8.dp
    ) {
        Button(
            onClick = onClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text("Message", modifier = Modifier.padding(vertical = 8.dp))
        }
    }
}

