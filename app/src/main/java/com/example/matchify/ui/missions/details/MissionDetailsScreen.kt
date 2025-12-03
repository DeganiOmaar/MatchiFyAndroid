package com.example.matchify.ui.missions.details

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.matchify.domain.model.postedDaysAgoText

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MissionDetailsScreen(
    missionId: String,
    onBack: () -> Unit,
    onCreateProposal: (String, String) -> Unit = { _, _ -> },
    viewModel: MissionDetailsViewModel = viewModel(
        factory = MissionDetailsViewModelFactory(missionId)
    )
) {
    val mission by viewModel.mission.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val canApply by viewModel.canApply.collectAsState()
    val shouldShowApplyButton by viewModel.shouldShowApplyButton.collectAsState()
    val isTogglingFavorite by viewModel.isTogglingFavorite.collectAsState()
    val isTalent = viewModel.isTalent
    
    // State pour le popup d'analyse de mission
    var showMissionFitAnalysis by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        viewModel.loadMission()
    }
    
    // Dark theme colors matching proposals, messages, alerts
    val darkBackground = Color(0xFF0F172A) // Dark navy background (same as proposals, messages, alerts)
    val textPrimary = Color(0xFFFFFFFF) // White text
    val textSecondary = Color(0xFF94A3B8) // Light gray text
    val blueAccent = Color(0xFF3B82F6) // Blue accent
    val dividerColor = Color(0xFF334155) // Divider color
    
    Scaffold(
        containerColor = darkBackground,
        topBar = {
            // Header - 56-60dp height, #0F172A background
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp), // 56-60dp
                color = darkBackground
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp), // 16dp padding from edges
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Left area - back button
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.size(42.dp)
                    ) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = textPrimary
                        )
                    }
                    
                    // Center Title - perfectly centered
                    Text(
                        text = "Details",
                        fontSize = 19.sp, // 18-20sp
                        fontWeight = FontWeight(650), // 600-700
                        color = textPrimary,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center
                    )
                    
                    // Right area - Gemini icon (only for talents) or empty spacer
                    if (isTalent) {
                        IconButton(
                            onClick = { showMissionFitAnalysis = true },
                            modifier = Modifier.size(42.dp)
                        ) {
                            Icon(
                                Icons.Default.AutoAwesome,
                                contentDescription = "Gemini",
                                tint = textPrimary
                            )
                        }
                    } else {
                        Spacer(modifier = Modifier.size(42.dp))
                    }
                }
            }
        },
        bottomBar = {
            // Apply Now button - only for talents, not recruiters
            if (shouldShowApplyButton && mission != null) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = darkBackground,
                    shadowElevation = 8.dp
                ) {
                    Button(
                        onClick = {
                            mission?.let { 
                                onCreateProposal(it.missionId, it.title)
                            }
                        },
                        enabled = canApply && !isLoading,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 16.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = blueAccent,
                            contentColor = textPrimary
                        )
                    ) {
                        Text(
                            text = if (canApply) "Apply Now" else "Already applied",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        when {
            isLoading && mission == null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(darkBackground)
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = blueAccent)
                }
            }
            errorMessage != null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(darkBackground)
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = errorMessage ?: "Error",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
            mission != null -> {
                MissionDetailsContent(
                    mission = mission!!,
                    darkBackground = darkBackground,
                    textPrimary = textPrimary,
                    textSecondary = textSecondary,
                    blueAccent = blueAccent,
                    dividerColor = dividerColor,
                    modifier = Modifier
                        .fillMaxSize()
                        .background(darkBackground)
                        .padding(paddingValues)
                )
            }
        }
    }
    
    // Afficher le popup d'analyse de mission
    if (showMissionFitAnalysis && isTalent) {
        MissionFitAnalysisDialog(
            missionId = missionId,
            onDismiss = { showMissionFitAnalysis = false }
        )
    }
}

