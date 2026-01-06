package com.example.matchify.ui.talent.filtering

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.matchify.ui.components.MatchifyTopAppBar

@Composable
fun TalentFilteringScreen(
    missionId: String,
    onBack: () -> Unit,
    onTalentClick: (String) -> Unit = {}
) {
    Scaffold(
        topBar = {
            MatchifyTopAppBar(
                title = "Filtrage des talents",
                onBack = onBack
            )
        },
        containerColor = Color(0xFF0F172A)
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Fonctionnalité en cours de développement",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
