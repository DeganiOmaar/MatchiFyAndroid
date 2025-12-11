package com.example.matchify.ui.stats

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.runtime.Composable
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
import com.example.matchify.domain.model.Stats

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyStatsScreen(
    viewModel: MyStatsViewModel = viewModel(factory = MyStatsViewModelFactory())
) {
    val stats by viewModel.stats.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val selectedTimeframe by viewModel.selectedTimeframe.collectAsState()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F172A))
    ) {
        // Custom Header
        StatsHeader()

        Box(modifier = Modifier.weight(1f)) {
            if (isLoading && stats == null) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = Color(0xFF3B82F6)
                )
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
                    
                    HorizontalDivider(color = Color(0xFF374151))
                    
                    // Job Success Score Section
                    JobSuccessScoreSection(
                        scoreText = viewModel.jobSuccessScoreText,
                        hasScore = viewModel.hasJobSuccessScore
                    )
                    
                    HorizontalDivider(color = Color(0xFF374151))
                    
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
private fun StatsHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .background(Color(0xFF1E293B))
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "My stats",
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
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
            text = "Overview",
            color = Color.White,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
        
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "View proposal history, earnings, profile analytics, and your Job Success Score.",
                color = Color.White,
                fontSize = 16.sp
            )
            
            Text(
                text = "Stats are not updated in real-time and may take up to 24 hours to reflect recent activity.",
                color = Color(0xFF9CA3AF),
                fontSize = 12.sp
            )
        }
        
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            color = Color(0xFF1E293B),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF374151))
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "12-month earnings",
                    color = Color(0xFF9CA3AF),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
                
                Text(
                    text = formattedEarnings,
                    color = Color.White,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold
                )
            }
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
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold
            )
            
            IconButton(
                onClick = { /* TODO: Show info */ },
                modifier = Modifier.size(20.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "Info",
                    modifier = Modifier.size(16.dp),
                    tint = Color(0xFF9CA3AF)
                )
            }
        }
        
        Text(
            text = "Leverage Job Success insights to help you learn how to earn or regain a score.",
            color = Color(0xFF9CA3AF),
            fontSize = 14.sp
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
                        strokeWidth = 8.dp,
                        color = Color(0xFF3B82F6), // Blue
                        trackColor = Color(0xFF1E293B)
                    )
                    
                    Text(
                        text = scoreText,
                        color = Color.White,
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = if (hasScore) "Current Score" else "No score yet",
                        color = Color(0xFF9CA3AF),
                        fontSize = 12.sp
                    )
                    
                    if (!hasScore) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            modifier = Modifier.size(12.dp),
                            tint = Color(0xFF9CA3AF)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ProposalsSection(
    stats: Stats?,
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
    
    fun calculateBarHeight(value: Int, maxValue: Int): Dp {
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
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold
            )
            
            // Dropdown Selector
            var showDropdown by remember { mutableStateOf(false) }
            
            Box {
                Surface(
                    onClick = { showDropdown = !showDropdown },
                    color = Color(0xFF1E293B),
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF374151))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
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
                            color = Color.White,
                            fontSize = 14.sp
                        )
                        
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = null,
                            tint = Color(0xFF9CA3AF),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
                
                DropdownMenu(
                    expanded = showDropdown,
                    onDismissRequest = { showDropdown = false },
                    modifier = Modifier
                        .background(Color(0xFF1E293B))
                        .border(androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF374151)), RoundedCornerShape(4.dp))
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
                                        color = if (isSelected) Color(0xFF3B82F6) else Color.White,
                                        fontSize = 14.sp
                                    )
                                    
                                    if (isSelected) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = "Selected",
                                            tint = Color(0xFF3B82F6),
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            },
                            onClick = {
                                onTimeframeSelected(timeframe)
                                showDropdown = false
                            },
                            colors = MenuDefaults.itemColors(
                                textColor = Color.White,
                                leadingIconColor = Color.White,
                                trailingIconColor = Color.White
                            )
                        )
                    }
                }
            }
        }
        
        // Proposals Count
        Text(
            text = proposalsSentText,
            color = Color.White,
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold
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
                                    Color(0xFF1E293B),
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
                                    Color(0xFF1E293B),
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
                                        Color(0xFF10B981), // Accepted color (green)
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
                                    Color(0xFF1E293B),
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
                                        Color(0xFFEF4444), // Refused color (red)
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
                        color = Color.White,
                        fontSize = 14.sp
                    )
                }
                
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .background(Color(0xFF10B981), RoundedCornerShape(2.dp))
                    )
                    Text(
                        text = "${stats?.proposalsAccepted ?: 0} proposals accepted",
                        color = Color.White,
                        fontSize = 14.sp
                    )
                }
                
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .background(Color(0xFFEF4444), RoundedCornerShape(2.dp))
                    )
                    Text(
                        text = "${stats?.proposalsRefused ?: 0} proposals refused",
                        color = Color.White,
                        fontSize = 14.sp
                    )
                }
            }
        }
        
    }
}

