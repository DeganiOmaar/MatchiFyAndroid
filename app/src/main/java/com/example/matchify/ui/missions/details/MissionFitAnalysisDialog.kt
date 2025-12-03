package com.example.matchify.ui.missions.details

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.matchify.data.local.AuthPreferencesProvider
import com.example.matchify.data.remote.AiRepository
import com.example.matchify.data.remote.ApiService
import com.example.matchify.data.remote.dto.ai.MissionFitResponseDto

/**
 * Dialog popup pour l'analyse de compatibilité mission-profil
 * Identique au popup iOS MissionFitAnalysisView
 */
@Composable
fun MissionFitAnalysisDialog(
    missionId: String,
    onDismiss: () -> Unit,
    viewModel: MissionFitAnalysisViewModel = viewModel(
        factory = MissionFitAnalysisViewModelFactory(missionId)
    )
) {
    val analysis by viewModel.analysis.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    
    // Démarrer l'analyse au moment de l'ouverture
    LaunchedEffect(Unit) {
        viewModel.analyzeMissionFit(missionId)
    }
    
    // Couleurs du design system (identique à iOS et au design Android global)
    val darkBackground = Color(0xFF0F172A)
    val textPrimary = Color(0xFFFFFFFF)
    val textSecondary = Color(0xFF94A3B8)
    val primaryColor = Color(0xFF3B82F6)
    val dividerColor = Color(0xFF334155)
    val cardBackground = Color(0xFF1E293B) // Slightly lighter than background
    
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.4f))
                .clickable { onDismiss() },
            contentAlignment = Alignment.Center
        ) {
            // Carte principale (comme iOS)
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.9f)
                    .padding(horizontal = 20.dp, vertical = 60.dp)
                    .clip(RoundedCornerShape(20.dp)),
                color = cardBackground,
                shadowElevation = 30.dp,
                onClick = { /* Empêcher la fermeture au clic sur la carte */ }
            ) {
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    // Header
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 20.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Mission Fit Analysis",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = textPrimary
                        )
                        
                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                                tint = textSecondary,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                    
                    // Divider
                    HorizontalDivider(
                        color = dividerColor,
                        thickness = 1.dp
                    )
                    
                    // Content
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(24.dp)
                        ) {
                            when {
                                isLoading -> {
                                    // Loading state
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 60.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.spacedBy(16.dp)
                                    ) {
                                        CircularProgressIndicator(
                                            color = primaryColor,
                                            modifier = Modifier.size(48.dp)
                                        )
                                        Text(
                                            text = "Analyzing mission fit...",
                                            fontSize = 14.sp,
                                            color = textSecondary
                                        )
                                    }
                                }
                                errorMessage != null -> {
                                    // Error state
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 20.dp, vertical = 60.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.spacedBy(16.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = null,
                                            tint = Color(0xFFFFA500),
                                            modifier = Modifier.size(40.dp)
                                        )
                                        Text(
                                            text = errorMessage ?: "Error",
                                            fontSize = 14.sp,
                                            color = textPrimary,
                                            modifier = Modifier.fillMaxWidth()
                                        )
                                    }
                                }
                                analysis != null -> {
                                    // Success state avec le graphique
                                    analysis?.let { analysisData ->
                                        // Radar Chart
                                        SpiderChartView(
                                            data = analysisData.radar,
                                            modifier = Modifier
                                                .height(280.dp)
                                                .fillMaxWidth()
                                                .padding(horizontal = 20.dp)
                                        )
                                        
                                        // Match Score Section
                                        ScoreSection(
                                            analysis = analysisData,
                                            textPrimary = textPrimary,
                                            textSecondary = textSecondary
                                        )
                                        
                                        // Summary Section
                                        SummarySection(
                                            summary = analysisData.shortSummary,
                                            textPrimary = textPrimary,
                                            textSecondary = textSecondary
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ScoreSection(
    analysis: MissionFitResponseDto,
    textPrimary: Color,
    textSecondary: Color
) {
    val scoreColor = when {
        analysis.score >= 80 -> Color(0xFF33CC33) // Green
        analysis.score >= 50 -> Color(0xFFFF9900) // Orange
        else -> Color(0xFFFF4D4D) // Red
    }
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Match Score",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = textSecondary
        )
        
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Score indicator circle
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .background(scoreColor, shape = CircleShape)
            )
            
            Text(
                text = "${analysis.score}",
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold,
                color = scoreColor
            )
        }
        
        // Progress bar
        ProgressBarView(
            score = analysis.score,
            color = scoreColor,
            textSecondary = textSecondary
        )
    }
}

@Composable
private fun ProgressBarView(
    score: Int,
    color: Color,
    textSecondary: Color
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(8.dp)
            .padding(horizontal = 20.dp)
            .background(
                textSecondary.copy(alpha = 0.15f),
                shape = RoundedCornerShape(4.dp)
            )
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(score / 100f)
                .background(
                    color,
                    shape = RoundedCornerShape(4.dp)
                )
        )
    }
}

@Composable
private fun SummarySection(
    summary: String,
    textPrimary: Color,
    textSecondary: Color
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Analysis Summary",
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = textPrimary
        )
        
        Text(
            text = summary,
            fontSize = 14.sp,
            color = textSecondary,
            lineHeight = 20.sp
        )
    }
}

// Factory pour le ViewModel
class MissionFitAnalysisViewModelFactory(
    private val missionId: String
) : androidx.lifecycle.ViewModelProvider.Factory {
    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MissionFitAnalysisViewModel::class.java)) {
            val prefs = AuthPreferencesProvider.getInstance().get()
            val apiService = ApiService.getInstance()
            val aiApi = apiService.aiApi
            val repository = AiRepository(aiApi)
            @Suppress("UNCHECKED_CAST")
            return MissionFitAnalysisViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
