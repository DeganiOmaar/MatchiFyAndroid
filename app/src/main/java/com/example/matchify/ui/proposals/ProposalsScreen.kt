package com.example.matchify.ui.proposals

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun ProposalsScreen(
    onProposalClick: (String) -> Unit = {},
    viewModel: ProposalsViewModel = viewModel(factory = ProposalsViewModelFactory())
) {
    val proposals by viewModel.proposals.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    
    LaunchedEffect(Unit) {
        viewModel.loadProposals()
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Proposals",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        when {
            isLoading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            }
            errorMessage != null -> {
                Text(
                    text = errorMessage ?: "Une erreur est survenue",
                    color = MaterialTheme.colorScheme.error
                )
            }
            proposals.isEmpty() -> {
                EmptyProposalsView(viewModel.isRecruiter)
            }
            else -> {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(proposals) { proposal ->
                        ProposalCard(
                            proposal = proposal,
                            isRecruiter = viewModel.isRecruiter,
                            onClick = { onProposalClick(proposal.proposalId) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyProposalsView(isRecruiter: Boolean) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "No proposals yet",
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                text = if (isRecruiter) {
                    "You have not received any proposals yet."
                } else {
                    "You have not applied to any missions yet."
                },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun ProposalCard(
    proposal: com.example.matchify.domain.model.Proposal,
    isRecruiter: Boolean,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = proposal.missionTitle ?: "Mission",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f)
                )
                StatusBadge(status = proposal.status)
            }
            
            if (isRecruiter && proposal.talentName != null) {
                Text(
                    text = "Par ${proposal.talentName}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else if (!isRecruiter && proposal.recruiterName != null) {
                Text(
                    text = "Par ${proposal.recruiterName}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Text(
                text = proposal.message,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 3
            )
            
            Text(
                text = proposal.formattedDate,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
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
        shape = MaterialTheme.shapes.small
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = color,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
        )
    }
}

