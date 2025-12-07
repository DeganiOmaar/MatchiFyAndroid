package com.example.matchify.ui.proposals.create

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

/**
 * Create Proposal Screen - Clones iOS CreateProposalView exactly
 * 
 * Design System:
 * - Background: #0F172A
 * - Input fields: #1E293B
 * - Text primary: #FFFFFF
 * - Text secondary: #9CA3AF
 * - Buttons: #2563EB
 * - Error text: #EF4444
 * - Borders: #111827
 */
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
    val proposalContent by viewModel.proposalContent.collectAsState()
    val proposedBudget by viewModel.proposedBudget.collectAsState()
    val estimatedDuration by viewModel.estimatedDuration.collectAsState()
    val isSubmitting by viewModel.isSubmitting.collectAsState()
    val isGeneratingAI by viewModel.isGeneratingAI.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val submissionSuccess by viewModel.submissionSuccess.collectAsState()
    
    // Design system colors - matching Mission Details, Alerts, Messages
    val darkBackground = Color(0xFF0F172A) // Dark navy background
    val cardBackground = Color(0xFF1E293B) // Card/input background
    val textPrimary = Color(0xFFFFFFFF) // White text
    val textSecondary = Color(0xFF94A3B8) // Light gray (matching Mission Details)
    val blueAccent = Color(0xFF3B82F6) // Blue accent (matching Mission Details)
    val errorColor = Color(0xFFEF4444) // Error red
    val borderColor = Color(0xFF334155) // Border/divider (matching Mission Details)
    val placeholderColor = Color(0xFF9CA3AF) // Placeholder text
    
    LaunchedEffect(submissionSuccess) {
        if (submissionSuccess) {
            onSuccess()
            onBack()
        }
    }
    
    Scaffold(
        topBar = {
            // Header matching Mission Details style - 58dp height, centered title
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp),
                color = darkBackground
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Left - Close button
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.size(42.dp)
                    ) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Close",
                            tint = textPrimary
                        )
                    }
                    
                    // Center - Title
                    Text(
                        text = "Create Proposal",
                        fontSize = 19.sp,
                        fontWeight = FontWeight(650),
                        color = textPrimary,
                        modifier = Modifier.weight(1f),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    
                    // Right - Spacer for symmetry
                    Spacer(modifier = Modifier.size(42.dp))
                }
            }
        },
        containerColor = darkBackground
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(darkBackground)
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // MARK: - Mission Header (read-only, same as iOS)
            MissionHeaderCard(
                missionTitle = missionTitle,
                cardBackground = cardBackground,
                textPrimary = textPrimary,
                textSecondary = textSecondary
            )
            
            // MARK: - Cover Letter Section (optional, same as iOS)
            CoverLetterSection(
                message = message,
                onMessageChange = { viewModel.updateMessage(it) },
                cardBackground = cardBackground,
                textPrimary = textPrimary,
                textSecondary = textSecondary,
                placeholderColor = placeholderColor,
                borderColor = borderColor
            )
            
            // MARK: - Proposal Section (required, same as iOS)
            ProposalSection(
                proposalContent = proposalContent,
                onProposalContentChange = { viewModel.updateProposalContent(it) },
                isGeneratingAI = isGeneratingAI,
                onGenerateAI = { viewModel.generateWithAI() },
                onCancelGeneration = { viewModel.cancelGeneration() },
                blueAccent = blueAccent,
                cardBackground = cardBackground,
                textPrimary = textPrimary,
                textSecondary = textSecondary,
                errorColor = errorColor,
                placeholderColor = placeholderColor,
                borderColor = borderColor
            )
            
            // MARK: - Optional Details Section (same as iOS)
            OptionalDetailsSection(
                proposedBudget = proposedBudget,
                onProposedBudgetChange = { viewModel.updateProposedBudget(it) },
                estimatedDuration = estimatedDuration,
                onEstimatedDurationChange = { viewModel.updateEstimatedDuration(it) },
                cardBackground = cardBackground,
                textPrimary = textPrimary,
                textSecondary = textSecondary,
                placeholderColor = placeholderColor,
                borderColor = borderColor
            )
            
            // MARK: - Error Message (same as iOS)
            if (errorMessage != null) {
                Text(
                    text = errorMessage ?: "",
                    color = errorColor,
                    fontSize = 13.sp,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // MARK: - Send Button (same as iOS)
            SendButton(
                onClick = { viewModel.sendProposal() },
                isSubmitting = isSubmitting,
                isFormValid = viewModel.isFormValid,
                blueAccent = blueAccent,
                textPrimary = textPrimary
            )
        }
    }
}