@Composable
private fun MissionDetailsContent(
    mission: com.example.matchify.domain.model.Mission,
    darkBackground: Color,
    textPrimary: Color,
    textSecondary: Color,
    blueAccent: Color,
    dividerColor: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Mission Title
        Text(
            text = mission.title,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = textPrimary,
            lineHeight = 32.sp
        )
        
        // Posted date
        Text(
            text = mission.postedDaysAgoText,
            fontSize = 14.sp,
            color = textSecondary,
            fontWeight = FontWeight.Normal
        )
        
        // SUMMARY Section
        Text(
            text = "SUMMARY",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = textPrimary,
            letterSpacing = 0.5.sp
        )
        
        Text(
            text = mission.description ?: "",
            fontSize = 15.sp,
            color = textSecondary,
            lineHeight = 22.sp
        )
        
        // Divider
        HorizontalDivider(
            color = dividerColor,
            thickness = 1.dp
        )
        
        // Budget / Price Section
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Wallet icon
            Surface(
                modifier = Modifier.size(44.dp),
                shape = RoundedCornerShape(10.dp),
                color = blueAccent.copy(alpha = 0.2f)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.AttachMoney,
                        contentDescription = null,
                        tint = blueAccent,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
            
            Column {
                Text(
                    text = "Budget / Price",
                    fontSize = 14.sp,
                    color = textSecondary,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = mission.formattedBudget,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = textPrimary
                )
            }
        }
        
        // Skills and Expertise Section
        Text(
            text = "Skills and Expertise",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = textPrimary,
            letterSpacing = 0.5.sp
        )
        
        // Skills tags with wrapping
        val skillsList = mission.skills ?: emptyList()
        if (skillsList.isEmpty()) {
            Text(
                text = "No skills specified.",
                fontSize = 14.sp,
                color = textSecondary
            )
        } else {
            FlowRowLayout(
                items = skillsList,
                horizontalSpacing = 8.dp,
                verticalSpacing = 8.dp
            ) { skill ->
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = textSecondary.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = skill,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = textSecondary,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                    )
                }
            }
        }
        
        // Activity on this mission Section
        Text(
            text = "Activity on this mission",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = textPrimary,
            letterSpacing = 0.5.sp
        )
        
        // Activity rows
        ActivityRow(
            title = "Proposals",
            value = formatProposalsCount(mission.proposals),
            textPrimary = textPrimary,
            textSecondary = textSecondary
        )
        
        ActivityRow(
            title = "Interviewing",
            value = "${mission.interviewing}",
            textPrimary = textPrimary,
            textSecondary = textSecondary
        )
        
        // Bottom spacing for Apply button
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun ActivityRow(
    title: String,
    value: String,
    textPrimary: Color,
    textSecondary: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = title,
            fontSize = 15.sp,
            color = textSecondary
        )
        Text(
            text = value,
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            color = textPrimary
        )
    }
}

private fun formatProposalsCount(count: Int): String {
    return when {
        count <= 5 -> "$count"
        count <= 10 -> "5 to 10"
        else -> "10+"
    }
}

/**
 * Layout personnalisé qui wrap les items comme FlowRow
 * Permet aux tags de s'afficher sur plusieurs lignes si nécessaire
 */
@Composable
private fun FlowRowLayout(
    items: List<String>,
    horizontalSpacing: Dp,
    verticalSpacing: Dp,
    content: @Composable (String) -> Unit
) {
    BoxWithConstraints {
        val maxWidthPx = with(LocalDensity.current) { maxWidth.toPx() }
        val density = LocalDensity.current
        
        // Calculer les lignes en fonction de la largeur disponible
        val rowsList = mutableListOf<MutableList<String>>()
        var currentRow = mutableListOf<String>()
        var currentRowWidth = 0f
        
        items.forEach { skill ->
            // Estimation de la largeur (approximative en dp)
            // Largeur du texte + padding horizontal (14dp * 2) + marge
            val estimatedWidthDp = (skill.length * 8 + 28 + 8).dp
            val estimatedWidthPx = with(density) { estimatedWidthDp.toPx() }
            
            if (currentRowWidth + estimatedWidthPx > maxWidthPx && currentRow.isNotEmpty()) {
                // Nouvelle ligne
                rowsList.add(currentRow.toMutableList())
                currentRow = mutableListOf(skill)
                currentRowWidth = estimatedWidthPx
            } else {
                currentRow.add(skill)
                currentRowWidth += estimatedWidthPx + with(density) { horizontalSpacing.toPx() }
            }
        }
        
        // Dernière ligne
        if (currentRow.isNotEmpty()) {
            rowsList.add(currentRow)
        }
        
        Column(
            verticalArrangement = Arrangement.spacedBy(verticalSpacing)
        ) {
            rowsList.forEach { row ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(horizontalSpacing)
                ) {
                    row.forEach { skill ->
                        content(skill)
                    }
                }
            }
        }
    }
}
