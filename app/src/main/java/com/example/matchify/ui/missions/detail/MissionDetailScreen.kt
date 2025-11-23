package com.example.matchify.ui.missions.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.BusinessCenter
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Work
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.matchify.domain.model.Mission

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MissionDetailScreen(
    mission: Mission,
    onBack: () -> Unit
) {
    val pageBackground = Color(0xFFF5F7FA) // Fond très clair comme ProposalsScreen

    Scaffold(
        containerColor = pageBackground,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Mission Details",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1A1A1A),
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Retour",
                            tint = Color.Black
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(pageBackground)
                .padding(paddingValues)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // Tous les détails dans un seul cadre
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .shadow(
                        elevation = 8.dp,
                        shape = RoundedCornerShape(20.dp),
                        spotColor = Color.Black.copy(alpha = 0.1f)
                    ),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    // Header Section - Titre de la mission et date
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = mission.title,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1A1A1A),
                            lineHeight = 32.sp
                        )
                        Text(
                            text = mission.formattedDate,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    }

                    // Divider
                    HorizontalDivider(
                        color = Color.Gray.copy(alpha = 0.2f),
                        thickness = 1.dp
                    )

                    // Summary Section
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Summary",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1A1A1A)
                        )
                        Text(
                            text = mission.description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFF1A1A1A),
                            lineHeight = 22.sp
                        )
                    }

                    // Divider
                    HorizontalDivider(
                        color = Color.Gray.copy(alpha = 0.2f),
                        thickness = 1.dp
                    )

                    // Budget Section
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Icône dans rectangle avec gradient
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.size(56.dp),
                            color = Color.Transparent
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        brush = Brush.linearGradient(
                                            colors = listOf(
                                                Color(0xFF61A5C2),
                                                Color(0xFF7DB8D6)
                                            )
                                        ),
                                        shape = RoundedCornerShape(16.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.CreditCard,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                        }

                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = "Budget / Price",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.Gray
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = mission.formattedBudget,
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1A1A1A)
                            )
                        }
                    }

                    // Divider
                    HorizontalDivider(
                        color = Color.Gray.copy(alpha = 0.2f),
                        thickness = 1.dp
                    )

                    // Skills Section
                    Column(
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Skills and Expertise",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1A1A1A)
                        )

                        if (mission.skills.isNotEmpty()) {
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                items(mission.skills) { skill ->
                                    Surface(
                                        shape = RoundedCornerShape(20.dp),
                                        color = Color(0xFFF0F0F0),
                                        modifier = Modifier.shadow(
                                            elevation = 2.dp,
                                            shape = RoundedCornerShape(20.dp)
                                        )
                                    ) {
                                        Text(
                                            text = skill,
                                            modifier = Modifier.padding(
                                                horizontal = 16.dp,
                                                vertical = 8.dp
                                            ),
                                            style = MaterialTheme.typography.bodySmall,
                                            fontWeight = FontWeight.Medium,
                                            color = Color(0xFF1A1A1A)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Divider
                    HorizontalDivider(
                        color = Color.Gray.copy(alpha = 0.2f),
                        thickness = 1.dp
                    )

                    // Activity Section avec badges circulaires
                    Column(
                        verticalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        Text(
                            text = "Activity on this mission",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1A1A1A)
                        )
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            // Proposals avec badge circulaire
                            ActivityCircleItem(
                                icon = Icons.Filled.People,
                                count = mission.proposalsCount ?: 0,
                                label = "Proposals"
                            )
                            
                            // Interviewing avec badge circulaire
                            ActivityCircleItem(
                                icon = Icons.Filled.BusinessCenter,
                                count = mission.interviewingCount ?: 0,
                                label = "Interviewing"
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun ActivityCircleItem(
    icon: ImageVector,
    count: Int,
    label: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Cercle bleu clair avec icône bleue foncée
        Surface(
            shape = CircleShape,
            color = Color(0xFFE3F2FD), // Bleu clair
            modifier = Modifier
                .size(80.dp)
                .shadow(
                    elevation = 4.dp,
                    shape = CircleShape,
                    spotColor = Color.Black.copy(alpha = 0.1f)
                )
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color(0xFF61A5C2), // Bleu foncé pour l'icône
                    modifier = Modifier.size(36.dp)
                )
            }
        }
        
        // Nombre en bleu foncé et gras
        Text(
            text = "$count",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF61A5C2)
        )
        
        // Label en gris
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray
        )
    }
}




