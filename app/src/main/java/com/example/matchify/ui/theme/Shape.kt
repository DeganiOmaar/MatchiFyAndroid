package com.example.matchify.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

/**
 * Material Design 3 Shape System
 * Follows MD3 shape specifications
 */
val Shapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),    // 4dp
    small = RoundedCornerShape(8.dp),         // 8dp
    medium = RoundedCornerShape(12.dp),       // 12dp (4-8dp for text fields as per MD3)
    large = RoundedCornerShape(16.dp),        // 16dp
    extraLarge = RoundedCornerShape(28.dp)    // 28dp
)

