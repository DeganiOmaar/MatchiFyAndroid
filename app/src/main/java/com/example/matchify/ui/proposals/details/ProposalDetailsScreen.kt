package com.example.matchify.ui.proposals.details

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Message
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.matchify.domain.model.ProposalStatus

@Composable
fun ProposalDetailsScreen(
    proposalId: String,
    onBack: () -> Unit,
    onTalentProfileClick: (String) -> Unit = {},
    onConversationClick: (String) -> Unit = {},
    onScheduleInterview: (String) -> Unit = {}, // proposalId
    viewModel: ProposalDetailsViewModel = viewModel(
        factory = ProposalDetailsViewModelFactory(proposalId)
    )
) {
    val proposal by viewModel.proposal.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val isUpdatingStatus by viewModel.isUpdatingStatus.collectAsState()
    val canShowActions by viewModel.canShowActions.collectAsState()
    val showMessageButton by viewModel.showMessageButton.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val justRefused by viewModel.justRefused.collectAsState()
    
    val snackbarHostState = remember { SnackbarHostState() }
    
    var showRejectDialog by remember { mutableStateOf(false) }
    var rejectionReason by remember { mutableStateOf("") }
    var rejectionError by remember { mutableStateOf<String?>(null) }
    
    // Fermer automatiquement la page seulement si le recruteur vient de refuser
    LaunchedEffect(proposal?.status, isUpdatingStatus, justRefused) {
        // Seulement si :
        // 1. Le recruteur vient de refuser (justRefused = true)
        // 2. Le statut est REFUSED
        // 3. On n'est plus en train de mettre à jour
        // 4. Il y a une raison de refus
        if (justRefused &&
            proposal?.status == ProposalStatus.REFUSED && 
            !isUpdatingStatus && 
            proposal?.rejectionReason != null) {
            // Attendre un court instant pour que l'utilisateur voie la confirmation
            kotlinx.coroutines.delay(800)
            // Fermer la page et retourner à la liste des proposals
            onBack()
        }
    }
    
    // Show error message in Snackbar
    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            snackbarHostState.showSnackbar(
                message = it,
                duration = SnackbarDuration.Long
            )
        }
    }
    
    // Rejection Dialog
    if (showRejectDialog) {
        AlertDialog(
            onDismissRequest = { showRejectDialog = false },
            containerColor = Color(0xFF1E293B),
            titleContentColor = Color.White,
            textContentColor = Color(0xFF9CA3AF),
            title = { Text("Reject Proposal", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("Please provide a reason for rejecting this proposal.")
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = rejectionReason,
                        onValueChange = { 
                            rejectionReason = it
                            rejectionError = null
                        },
                        label = { Text("Reason") },
                        modifier = Modifier.fillMaxWidth(),
                        isError = rejectionError != null,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedContainerColor = Color(0xFF0F172A),
                            unfocusedContainerColor = Color(0xFF0F172A),
                            focusedBorderColor = Color(0xFF3B82F6),
                            unfocusedBorderColor = Color(0xFF374151),
                            cursorColor = Color(0xFF3B82F6),
                            focusedLabelColor = Color(0xFF3B82F6),
                            unfocusedLabelColor = Color(0xFF9CA3AF)
                        ),
                        supportingText = {
                            if (rejectionError != null) {
                                Text(rejectionError!!, color = Color(0xFFEF4444))
                            }
                        }
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (rejectionReason.isBlank()) {
                            rejectionError = "Reason is required"
                        } else {
                            // Fermer le dialog d'abord
                            showRejectDialog = false
                            // Ensuite refuser la proposal (la page se fermera automatiquement après)
                            viewModel.refuseProposal(rejectionReason.trim())
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFEF4444),
                        contentColor = Color.White
                    ),
                    enabled = !isUpdatingStatus
                ) {
                    if (isUpdatingStatus) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("Reject")
                    }
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showRejectDialog = false },
                    colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFF9CA3AF))
                ) {
                    Text("Cancel")
                }
            }
        )
    }
    
    LaunchedEffect(Unit) {
        viewModel.loadProposal()
    }
    
    Scaffold(
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
                snackbar = { data ->
                    Snackbar(
                        snackbarData = data,
                        containerColor = Color(0xFF1E293B),
                        contentColor = Color.White,
                        actionColor = Color(0xFF3B82F6)
                    )
                }
            )
        },
        containerColor = Color(0xFF0F172A)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = paddingValues.calculateBottomPadding())
                .background(Color(0xFF0F172A))
    ) {
        // Custom Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF1E293B))
                .statusBarsPadding()
                .padding(horizontal = 8.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.Rounded.ArrowBack,
                    contentDescription = "Back",
                    tint = Color(0xFF3B82F6)
                )
            }
            Text(
                text = "Proposal Details",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        Box(modifier = Modifier.weight(1f)) {
            if (isLoading && proposal == null) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = Color(0xFF3B82F6)
                )
            } else if (proposal != null) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Mission Card
                    DetailCard(title = "Mission") {
                        Text(
                            text = proposal!!.missionTitle ?: "Mission",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    // User Card (Talent or Recruiter)
                    DetailCard(title = if (viewModel.isRecruiter) "Talent" else "Recruiter") {
                        if (viewModel.isRecruiter) {
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = proposal!!.talentFullName,
                                            color = Color.White,
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                        if (proposal!!.talent?.email != null) {
                                            Text(
                                                text = proposal!!.talent!!.email!!,
                                                color = Color(0xFF9CA3AF),
                                                fontSize = 14.sp,
                                                modifier = Modifier.padding(top = 4.dp)
                                            )
                                        }
                                    }
                                    Column(
                                        verticalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        TextButton(onClick = { proposal!!.talentId?.let { onTalentProfileClick(it) } }) {
                                            Text("View Profile", color = Color(0xFF3B82F6), fontSize = 12.sp)
                                        }
                                    }
                                }
                            }
                        } else {
                            Text(
                                text = proposal!!.recruiterName ?: "Recruiter",
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    // Message Card
                    DetailCard(title = "Message") {
                        Text(
                            text = proposal!!.message,
                            color = Color(0xFFE2E8F0),
                            fontSize = 15.sp,
                            lineHeight = 22.sp
                        )
                    }

                    // Proposal Content Card (if available)
                    if (!proposal!!.proposalContent.isNullOrBlank()) {
                        DetailCard(title = "Proposal") {
                            Text(
                                text = proposal!!.proposalContent!!,
                                color = Color(0xFFE2E8F0),
                                fontSize = 15.sp,
                                lineHeight = 22.sp
                            )
                        }
                    }

                    // Status & Date Card
                    DetailCard(title = "Status & Info") {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            StatusBadge(status = proposal!!.status)
                            Text(
                                text = proposal!!.formattedDate,
                                color = Color(0xFF9CA3AF),
                                fontSize = 14.sp
                            )
                        }
                        
                        if (proposal!!.status == ProposalStatus.REFUSED && !proposal!!.rejectionReason.isNullOrBlank()) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Rejection Reason:",
                                color = Color(0xFFEF4444),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = proposal!!.rejectionReason!!,
                                color = Color(0xFFEF4444).copy(alpha = 0.9f),
                                fontSize = 14.sp,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(80.dp)) // Space for bottom bar
                }
            }
        }
    }
    
    // Bottom Action Bar
    if (viewModel.isRecruiter && (canShowActions || showMessageButton)) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 16.dp, start = 16.dp, end = 16.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            if (canShowActions) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Refuse Button
                    Button(
                        onClick = { showRejectDialog = true },
                        enabled = !isUpdatingStatus,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF1E293B),
                            contentColor = Color(0xFFEF4444),
                            disabledContainerColor = Color(0xFF1E293B).copy(alpha = 0.6f)
                        ),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEF4444)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp)
                    ) {
                        Icon(Icons.Rounded.Close, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Refuse")
                    }
                    
                    // Accept Button
                    Button(
                        onClick = { viewModel.acceptProposal() },
                        enabled = !isUpdatingStatus,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF3B82F6),
                            contentColor = Color.White,
                            disabledContainerColor = Color(0xFF3B82F6).copy(alpha = 0.6f)
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp)
                    ) {
                        if (isUpdatingStatus) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(Icons.Rounded.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Accept")
                        }
                    }
                }
            } else if (showMessageButton) {
                Button(
                    onClick = { viewModel.conversationId?.let { onConversationClick(it) } },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF3B82F6),
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Icon(Icons.Rounded.Message, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Message Talent")
                }
            }
        }
            }
        }
    }


@Composable
private fun DetailCard(
    title: String,
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF1E293B), RoundedCornerShape(16.dp))
            .border(1.dp, Color(0xFF374151), RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Text(
            text = title,
            color = Color(0xFF9CA3AF),
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        content()
    }
}

@Composable
private fun StatusBadge(status: ProposalStatus) {
    val (text, color, bgColor) = when (status) {
        ProposalStatus.NOT_VIEWED -> Triple("Not Viewed", Color(0xFFF59E0B), Color(0xFF451A03)) // Amber
        ProposalStatus.VIEWED -> Triple("Viewed", Color(0xFF3B82F6), Color(0xFF172554)) // Blue
        ProposalStatus.ACCEPTED -> Triple("Accepted", Color(0xFF10B981), Color(0xFF064E3B)) // Emerald
        ProposalStatus.REFUSED -> Triple("Refused", Color(0xFFEF4444), Color(0xFF450A0A)) // Red
    }
    
    Box(
        modifier = Modifier
            .background(bgColor, RoundedCornerShape(8.dp))
            .border(1.dp, color.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            text = text,
            color = color,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

