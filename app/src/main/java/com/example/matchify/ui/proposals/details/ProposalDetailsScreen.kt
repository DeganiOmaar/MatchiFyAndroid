package com.example.matchify.ui.proposals.details

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
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
        containerColor = Color(0xFFF5F7FA),
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "Proposal Details",
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1A1A1A)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.Default.ArrowBack, 
                            contentDescription = "Back",
                            tint = Color(0xFF1A1A1A)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
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
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Mission Section
            Section(title = "Mission") {
                Text(
                    text = proposal.missionTitle ?: "Mission",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A1A1A)
                )
            }
            
            // Talent/Recruiter Section
            Section(title = if (isRecruiter) "Talent" else "Recruiter") {
                if (isRecruiter) {
                    TextButton(
                        onClick = onTalentClick,
                        contentPadding = PaddingValues(0.dp)
                    ) {
                    Text(
                        text = proposal.talentName ?: "Talent",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF2196F3)
                    )
                    }
                } else {
                    Text(
                        text = proposal.recruiterName ?: "Recruiter",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF1A1A1A)
                    )
                }
            }
            
            // Message Section
            Section(title = "Proposal Message") {
                Text(
                    text = proposal.message,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color(0xFF4A4A4A)
                )
            }
            
            // Date Section
            Section(title = "Sent") {
                Text(
                    text = proposal.formattedDate,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color(0xFF4A4A4A)
                )
            }
            
            // Status Section
            Section(title = "Status") {
                StatusBadge(status = proposal.status)
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
        color = Color.White,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            // Refuse Button - Red X icon
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .shadow(
                        elevation = 4.dp,
                        shape = CircleShape,
                        spotColor = Color(0xFFF44336).copy(alpha = 0.3f)
                    )
                    .background(
                        color = Color.White,
                        shape = CircleShape
                    )
                    .clickable(
                        enabled = !isLoading,
                        onClick = onRefuse
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color(0xFFF44336),
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = "Refuse",
                        tint = Color(0xFFF44336),
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
            
            // Accept Button - Green Check icon
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .shadow(
                        elevation = 4.dp,
                        shape = CircleShape,
                        spotColor = Color(0xFF4CAF50).copy(alpha = 0.3f)
                    )
                    .background(
                        color = Color.White,
                        shape = CircleShape
                    )
                    .clickable(
                        enabled = !isLoading,
                        onClick = onAccept
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color(0xFF4CAF50),
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = "Accept",
                        tint = Color(0xFF4CAF50),
                        modifier = Modifier.size(32.dp)
                    )
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

