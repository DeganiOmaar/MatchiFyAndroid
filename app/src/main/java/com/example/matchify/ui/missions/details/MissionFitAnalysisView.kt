package com.example.matchify.ui.missions.details

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.draw
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.matchify.data.remote.dto.ai.MissionFitResponseDto
import kotlin.math.cos
import kotlin.math.sin

/**
 * Vue pour l'analyse de compatibilité mission-profil avec spider chart
 * Même comportement que iOS
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MissionFitAnalysisView(
    missionId: String,
    onDismiss: () -> Unit,
    viewModel: MissionFitAnalysisViewModel = viewModel(
        factory = MissionFitAnalysisViewModelFactory(missionId)
    )
) {
    val fitAnalysis by viewModel.fitAnalysis.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Analyse de Compatibilité",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close")
                }
            }
            
            when {
                isLoading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(400.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                errorMessage != null -> {
                    Text(
                        text = errorMessage ?: "Erreur",
                        color = MaterialTheme.colorScheme.error
                    )
                }
                fitAnalysis != null -> {
                    FitAnalysisContent(analysis = fitAnalysis!!)
                }
            }
        }
    }
}

@Composable
private fun FitAnalysisContent(analysis: MissionFitResponseDto) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Global Score
        ScoreCard(score = analysis.score)
        
        HorizontalDivider()
        
        // Short Summary
        Text(
            text = analysis.shortSummary,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        HorizontalDivider()
        
        // Spider Chart
        Text(
            text = "Détails de Compatibilité",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
        
        SpiderChart(radarData = analysis.radar)
    }
}

@Composable
private fun ScoreCard(score: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = scoreColor(score).copy(alpha = 0.1f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Score Global",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "$score/100",
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.Bold,
                color = scoreColor(score)
            )
        }
    }
}

@Composable
private fun SpiderChart(radarData: com.example.matchify.data.remote.dto.ai.RadarDataDto) {
    val dimensions = listOf(
        "Compétences" to radarData.skillsMatch,
        "Expérience" to radarData.experienceFit,
        "Pertinence Projet" to radarData.projectRelevance,
        "Exigences Mission" to radarData.missionRequirementsFit,
        "Soft Skills" to radarData.softSkillsFit
    )
    
    // Get colors from MaterialTheme in Composable context
    val outlineColor = MaterialTheme.colorScheme.outline
    val primaryColor = MaterialTheme.colorScheme.primary
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(400.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val centerX = size.width / 2
            val centerY = size.height / 2
            val radius = size.minDimension / 2 * 0.8f
            
            // Draw grid circles
            for (i in 1..5) {
                val gridRadius = radius * (i / 5f)
                drawCircle(
                    color = outlineColor.copy(alpha = 0.2f),
                    radius = gridRadius,
                    center = Offset(centerX, centerY),
                    style = Stroke(width = 1.dp.toPx())
                )
            }
            
            // Draw axes
            val numDimensions = dimensions.size
            for (i in 0 until numDimensions) {
                val angle = (2 * Math.PI * i / numDimensions) - (Math.PI / 2)
                val endX = centerX + radius * cos(angle).toFloat()
                val endY = centerY + radius * sin(angle).toFloat()
                
                drawLine(
                    color = outlineColor.copy(alpha = 0.3f),
                    start = Offset(centerX, centerY),
                    end = Offset(endX, endY),
                    strokeWidth = 1.dp.toPx()
                )
            }
            
            // Draw data polygon
            val path = Path()
            dimensions.forEachIndexed { index, (_, value) ->
                val angle = (2 * Math.PI * index / numDimensions) - (Math.PI / 2)
                val normalizedValue = value / 100f
                val x = centerX + radius * normalizedValue * cos(angle).toFloat()
                val y = centerY + radius * normalizedValue * sin(angle).toFloat()
                
                if (index == 0) {
                    path.moveTo(x, y)
                } else {
                    path.lineTo(x, y)
                }
            }
            path.close()
            
            // Fill polygon
            drawPath(
                path = path,
                color = primaryColor.copy(alpha = 0.3f)
            )
            
            // Draw polygon border
            drawPath(
                path = path,
                color = scoreColorNonComposable(getAverageScore(dimensions)),
                style = Stroke(width = 2.dp.toPx())
            )
            
            // Draw points
            dimensions.forEachIndexed { index, (_, value) ->
                val angle = (2 * Math.PI * index / numDimensions) - (Math.PI / 2)
                val normalizedValue = value / 100f
                val x = centerX + radius * normalizedValue * cos(angle).toFloat()
                val y = centerY + radius * normalizedValue * sin(angle).toFloat()
                
                drawCircle(
                    color = scoreColorNonComposable(value),
                    radius = 6.dp.toPx(),
                    center = Offset(x, y)
                )
            }
        }
        
        // Labels below the chart
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 320.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            dimensions.forEach { (label, value) ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "$value%",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        color = scoreColor(value)
                    )
                }
            }
        }
    }
}

@Composable
private fun scoreColor(score: Int): Color {
    return when {
        score >= 80 -> Color(0xFF4CAF50) // Green
        score >= 50 -> Color(0xFFFF9800) // Orange
        else -> Color(0xFFF44336) // Red
    }
}

private fun scoreColorNonComposable(score: Int): Color {
    return when {
        score >= 80 -> Color(0xFF4CAF50) // Green
        score >= 50 -> Color(0xFFFF9800) // Orange
        else -> Color(0xFFF44336) // Red
    }
}

private fun getAverageScore(dimensions: List<Pair<String, Int>>): Int {
    return dimensions.map { it.second }.average().toInt()
}

