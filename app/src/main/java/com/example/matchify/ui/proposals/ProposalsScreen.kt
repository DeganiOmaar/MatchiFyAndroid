package com.example.matchify.ui.proposals

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.matchify.R
import com.example.matchify.domain.model.Proposal
import java.time.Instant
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProposalsScreen(
    onProposalClick: (String) -> Unit = {},
    viewModel: ProposalsViewModel = viewModel(factory = ProposalsViewModelFactory())
) {
    val proposals by viewModel.proposals.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val selectedStatusFilter by viewModel.selectedStatusFilter.collectAsState()
    val selectedTab by viewModel.selectedTab.collectAsState()
    
    // Recruiter-specific state
    val missions by viewModel.missions.collectAsState()
    val selectedMission by viewModel.selectedMission.collectAsState()
    val aiSortEnabled by viewModel.aiSortEnabled.collectAsState()
    val isLoadingMissions by viewModel.isLoadingMissions.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Proposals") }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Mission Selector & AI Sort (Recruiter only)
            if (viewModel.isRecruiter) {
                RecruiterMissionSelector(
                    missions = missions,
                    selectedMission = selectedMission,
                    isLoadingMissions = isLoadingMissions,
                    onMissionSelected = { viewModel.selectMission(it) },
                    aiSortEnabled = aiSortEnabled,
                    onToggleAiSort = { viewModel.toggleAiSort() },
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                )
            }
            
            // Active/Archive tabs (Talent only)
            if (!viewModel.isRecruiter) {
                ProposalTabSelector(
                    selectedTab = selectedTab,
                    onTabSelected = { tab ->
                        viewModel.selectTab(tab)
                    },
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                )
                
                // Status filter chips (Talent only, Active tab only)
                if (selectedTab == ProposalTab.ACTIVE) {
                    ProposalStatusFilters(
                        selectedFilter = selectedStatusFilter,
                        onFilterSelected = { filter ->
                            viewModel.selectStatusFilter(filter)
                        },
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                    )
                }
            }
            
            when {
                isLoading && proposals.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                errorMessage != null -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = errorMessage ?: "Une erreur est survenue",
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
                viewModel.isRecruiter && selectedMission == null -> {
                    // Show placeholder when no mission selected (recruiter)
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Description,
                                contentDescription = null,
                                modifier = Modifier.size(60.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                            )
                            Text(
                                text = "Select a mission to view proposals",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "Choose a mission from the dropdown above",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
                proposals.isEmpty() -> {
                    EmptyProposalsView(viewModel.isRecruiter)
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(proposals) { proposal ->
                            ProposalRow(
                                proposal = proposal,
                                isRecruiter = viewModel.isRecruiter,
                                showAiScore = aiSortEnabled && viewModel.isRecruiter,
                                onClick = { onProposalClick(proposal.proposalId) },
                                onArchive = { viewModel.archiveProposal(proposal.proposalId) },
                                onDelete = { viewModel.deleteProposal(proposal.proposalId) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ProposalTabSelector(
    selectedTab: ProposalTab,
    onTabSelected: (ProposalTab) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            ProposalTab.values().forEach { tab ->
                val isSelected = selectedTab == tab
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onTabSelected(tab) }
                        .then(
                            if (isSelected) {
                                Modifier.background(
                                    color = MaterialTheme.colorScheme.primary,
                                    shape = RoundedCornerShape(12.dp)
                                )
                            } else {
                                Modifier
                            }
                        )
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = tab.displayName,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = if (isSelected) {
                            FontWeight.SemiBold
                        } else {
                            FontWeight.Medium
                        },
                        color = if (isSelected) {
                            MaterialTheme.colorScheme.onPrimary
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun ProposalStatusFilters(
    selectedFilter: ProposalStatusFilter,
    onFilterSelected: (ProposalStatusFilter) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(scrollState),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        ProposalStatusFilter.values().forEach { filter ->
            FilterChip(
                selected = selectedFilter == filter,
                onClick = { onFilterSelected(filter) },
                label = {
                    Text(
                        text = filter.displayName,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = if (selectedFilter == filter) {
                            FontWeight.SemiBold
                        } else {
                            FontWeight.Medium
                        }
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
    }
}

@Composable
private fun EmptyProposalsView(isRecruiter: Boolean) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Description,
                contentDescription = null,
                modifier = Modifier.size(60.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
            )
            Text(
                text = "No Proposals",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = if (isRecruiter) {
                    "You have not received any proposals yet."
                } else {
                    "You have not applied to any missions yet."
                },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RecruiterMissionSelector(
    missions: List<com.example.matchify.domain.model.Mission>,
    selectedMission: com.example.matchify.domain.model.Mission?,
    isLoadingMissions: Boolean,
    onMissionSelected: (com.example.matchify.domain.model.Mission?) -> Unit,
    aiSortEnabled: Boolean,
    onToggleAiSort: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Mission Dropdown
        var expanded by remember { mutableStateOf(false) }
        
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = selectedMission?.title ?: "Select a mission",
                onValueChange = {},
                readOnly = true,
                label = { Text("Mission") },
                leadingIcon = {
                    Icon(Icons.Default.Work, contentDescription = null)
                },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
                enabled = !isLoadingMissions
            )
            
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                missions.forEach { mission ->
                    DropdownMenuItem(
                        text = { Text(mission.title) },
                        onClick = {
                            onMissionSelected(mission)
                            expanded = false
                        }
                    )
                }
            }
        }
        
        // AI Sort Toggle (only shown when mission is selected)
        if (selectedMission != null) {
            Button(
                onClick = onToggleAiSort,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (aiSortEnabled) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.primaryContainer
                    }
                )
            ) {
                Icon(
                    Icons.Default.AutoAwesome,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (aiSortEnabled) "AI Sorting Enabled" else "Enable AI Sorting",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun ProposalRow(
    proposal: Proposal,
    isRecruiter: Boolean,
    showAiScore: Boolean = false,
    onClick: () -> Unit,
    onArchive: () -> Unit = {},
    onDelete: () -> Unit = {}
) {
    var showMenu by remember { mutableStateOf(false) }
    
    // Only show menu for Talent
    val showContextMenu = !isRecruiter
    // Determine if proposal is "unread" (not viewed) - matching alerts logic
    val isUnread = proposal.status == com.example.matchify.domain.model.ProposalStatus.NOT_VIEWED
    
    // Get profile image URL - for recruiter view, show talent profile; for talent view, show recruiter profile
    val profileImageUrl = if (isRecruiter) {
        // Recruiter viewing proposals: show talent profile image
        // Note: Proposal model doesn't have profileImage fields, so we'll use a placeholder
        // In a real implementation, you'd need to fetch this from the user profile
        null
    } else {
        // Talent viewing proposals: show recruiter profile image
        null
    }
    
    Box {
        Card(
            onClick = onClick,
            modifier = Modifier
                .fillMaxWidth()
                .then(
                    if (showContextMenu) {
                        Modifier.pointerInput(Unit) {
                            detectTapGestures(
                                onLongPress = {
                                    showMenu = true
                                }
                            )
                        }
                    } else {
                        Modifier
                    }
                ),
            colors = CardDefaults.cardColors(
                containerColor = if (!isUnread) {
                    MaterialTheme.colorScheme.surface
                } else {
                    MaterialTheme.colorScheme.surface.copy(alpha = 0.7f)
                }
            ),
            border = if (isUnread) {
                androidx.compose.foundation.BorderStroke(
                    1.dp,
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                )
            } else null
        ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Profile Image - matching alerts design exactly
            ProfileImage(
                imageUrl = profileImageUrl,
                modifier = Modifier.size(50.dp),
                isUnread = isUnread
            )
            
            // Content - matching alerts design exactly
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = proposal.missionTitle ?: "Mission",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = if (!isUnread) FontWeight.Normal else FontWeight.SemiBold,
                        maxLines = 2,
                        modifier = Modifier.weight(1f)
                    )
                    
                    // Status Badge - matching alerts badge style exactly
                    StatusBadge(status = proposal.status)
                    
                    // Unread indicator - matching alerts exactly
                    if (isUnread) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            color = MaterialTheme.colorScheme.primary,
                            shape = CircleShape,
                            modifier = Modifier.size(8.dp)
                        ) {}
                    }
                }
                
                // Show talent/recruiter name - matching alerts message style
                val userName = if (isRecruiter) {
                    proposal.talentName
                } else {
                    proposal.recruiterName
                }
                if (!userName.isNullOrEmpty()) {
                    Text(
                        text = if (isRecruiter) "By $userName" else "From $userName",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1
                    )
                }
                
                // AI Score Badge (shown when AI sorting is enabled and score is available)
                if (showAiScore) {
                    // Note: Le backend devrait retourner le score AI dans ProposalDto
                    // Pour l'instant, on affiche juste un indicateur visuel
                    Row(
                        modifier = Modifier.padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            Icons.Default.AutoAwesome,
                            contentDescription = null,
                            modifier = Modifier.size(12.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "AI Match",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                
                Text(
                    text = proposal.message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2
                )
                
                Text(
                    text = formatDate(proposal.createdAt),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            }
        }
        }
        
        // Context menu for Talent only
        if (showContextMenu) {
            DropdownMenu(
                expanded = showMenu,
                onDismissRequest = { showMenu = false }
            ) {
                DropdownMenuItem(
                    text = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Archive,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Text("Archive")
                        }
                    },
                    onClick = {
                        showMenu = false
                        onArchive()
                    }
                )
                DropdownMenuItem(
                    text = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Delete,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp),
                                tint = MaterialTheme.colorScheme.error
                            )
                            Text(
                                "Delete",
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    },
                    onClick = {
                        showMenu = false
                        onDelete()
                    }
                )
            }
        }
    }
}

@Composable
private fun ProfileImage(
    imageUrl: String?,
    modifier: Modifier = Modifier,
    isUnread: Boolean = false
) {
    Box(
        modifier = modifier
            .clip(CircleShape)
            .then(
                if (isUnread) {
                    Modifier.border(
                        2.dp,
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                        CircleShape
                    )
                } else {
                    Modifier
                }
            )
    ) {
        AsyncImage(
            model = imageUrl ?: "",
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .clip(CircleShape),
            contentScale = ContentScale.Crop,
            error = painterResource(id = R.drawable.avatar),
            placeholder = painterResource(id = R.drawable.avatar)
        )
    }
}

@Composable
private fun StatusBadge(
    status: com.example.matchify.domain.model.ProposalStatus
) {
    val (text, color) = when (status) {
        com.example.matchify.domain.model.ProposalStatus.NOT_VIEWED -> "Not viewed" to Color(0xFFFF9800)
        com.example.matchify.domain.model.ProposalStatus.VIEWED -> "Viewed" to Color(0xFF2196F3)
        com.example.matchify.domain.model.ProposalStatus.ACCEPTED -> "Accepted" to Color(0xFF4CAF50)
        com.example.matchify.domain.model.ProposalStatus.REFUSED -> "Refused" to Color(0xFFF44336)
    }
    
    Surface(
        color = color.copy(alpha = 0.15f),
        shape = MaterialTheme.shapes.small
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = color,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
private fun formatDate(dateString: String?): String {
    if (dateString == null) return ""
    
    return try {
        val formatter = DateTimeFormatter.ISO_DATE_TIME
        val date = Instant.from(formatter.parse(dateString))
        val now = Instant.now()
        val diff = ChronoUnit.MINUTES.between(date, now)
        
        when {
            diff < 1 -> "Just now"
            diff < 60 -> "${diff}m ago"
            diff < 1440 -> {
                val hours = diff / 60
                "${hours}h ago"
            }
            diff < 10080 -> {
                val days = diff / 1440
                if (days == 1L) "Yesterday" else "$days days ago"
            }
            else -> {
                val dateTime = date.atZone(java.time.ZoneId.systemDefault())
                DateTimeFormatter.ofPattern("MMM d, yyyy").format(dateTime)
            }
        }
    } catch (e: Exception) {
        ""
    }
}
