package com.example.matchify.ui.stats

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyStatsScreen(
    viewModel: MyStatsViewModel = viewModel(factory = MyStatsViewModelFactory())
) {
    val stats by viewModel.stats.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val selectedTimeframe by viewModel.selectedTimeframe.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "My stats",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            if (isLoading && stats == null) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    // Overview Section
                    OverviewSection(
                        formattedEarnings = viewModel.formattedEarnings
                    )
                    
                    Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                    
                    // Job Success Score Section
                    JobSuccessScoreSection(
                        scoreText = viewModel.jobSuccessScoreText,
                        hasScore = viewModel.hasJobSuccessScore
                    )
                    
                    Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                    
                    // Proposals Section
                    ProposalsSection(
                        stats = stats,
                        selectedTimeframe = selectedTimeframe,
                        onTimeframeSelected = { viewModel.selectTimeframe(it) },
                        proposalsSentText = viewModel.proposalsSentText
                    )
                }
            }
        }
    }
}

@Composable
private fun OverviewSection(
    formattedEarnings: String
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Text(
            text = "My stats",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "View proposal history, earnings, profile analytics, and your Job Success Score.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Text(
                text = "Stats are not updated in real-time and may take up to 24 hours to reflect recent activity.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "12-month earnings",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Text(
                text = formattedEarnings,
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun JobSuccessScoreSection(
    scoreText: String,
    hasScore: Boolean
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Job Success Score",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            IconButton(
                onClick = { /* TODO: Show info */ },
                modifier = Modifier.size(20.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "Info",
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        
        Text(
            text = "Leverage Job Success insights to help you learn how to earn or regain a score.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        // Score Circle
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.size(120.dp)
                ) {
                    CircularProgressIndicator(
                        progress = { if (hasScore && scoreText != "–") scoreText.toIntOrNull()?.div(100f) ?: 0f else 0f },
                        modifier = Modifier.size(120.dp),
                        strokeWidth = 3.dp,
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                    )
                    
                    Text(
                        text = scoreText,
                        style = MaterialTheme.typography.displayMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "No score",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        modifier = Modifier.size(12.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun ProposalsSection(
    stats: com.example.matchify.domain.model.Stats?,
    selectedTimeframe: StatsTimeframe,
    onTimeframeSelected: (StatsTimeframe) -> Unit,
    proposalsSentText: String
) {
    val maxProposalValue = maxOf(
        1,
        maxOf(
            stats?.proposalsSent ?: 0,
            maxOf(
                stats?.proposalsAccepted ?: 0,
                stats?.proposalsRefused ?: 0
            )
        )
    )
    
    fun calculateBarHeight(value: Int, maxValue: Int): androidx.compose.ui.unit.Dp {
        if (maxValue == 0) return 4.dp
        val ratio = value.toFloat() / maxValue.toFloat()
        return maxOf(4.dp, (ratio * 80).dp)
    }
    Column(
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Title Row with Dropdown
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Proposals",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            // Dropdown Selector with Material 3 style
            var showDropdown by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(false) }
            
            Box {
                TextButton(
                    onClick = { showDropdown = !showDropdown },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.onSurface
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .background(
                            MaterialTheme.colorScheme.surfaceVariant,
                            RoundedCornerShape(8.dp)
                        )
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = when (selectedTimeframe) {
                                StatsTimeframe.LAST_7_DAYS -> "Last 7 days"
                                StatsTimeframe.LAST_30_DAYS -> "Last 30 days"
                                StatsTimeframe.LAST_90_DAYS -> "Last 90 days"
                                StatsTimeframe.LAST_12_MONTHS -> "Last 12 months"
                            },
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
                
                DropdownMenu(
                    expanded = showDropdown,
                    onDismissRequest = { showDropdown = false },
                    modifier = Modifier
                        .background(
                            MaterialTheme.colorScheme.surface,
                            RoundedCornerShape(12.dp)
                        )
                ) {
                    StatsTimeframe.entries.forEach { timeframe ->
                        val isSelected = timeframe == selectedTimeframe
                        DropdownMenuItem(
                            text = {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = when (timeframe) {
                                            StatsTimeframe.LAST_7_DAYS -> "Last 7 days"
                                            StatsTimeframe.LAST_30_DAYS -> "Last 30 days"
                                            StatsTimeframe.LAST_90_DAYS -> "Last 90 days"
                                            StatsTimeframe.LAST_12_MONTHS -> "Last 12 months"
                                        },
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    
                                    if (isSelected) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = "Selected",
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                            },
                            onClick = {
                                onTimeframeSelected(timeframe)
                                showDropdown = false
                            }
                        )
                    }
                }
            }
        }
        
        // Proposals Count
        Text(
            text = proposalsSentText,
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        
        // Graph and Stats Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Graph Placeholder (Simple bar chart with 3 bars)
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.Bottom
                ) {
                    // Bar 1 - Sent
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .width(20.dp)
                                .height(80.dp)
                                .background(
                                    MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                                    RoundedCornerShape(4.dp)
                                ),
                            contentAlignment = Alignment.BottomCenter
                        ) {
                            Box(
                                modifier = Modifier
                                    .width(20.dp)
                                    .height(calculateBarHeight(
                                        value = stats?.proposalsSent ?: 0,
                                        maxValue = maxProposalValue
                                    ))
                                    .background(
                                        Color(0xFF66CCCC), // Sent color (light turquoise)
                                        RoundedCornerShape(4.dp)
                                    )
                            )
                        }
                    }
                    
                    // Bar 2 - Accepted
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .width(20.dp)
                                .height(80.dp)
                                .background(
                                    MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                                    RoundedCornerShape(4.dp)
                                ),
                            contentAlignment = Alignment.BottomCenter
                        ) {
                            Box(
                                modifier = Modifier
                                    .width(20.dp)
                                    .height(calculateBarHeight(
                                        value = stats?.proposalsAccepted ?: 0,
                                        maxValue = maxProposalValue
                                    ))
                                    .background(
                                        Color(0xFF33B84D), // Accepted color (green)
                                        RoundedCornerShape(4.dp)
                                    )
                            )
                        }
                    }
                    
                    // Bar 3 - Refused
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .width(20.dp)
                                .height(80.dp)
                                .background(
                                    MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                                    RoundedCornerShape(4.dp)
                                ),
                            contentAlignment = Alignment.BottomCenter
                        ) {
                            Box(
                                modifier = Modifier
                                    .width(20.dp)
                                    .height(calculateBarHeight(
                                        value = stats?.proposalsRefused ?: 0,
                                        maxValue = maxProposalValue
                                    ))
                                    .background(
                                        Color(0xFFE64A4A), // Refused color (red)
                                        RoundedCornerShape(4.dp)
                                    )
                            )
                        }
                    }
                }
            }
            
            // Stats List
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .background(Color(0xFF66CCCC), RoundedCornerShape(2.dp))
                    )
                    Text(
                        text = "${stats?.proposalsSent ?: 0} proposals sent",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .background(Color(0xFF33B84D), RoundedCornerShape(2.dp))
                    )
                    Text(
                        text = "${stats?.proposalsAccepted ?: 0} proposals accepted",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .background(Color(0xFFE64A4A), RoundedCornerShape(2.dp))
                    )
                    Text(
                        text = "${stats?.proposalsRefused ?: 0} proposals refused",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
        
    }
}