@Composable
private fun MissionHeaderCard(
    missionTitle: String,
    cardBackground: Color,
    textPrimary: Color,
    textSecondary: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = cardBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Mission",
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = textSecondary
            )
            Text(
                text = missionTitle,
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = textPrimary
            )
        }
    }
}

@Composable
private fun CoverLetterSection(
    message: String,
    onMessageChange: (String) -> Unit,
    cardBackground: Color,
    textPrimary: Color,
    textSecondary: Color,
    placeholderColor: Color,
    borderColor: Color
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Cover letter",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = textPrimary
        )
        
        OutlinedTextField(
            value = message,
            onValueChange = onMessageChange,
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            placeholder = {
                Text(
                    "Introduce yourself, highlight experience, explain your approach...",
                    color = placeholderColor,
                    fontSize = 14.sp
                )
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = textPrimary,
                unfocusedTextColor = textPrimary,
                focusedPlaceholderColor = placeholderColor,
                unfocusedPlaceholderColor = placeholderColor,
                focusedContainerColor = cardBackground,
                unfocusedContainerColor = cardBackground,
                focusedBorderColor = borderColor,
                unfocusedBorderColor = borderColor
            ),
            shape = RoundedCornerShape(16.dp),
            textStyle = androidx.compose.ui.text.TextStyle(
                fontSize = 14.sp,
                color = textPrimary
            ),
            minLines = 4,
            maxLines = 6
        )
    }
}

