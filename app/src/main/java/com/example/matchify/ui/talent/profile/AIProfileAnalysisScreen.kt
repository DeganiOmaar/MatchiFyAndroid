package com.example.matchify.ui.talent.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.matchify.data.remote.dto.ai.ProfileAnalysisResponseDto
import java.text.SimpleDateFormat
import java.util.*

/**
 * AI Profile Analysis Screen - Clone exact de la version iOS
 * Utilise le nouveau design system Android (#0F172A)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AIProfileAnalysisScreen(
    onBack: () -> Unit,
    viewModel: AIProfileAnalysisViewModel = viewModel(
        factory = AIProfileAnalysisViewModelFactory()
    )
) {
    val analysis by viewModel.analysis.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    
    var showError by remember { mutableStateOf(false) }
    
    // Load latest analysis when screen appears
    LaunchedEffect(Unit) {
        viewModel.loadLatestAnalysis()
    }
    
    // Show error dialog if needed
    LaunchedEffect(errorMessage) {
        if (errorMessage != null) {
            showError = true
        }
    }
    
    // Dark theme colors matching new Android design system
    val darkBackground = Color(0xFF0F172A)
    val cardBackground = Color(0xFF1E293B)
    val textPrimary = Color(0xFFFFFFFF)
    val textSecondary = Color(0xFF9CA3AF)
    val textTertiary = Color(0xFFCBD5E1)
    val blueAccent = Color(0xFF2563EB)
    val separator = Color(0xFF334155)
    
    Scaffold(
        containerColor = darkBackground,
        topBar = {
            // Header matching iOS and Android design system
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                color = darkBackground
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Back button
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = textPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    
                    // Centered title
                    Text(
                        text = "AI Analysis",
                        fontSize = 18.sp,
                        fontWeight = FontWeight(600),
                        color = textPrimary,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center
                    )
                    
                    // Right spacer for symmetry
                    Spacer(modifier = Modifier.size(40.dp))
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(darkBackground)
                .padding(paddingValues)
        ) {
            when {
                isLoading && analysis == null -> {
                    LoadingView(
                        darkBackground = darkBackground,
                        blueAccent = blueAccent,
                        textSecondary = textSecondary
                    )
                }
                analysis != null -> {
                    AnalysisContent(
                        analysis = analysis!!,
                        onReanalyze = {
                            viewModel.analyzeProfile { error ->
                                // Error handled by viewModel state
                            }
                        },
                        isLoading = isLoading,
                        darkBackground = darkBackground,
                        cardBackground = cardBackground,
                        textPrimary = textPrimary,
                        textSecondary = textSecondary,
                        textTertiary = textTertiary,
                        blueAccent = blueAccent,
                        separator = separator
                    )
                }
                else -> {
                    EmptyStateView(
                        onAnalyze = {
                            viewModel.analyzeProfile { error ->
                                // Error handled by viewModel state
                            }
                        },
                        isLoading = isLoading,
                        darkBackground = darkBackground,
                        cardBackground = cardBackground,
                        textPrimary = textPrimary,
                        textSecondary = textSecondary,
                        blueAccent = blueAccent
                    )
                }
            }
        }
    }
    
    // Error dialog
    if (showError && errorMessage != null) {
        AlertDialog(
            onDismissRequest = { showError = false },
            title = { Text("Erreur") },
            text = { Text(errorMessage ?: "") },
            confirmButton = {
                TextButton(onClick = { showError = false }) {
                    Text("OK")
                }
            }
        )
    }
}

@Composable
private fun LoadingView(
    darkBackground: Color,
    blueAccent: Color,
    textSecondary: Color
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(darkBackground)
            .padding(vertical = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(48.dp),
            color = blueAccent
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Analyse en cours...",
            fontSize = 14.sp,
            color = textSecondary
        )
    }
}

@Composable
private fun EmptyStateView(
    onAnalyze: () -> Unit,
    isLoading: Boolean,
    darkBackground: Color,
    cardBackground: Color,
    textPrimary: Color,
    textSecondary: Color,
    blueAccent: Color
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(darkBackground)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.AutoAwesome,
            contentDescription = null,
            modifier = Modifier.size(48.dp),
            tint = blueAccent.copy(alpha = 0.6f)
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "Aucune analyse disponible",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = textPrimary
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "Analysez votre profil pour obtenir des recommandations personnalisées",
            fontSize = 14.sp,
            color = textSecondary,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Button(
            onClick = onAnalyze,
            enabled = !isLoading,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = blueAccent,
                contentColor = textPrimary
            )
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = textPrimary,
                    strokeWidth = 2.dp
                )
            } else {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Analyser mon profil",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun AnalysisContent(
    analysis: ProfileAnalysisResponseDto,
    onReanalyze: () -> Unit,
    isLoading: Boolean,
    darkBackground: Color,
    cardBackground: Color,
    textPrimary: Color,
    textSecondary: Color,
    textTertiary: Color,
    blueAccent: Color,
    separator: Color
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(darkBackground)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Score Indicator
        ScoreIndicator(
            score = analysis.profileScore,
            textPrimary = textPrimary,
            cardBackground = cardBackground
        )
        
        HorizontalDivider(color = separator, thickness = 1.dp)
        
        // Summary Section
        SummarySection(
            summary = analysis.summary,
            textPrimary = textPrimary,
            textTertiary = textTertiary
        )
        
        HorizontalDivider(color = separator, thickness = 1.dp)
        
        // Key Strengths
        if (analysis.keyStrengths.isNotEmpty()) {
            StrengthsSection(
                strengths = analysis.keyStrengths,
                textPrimary = textPrimary,
                textSecondary = textSecondary,
                blueAccent = blueAccent
            )
            HorizontalDivider(color = separator, thickness = 1.dp)
        }
        
        // Areas to Improve
        if (analysis.areasToImprove.isNotEmpty()) {
            AreasToImproveSection(
                areas = analysis.areasToImprove,
                textPrimary = textPrimary,
                textSecondary = textSecondary,
                blueAccent = blueAccent
            )
            HorizontalDivider(color = separator, thickness = 1.dp)
        }
        
        // Recommended Tags
        if (analysis.recommendedTags.isNotEmpty()) {
            RecommendedTagsSection(
                tags = analysis.recommendedTags,
                textPrimary = textPrimary,
                blueAccent = blueAccent
            )
        }
        
        // Re-analyze Button
        Button(
            onClick = onReanalyze,
            enabled = !isLoading,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = blueAccent.copy(alpha = 0.1f),
                contentColor = blueAccent
            )
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    color = blueAccent,
                    strokeWidth = 2.dp
                )
            } else {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Analyser à nouveau",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
        
        // Analysis Date
        analysis.analyzedAt?.let { dateStr ->
            Text(
                text = "Analysé le ${formatDate(dateStr)}",
                fontSize = 12.sp,
                color = textSecondary.copy(alpha = 0.7f),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun ScoreIndicator(
    score: Int,
    textPrimary: Color,
    cardBackground: Color
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Score du Profil",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = textPrimary
            )
            
            Text(
                text = "$score/100",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = scoreColor(score)
            )
        }
        
        // Progress bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(12.dp)
                .background(cardBackground, RoundedCornerShape(8.dp))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(score / 100f)
                    .height(12.dp)
                    .background(scoreColor(score), RoundedCornerShape(8.dp))
            )
        }
    }
}

@Composable
private fun SummarySection(
    summary: String,
    textPrimary: Color,
    textTertiary: Color
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Résumé",
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = textPrimary
        )
        Text(
            text = summary,
            fontSize = 15.sp,
            color = textTertiary,
            lineHeight = 22.sp
        )
    }
}

@Composable
private fun StrengthsSection(
    strengths: List<String>,
    textPrimary: Color,
    textSecondary: Color,
    blueAccent: Color
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = Color(0xFF22C55E), // Green
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = "Points Forts",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = textPrimary
            )
        }
        
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            strengths.forEach { strength ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "•",
                        color = blueAccent,
                        fontSize = 14.sp
                    )
                    Text(
                        text = strength,
                        fontSize = 14.sp,
                        color = textSecondary,
                        lineHeight = 20.sp,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun AreasToImproveSection(
    areas: List<String>,
    textPrimary: Color,
    textSecondary: Color,
    blueAccent: Color
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.TrendingUp,
                contentDescription = null,
                tint = Color(0xFFF97316), // Orange
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = "Axes d'Amélioration",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = textPrimary
            )
        }
        
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            areas.forEach { area ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "•",
                        color = blueAccent,
                        fontSize = 14.sp
                    )
                    Text(
                        text = area,
                        fontSize = 14.sp,
                        color = textSecondary,
                        lineHeight = 20.sp,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun RecommendedTagsSection(
    tags: List<String>,
    textPrimary: Color,
    blueAccent: Color
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Tags Recommandés",
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = textPrimary
        )
        
        // Flow layout for tags
        FlowRowLayout(
            items = tags,
            horizontalSpacing = 8.dp,
            verticalSpacing = 8.dp
        ) { tag ->
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = blueAccent.copy(alpha = 0.1f)
            ) {
                Text(
                    text = tag,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = blueAccent,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }
        }
    }
}

/**
 * Helper function to determine score color based on value
 * Matches iOS logic exactly
 */
