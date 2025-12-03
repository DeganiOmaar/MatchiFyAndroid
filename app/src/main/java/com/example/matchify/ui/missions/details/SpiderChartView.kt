package com.example.matchify.ui.missions.details

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.matchify.data.remote.dto.ai.RadarDataDto
import kotlin.math.*

/**
 * Spider Chart / Radar Chart View
 * Identique au composant iOS RadarChartView
 */
@Composable
fun SpiderChartView(
    data: RadarDataDto,
    modifier: Modifier = Modifier
) {
    // Animation pour l'apparition du graphique (comme iOS avec easeOut 1.0s)
    val animationProgress by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(
            durationMillis = 1000,
            easing = FastOutSlowInEasing
        ),
        label = "spider_chart_animation"
    )
    
    val axes = listOf(
        "Skills Match",
        "Experience Fit",
        "Project Relevance",
        "Mission Requirements",
        "Soft Skills Fit"
    )
    
    val maxValue = 100f
    
    // Valeurs des données
    val values = remember(data) {
        listOf(
            data.skillsMatch.toFloat(),
            data.experienceFit.toFloat(),
            data.projectRelevance.toFloat(),
            data.missionRequirementsFit.toFloat(),
            data.softSkillsFit.toFloat()
        )
    }
    
    var sizeState by remember { mutableStateOf(IntSize.Zero) }
    val density = LocalDensity.current
    
    Box(
        modifier = modifier
            .onSizeChanged { sizeState = it },
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier.fillMaxSize()
        ) {
            val width = size.width.toFloat()
            val height = size.height.toFloat()
            val centerX = width / 2
            val centerY = height / 2
            val chartRadius = min(width, height) / 2 - with(density) { 40.dp.toPx() }
            
            // Couleurs du design system (identique à iOS)
            val borderColor = Color(0xFF334155).copy(alpha = 0.3f)
            val primaryColor = Color(0xFF3B82F6)
            val textSecondary = Color(0xFF94A3B8)
            
            // Dessiner les cercles de la grille (5 cercles comme iOS)
            for (i in 0..4) {
                val circleRadius = chartRadius * (i + 1) / 5
                drawCircle(
                    color = borderColor,
                    radius = circleRadius,
                    center = Offset(centerX, centerY),
                    style = Stroke(width = 1f)
                )
            }
            
            // Dessiner les lignes d'axes (5 axes comme iOS)
            for (i in 0..4) {
                val angle = (i * 2 * PI / 5) - (PI / 2)
                val endX = centerX + chartRadius * cos(angle).toFloat()
                val endY = centerY + chartRadius * sin(angle).toFloat()
                
                drawLine(
                    color = borderColor,
                    start = Offset(centerX, centerY),
                    end = Offset(endX, endY),
                    strokeWidth = 1f
                )
            }
            
            // Calculer les points du polygone avec animation
            val points = (0..4).map { index ->
                val angle = (index * 2 * PI / 5) - (PI / 2)
                val animatedValue = values[index] * animationProgress
                val normalizedRadius = chartRadius * (animatedValue / maxValue)
                val x = centerX + normalizedRadius * cos(angle).toFloat()
                val y = centerY + normalizedRadius * sin(angle).toFloat()
                Offset(x, y)
            }
            
            // Dessiner le polygone rempli avec opacité (comme iOS)
            val polygonPath = Path().apply {
                moveTo(points[0].x, points[0].y)
                for (i in 1 until points.size) {
                    lineTo(points[i].x, points[i].y)
                }
                close()
            }
            
            // Remplissage avec opacité 0.3 (comme iOS)
            drawPath(
                path = polygonPath,
                color = primaryColor.copy(alpha = 0.3f)
            )
            
            // Contour du polygone (comme iOS stroke width 2)
            drawPath(
                path = polygonPath,
                color = primaryColor,
                style = Stroke(width = 2f)
            )
            
            // Dessiner les points de données (cercles de 8x8 comme iOS)
            points.forEach { point ->
                drawCircle(
                    color = primaryColor,
                    radius = 4f,
                    center = point
                )
            }
            
            // Dessiner les labels des axes
            val labelRadius = chartRadius + with(density) { 25.dp.toPx() }
            axes.forEachIndexed { index, label ->
                val angle = (index * 2 * PI / 5) - (PI / 2)
                val labelX = centerX + labelRadius * cos(angle).toFloat()
                val labelY = centerY + labelRadius * sin(angle).toFloat()
                
                // Dessiner le texte avec Canvas native
                drawIntoCanvas { canvas ->
                    val paint = android.graphics.Paint().apply {
                        color = android.graphics.Color.parseColor("#94A3B8")
                        textSize = with(density) { 11.sp.toPx() }
                        textAlign = android.graphics.Paint.Align.CENTER
                        typeface = android.graphics.Typeface.create(
                            android.graphics.Typeface.DEFAULT,
                            android.graphics.Typeface.NORMAL
                        )
                        textAlign = android.graphics.Paint.Align.CENTER
                        isFakeBoldText = true // Simuler le font weight medium
                        isAntiAlias = true
                    }
                    
                    // Mesurer le texte pour centrer correctement
                    val textWidth = paint.measureText(label)
                    val textHeight = paint.fontMetrics.let { it.descent - it.ascent }
                    
                    canvas.nativeCanvas.drawText(
                        label,
                        labelX,
                        labelY + textHeight / 4, // Centrer verticalement
                        paint
                    )
                }
            }
        }
    }
}