@Composable
private fun ProposalSection(
    proposalContent: String,
    onProposalContentChange: (String) -> Unit,
    isGeneratingAI: Boolean,
    onGenerateAI: () -> Unit,
    onCancelGeneration: () -> Unit = {},
    blueAccent: Color,
    cardBackground: Color,
    textPrimary: Color,
    textSecondary: Color,
    errorColor: Color,
    placeholderColor: Color,
    borderColor: Color
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Proposal",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = textPrimary
            )
            
            // Conditional button: Generate or Cancel
            if (isGeneratingAI) {
                // Cancel button during generation
                OutlinedButton(
                    onClick = onCancelGeneration,
                    modifier = Modifier.height(36.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = errorColor
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        errorColor.copy(alpha = 0.3f)
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = errorColor
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        "Annuler",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            } else {
                // Generate button when not generating
                OutlinedButton(
                    onClick = onGenerateAI,
                    modifier = Modifier.height(36.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = blueAccent
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        blueAccent.copy(alpha = 0.3f)
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(
                        Icons.Default.AutoAwesome,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = blueAccent
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        "Générer avec IA",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
        
        // Streaming indicator or helper text
        if (isGeneratingAI) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    strokeWidth = 2.dp,
                    color = blueAccent
                )
                Text(
                    text = "L'IA génère votre proposition en temps réel...",
                    fontSize = 13.sp,
                    color = blueAccent
                )
            }
        } else {
            Text(
                text = "Généré par IA ou écrit par vous. C'est ce que le recruteur recevra comme proposition détaillée.",
                fontSize = 13.sp,
                color = textSecondary
            )
        }
        
        // Proposal Content TextField
        val charCount = proposalContent.trim().length
        val isError = charCount > 0 && charCount < 200
        
        OutlinedTextField(
            value = proposalContent,
            onValueChange = onProposalContentChange,
            enabled = !isGeneratingAI, // Disable editing during generation
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            placeholder = {
                if (!isGeneratingAI) {
                    Text(
                        "Votre proposition détaillée pour cette mission...",
                        color = placeholderColor,
                        fontSize = 14.sp
                    )
                }
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = textPrimary,
                unfocusedTextColor = textPrimary,
                disabledTextColor = textPrimary, // Keep text visible when disabled
                focusedPlaceholderColor = placeholderColor,
                unfocusedPlaceholderColor = placeholderColor,
                focusedContainerColor = cardBackground,
                unfocusedContainerColor = cardBackground,
                disabledContainerColor = cardBackground,
                focusedBorderColor = if (isError) errorColor.copy(alpha = 0.5f) else borderColor,
                unfocusedBorderColor = if (isError) errorColor.copy(alpha = 0.5f) else borderColor,
                disabledBorderColor = borderColor
            ),
            shape = RoundedCornerShape(16.dp),
            textStyle = androidx.compose.ui.text.TextStyle(
                fontSize = 14.sp,
                color = textPrimary
            ),
            minLines = 8,
            maxLines = 12
        )
        
        // Character count (same as iOS)
        if (proposalContent.isNotEmpty()) {
            Text(
                text = "$charCount / 200 caractères minimum",
                fontSize = 12.sp,
                color = if (charCount < 200) errorColor else textSecondary
            )
        }
    }
}

@Composable
private fun OptionalDetailsSection(
    proposedBudget: String,
    onProposedBudgetChange: (String) -> Unit,
    estimatedDuration: String,
    onEstimatedDurationChange: (String) -> Unit,
    cardBackground: Color,
    textPrimary: Color,
    textSecondary: Color,
    placeholderColor: Color,
    borderColor: Color
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Optional details",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = textPrimary
        )
        
        // Proposed Budget
        OutlinedTextField(
            value = proposedBudget,
            onValueChange = onProposedBudgetChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text(
                    "Proposed budget (€)",
                    color = placeholderColor,
                    fontSize = 14.sp
                )
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = textPrimary,
                unfocusedTextColor = textPrimary,
                focusedPlaceholderColor = placeholderColor,
                unfocusedPlaceholderColor = placeholderColor,
                focusedContainerColor = cardBackground,
                unfocusedContainerColor = cardBackground,
                focusedBorderColor = borderColor,
                unfocusedBorderColor = borderColor
            ),
            shape = RoundedCornerShape(12.dp),
            textStyle = androidx.compose.ui.text.TextStyle(
                fontSize = 14.sp,
                color = textPrimary
            ),
            singleLine = true
        )
        
        // Estimated Duration
        OutlinedTextField(
            value = estimatedDuration,
            onValueChange = onEstimatedDurationChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text(
                    "Estimated duration (e.g. 8 weeks)",
                    color = placeholderColor,
                    fontSize = 14.sp
                )
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = textPrimary,
                unfocusedTextColor = textPrimary,
                focusedPlaceholderColor = placeholderColor,
                unfocusedPlaceholderColor = placeholderColor,
                focusedContainerColor = cardBackground,
                unfocusedContainerColor = cardBackground,
                focusedBorderColor = borderColor,
                unfocusedBorderColor = borderColor
            ),
            shape = RoundedCornerShape(12.dp),
            textStyle = androidx.compose.ui.text.TextStyle(
                fontSize = 14.sp,
                color = textPrimary
            ),
            singleLine = true
        )
    }
}

@Composable
private fun SendButton(
    onClick: () -> Unit,
    isSubmitting: Boolean,
    isFormValid: Boolean,
    blueAccent: Color,
    textPrimary: Color
) {
    Button(
        onClick = onClick,
        enabled = isFormValid && !isSubmitting,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isFormValid) blueAccent else blueAccent.copy(alpha = 0.4f),
            disabledContainerColor = blueAccent.copy(alpha = 0.4f)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        if (isSubmitting) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                color = textPrimary,
                strokeWidth = 2.dp
            )
        } else {
            Text(
                "Send proposal",
                fontSize = 17.sp,
                fontWeight = FontWeight.SemiBold,
                color = textPrimary
            )
        }
    }
}