private fun scoreColor(score: Int): Color {
    return when {
        score < 40 -> Color(0xFFF44336) // Red
        score < 70 -> Color(0xFFFF9800) // Orange
        score < 85 -> Color(0xFF2196F3) // Blue
        else -> Color(0xFF22C55E) // Green
    }
}

/**
 * Format date string to French locale
 * Matches iOS formatting
 */
private fun formatDate(dateString: String): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
        inputFormat.timeZone = TimeZone.getTimeZone("UTC")
        val date = inputFormat.parse(dateString) ?: return dateString
        
        val outputFormat = SimpleDateFormat("d MMM yyyy 'à' HH:mm", Locale.FRENCH)
        outputFormat.format(date)
    } catch (e: Exception) {
        dateString
    }
}

/**
 * Flow layout for wrapping tags
 * Reused from TalentProfileScreen
 */
@Composable
private fun FlowRowLayout(
    items: List<String>,
    horizontalSpacing: androidx.compose.ui.unit.Dp,
    verticalSpacing: androidx.compose.ui.unit.Dp,
    content: @Composable (String) -> Unit
) {
    BoxWithConstraints {
        val maxWidthPx = with(androidx.compose.ui.platform.LocalDensity.current) { maxWidth.toPx() }
        val density = androidx.compose.ui.platform.LocalDensity.current
        
        val rowsList = mutableListOf<MutableList<String>>()
        var currentRow = mutableListOf<String>()
        var currentRowWidth = 0f
        
        items.forEach { tag ->
            val estimatedWidthDp = (tag.length * 7 + 32).dp
            val estimatedWidthPx = with(density) { estimatedWidthDp.toPx() }
            
            if (currentRowWidth + estimatedWidthPx > maxWidthPx && currentRow.isNotEmpty()) {
                rowsList.add(currentRow.toMutableList())
                currentRow = mutableListOf(tag)
                currentRowWidth = estimatedWidthPx
            } else {
                currentRow.add(tag)
                currentRowWidth += estimatedWidthPx + with(density) { horizontalSpacing.toPx() }
            }
        }
        
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
                    row.forEach { tag ->
                        content(tag)
                    }
                }
            }
        }
    }
}
