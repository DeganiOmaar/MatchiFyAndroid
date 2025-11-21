package com.example.matchify.ui.contracts

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.ui.platform.LocalContext
import android.graphics.Bitmap
import android.graphics.Canvas as AndroidCanvas
import android.graphics.Paint
import android.graphics.Path as AndroidPath

data class StrokePoint(
    val x: Float,
    val y: Float,
    val isNewStroke: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignaturePadView(
    onSignatureCaptured: (Bitmap) -> Unit,
    onDismiss: () -> Unit
) {
    var paths by remember { mutableStateOf<List<Path>>(emptyList()) }
    var currentPath by remember { mutableStateOf<Path?>(null) }
    var canvasSize by remember { mutableStateOf(IntSize.Zero) }
    var strokePoints by remember { mutableStateOf<List<StrokePoint>>(emptyList()) }
    val isDark = isSystemInDarkTheme()
    
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Signature",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                
                Canvas(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .background(
                            color = MaterialTheme.colorScheme.surface,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .onSizeChanged { size ->
                            canvasSize = size
                        }
                        .pointerInput(Unit) {
                            detectDragGestures(
                                onDragStart = { offset ->
                                    val newPath = Path()
                                    newPath.moveTo(offset.x, offset.y)
                                    currentPath = newPath
                                    strokePoints = strokePoints + StrokePoint(offset.x, offset.y, isNewStroke = true)
                                },
                                onDrag = { change, dragAmount ->
                                    currentPath?.let { path ->
                                        val latest = change.position
                                        
                                        // Simple line drawing - smooth curves will be handled by stroke cap
                                        path.lineTo(latest.x, latest.y)
                                        strokePoints = strokePoints + StrokePoint(latest.x, latest.y, isNewStroke = false)
                                    }
                                },
                                onDragEnd = {
                                    currentPath?.let { path ->
                                        paths = paths + path
                                        currentPath = null
                                    }
                                }
                            )
                        }
                ) {
                    val strokeColor = if (isDark) Color.White else Color.Black
                    
                    paths.forEach { path ->
                        drawPath(
                            path = path,
                            color = strokeColor,
                            style = Stroke(
                                width = 2.dp.toPx(),
                                cap = StrokeCap.Round,
                                join = StrokeJoin.Round
                            )
                        )
                    }
                    
                    currentPath?.let { path ->
                        drawPath(
                            path = path,
                            color = strokeColor,
                            style = Stroke(
                                width = 2.dp.toPx(),
                                cap = StrokeCap.Round,
                                join = StrokeJoin.Round
                            )
                        )
                    }
                }
                
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            paths = emptyList()
                            currentPath = null
                            strokePoints = emptyList()
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Effacer", color = MaterialTheme.colorScheme.error)
                    }
                    
                    Button(
                        onClick = {
                            if (paths.isNotEmpty() || currentPath != null) {
                                captureSignatureAsBitmap(canvasSize, strokePoints, isDark)?.let {
                                    onSignatureCaptured(it)
                                }
                            }
                            onDismiss()
                        },
                        modifier = Modifier.weight(1f),
                        enabled = paths.isNotEmpty() || currentPath != null
                    ) {
                        Text("Terminer")
                    }
                }
            }
        }
    }
}

private fun captureSignatureAsBitmap(
    size: IntSize,
    strokePoints: List<StrokePoint>,
    isDark: Boolean
): Bitmap? {
    if (size.width <= 0 || size.height <= 0 || strokePoints.isEmpty()) return null
    
    val bitmap = Bitmap.createBitmap(
        size.width,
        size.height,
        Bitmap.Config.ARGB_8888
    )
    val canvas = AndroidCanvas(bitmap)
    
    // Clear background to white for signatures
    canvas.drawColor(android.graphics.Color.WHITE)
    
    // Draw paths from points
    val paint = Paint().apply {
        color = android.graphics.Color.BLACK
        style = Paint.Style.STROKE
        strokeWidth = 4f
        strokeCap = Paint.Cap.ROUND
        strokeJoin = Paint.Join.ROUND
        isAntiAlias = true
    }
    
    var currentPath: AndroidPath? = null
    var previousPoint: StrokePoint? = null
    
    strokePoints.forEach { point ->
        if (point.isNewStroke || currentPath == null) {
            // Finish previous path if exists
            currentPath?.let { canvas.drawPath(it, paint) }
            // Start new path
            currentPath = AndroidPath().apply {
                moveTo(point.x, point.y)
            }
            previousPoint = point
        } else {
            // Continue current path
            currentPath?.let { path ->
                previousPoint?.let { prev ->
                    // Use lineTo for simplicity - smooth curves handled by stroke cap
                    path.lineTo(point.x, point.y)
                } ?: run {
                    path.moveTo(point.x, point.y)
                }
            }
            previousPoint = point
        }
    }
    
    // Draw last path
    currentPath?.let { canvas.drawPath(it, paint) }
    
    return bitmap
}
