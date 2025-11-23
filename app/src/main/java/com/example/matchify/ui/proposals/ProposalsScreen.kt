package com.example.matchify.ui.proposals

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
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
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Mission title only
            Text(
                text = proposal.missionTitle ?: "Mission",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1A1A1A)
            )
            
            // Button - style like "Book an Appointment" with chevrons on the right
            val buttonInteractionSource = remember { MutableInteractionSource() }
            val isButtonPressed by buttonInteractionSource.collectIsPressedAsState()
            val buttonShadow = if (isButtonPressed) 8f else 12f
            
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .shadow(
                        elevation = buttonShadow.dp,
                        shape = RoundedCornerShape(12.dp),
                        spotColor = Color.Black.copy(alpha = 0.3f),
                        ambientColor = Color.Black.copy(alpha = 0.2f)
                    )
                    .clickable(
                        interactionSource = buttonInteractionSource,
                        indication = null
                    ) {
                        onClick()
                    }
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                Color(0xFF4A90E2),
                                Color(0xFF61A5C2),
                                Color(0xFF7DB8D6)
                            )
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )
                    .padding(horizontal = 20.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = when (proposal.status) {
                            com.example.matchify.domain.model.ProposalStatus.NOT_VIEWED -> "Not viewed"
                            com.example.matchify.domain.model.ProposalStatus.VIEWED -> "Viewed"
                            com.example.matchify.domain.model.ProposalStatus.ACCEPTED -> "Accepted"
                            com.example.matchify.domain.model.ProposalStatus.REFUSED -> "Refused"
                        },
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color.White
                    )
                    
                    // Chevrons on the right (like ">>")
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ChevronRight,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp),
                            tint = Color.White
                        )
                        Icon(
                            imageVector = Icons.Filled.ChevronRight,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp),
                            tint = Color.White
                        )
                    }
                }
            }
        }
    }
}


