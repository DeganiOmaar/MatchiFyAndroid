package com.example.matchify.ui.proposals

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.matchify.R
import com.example.matchify.data.local.AuthPreferencesProvider
import com.example.matchify.domain.model.Proposal
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProposalsScreen(
    onProposalClick: (String) -> Unit = {},
    onDrawerItemSelected: (com.example.matchify.ui.missions.components.DrawerMenuItemType) -> Unit = {},
    viewModel: ProposalsViewModel = viewModel(factory = ProposalsViewModelFactory())
) {
    val proposals by viewModel.proposals.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val selectedStatusFilter by viewModel.selectedStatusFilter.collectAsState()
    val selectedTab by viewModel.selectedTab.collectAsState()
    
    // Recruiter-specific state (keep existing logic)
    val missions by viewModel.missions.collectAsState()
    val selectedMission by viewModel.selectedMission.collectAsState()
    val aiSortEnabled by viewModel.aiSortEnabled.collectAsState()
    val isLoadingMissions by viewModel.isLoadingMissions.collectAsState()
    
    // Only show new UI for Talent
    if (viewModel.isRecruiter) {
        // Keep existing recruiter UI (not changing)
        RecruiterProposalsScreen(
            proposals = proposals,
            isLoading = isLoading,
            errorMessage = errorMessage,
            missions = missions,
            selectedMission = selectedMission,
            isLoadingMissions = isLoadingMissions,
            aiSortEnabled = aiSortEnabled,
            onMissionSelected = { viewModel.selectMission(it) },
            onToggleAiSort = { viewModel.toggleAiSort() },
            onProposalClick = onProposalClick,
            onDrawerItemSelected = onDrawerItemSelected,
            viewModel = viewModel
        )
    } else {
        // New Talent UI - completely rebuilt
        TalentProposalsScreen(
            proposals = proposals,
            isLoading = isLoading,
            errorMessage = errorMessage,
            selectedStatusFilter = selectedStatusFilter,
            selectedTab = selectedTab,
            onTabSelected = { viewModel.selectTab(it) },
            onFilterSelected = { viewModel.selectStatusFilter(it) },
            onProposalClick = onProposalClick,
            onDrawerItemSelected = onDrawerItemSelected
        )
    }
}

@Composable
private fun TalentProposalsScreen(
    proposals: List<Proposal>,
    isLoading: Boolean,
    errorMessage: String?,
    selectedStatusFilter: ProposalStatusFilter,
    selectedTab: ProposalTab,
    onTabSelected: (ProposalTab) -> Unit,
    onFilterSelected: (ProposalStatusFilter) -> Unit,
    onProposalClick: (String) -> Unit,
    onDrawerItemSelected: (com.example.matchify.ui.missions.components.DrawerMenuItemType) -> Unit
) {
    // Get user for avatar
    val context = androidx.compose.ui.platform.LocalContext.current
    val prefs = remember { com.example.matchify.data.local.AuthPreferencesProvider.getInstance().get() }
    val user by prefs.user.collectAsState(initial = null)
    
    // Drawer state
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    
    // Navigation Drawer wraps the entire Scaffold
    com.example.matchify.ui.missions.components.NewDrawerContent(
        drawerState = drawerState,
        currentRoute = null,
        onClose = {
            scope.launch {
                drawerState.close()
            }
        },
        onMenuItemSelected = { itemType ->
            scope.launch {
                drawerState.close()
            }
            onDrawerItemSelected(itemType)
        }
    ) {
        Scaffold(
            topBar = {
                com.example.matchify.ui.components.CustomAppBar(
                    title = "Proposals",
                    profileImageUrl = user?.profileImageUrl,
                    onProfileClick = {
                        scope.launch {
                            drawerState.open()
                        }
                    }
                )
            },
            containerColor = Color(0xFF0F172A) // Dark navy background
        ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF0F172A))
                .padding(paddingValues)
        ) {
            // Top Tabs (Active / Archive) - directly under header
            ProposalTabsRow(
                selectedTab = selectedTab,
                onTabSelected = onTabSelected,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            
            // Divider under tabs - #1E293B, 1dp
            HorizontalDivider(
                color = Color(0xFF1E293B),
                thickness = 1.dp
            )
            
            // Filter Pills Row (only for Active tab) - immediately below tabs
            if (selectedTab == ProposalTab.ACTIVE) {
                ProposalFilterPills(
                    selectedFilter = selectedStatusFilter,
                    onFilterSelected = onFilterSelected,
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .padding(top = 12.dp) // 12dp vertical spacing from tabs
                )
            }
            
            // Proposals List
            when {
                isLoading && proposals.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = Color(0xFF3B82F6)
                        )
                    }
                }
                errorMessage != null -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = errorMessage ?: "Une erreur est survenue",
                            color = Color(0xFFFF6B6B)
                        )
                    }
                }
                proposals.isEmpty() -> {
                    EmptyProposalsView()
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(
                            start = 16.dp,
                            top = 14.dp, // 12-16dp top padding after pills
                            end = 16.dp,
                            bottom = 80.dp // Extra bottom padding to avoid overlap with bottom navigation
                        ),
                        verticalArrangement = Arrangement.spacedBy(14.dp) // 12-16dp between cards
                    ) {
                        items(proposals) { proposal ->
                            ProposalCard(
                                proposal = proposal,
                                onClick = { onProposalClick(proposal.proposalId) }
                            )
                        }
                    }
                }
                }
            }
        }
    }
}

@Composable
private fun ProposalTabsRow(
    selectedTab: ProposalTab,
    onTabSelected: (ProposalTab) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly // Equal spacing horizontally
    ) {
        ProposalTab.values().forEach { tab ->
            val isSelected = selectedTab == tab
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clickable { onTabSelected(tab) }
                    .padding(vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = tab.displayName,
                    fontSize = 14.sp,
                    fontWeight = if (isSelected) FontWeight(600) else FontWeight(500), // Selected: bold, unselected: medium
                    color = if (isSelected) Color(0xFFFFFFFF) else Color(0xFF6B7280)
                )
                
                // Blue underline for selected tab - exact width of text, 2dp height
                if (isSelected) {
                    Spacer(modifier = Modifier.height(7.dp)) // Spacing between text and underline
                    Box(
                        modifier = Modifier
                            .height(2.dp)
                            .fillMaxWidth(0.75f) // Width approximately matches text width
                            .background(Color(0xFF3B82F6))
                    )
                }
            }
        }
    }
}

@Composable
private fun ProposalFilterPills(
    selectedFilter: ProposalStatusFilter,
    onFilterSelected: (ProposalStatusFilter) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(scrollState),
        horizontalArrangement = Arrangement.spacedBy(8.dp) // 8dp spacing between pills
    ) {
        ProposalStatusFilter.values().forEach { filter ->
            // Pill container - 32-34dp height, 20dp border radius
            Surface(
                modifier = Modifier
                    .height(33.dp), // 32-34dp
                    shape = RoundedCornerShape(20.dp), // 20dp rounded corners
                color = if (selectedFilter == filter) {
                    Color(0xFF2563EB) // Selected: blue background
                } else {
                    Color(0xFF111827) // Unselected: dark gray background
                },
                onClick = { onFilterSelected(filter) }
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp), // 16dp horizontal padding
                    contentAlignment = Alignment.Center // Center text vertically and horizontally
                ) {
                    Text(
                        text = filter.displayName,
                        fontSize = 13.5.sp, // 13-14sp
                        fontWeight = FontWeight(500),
                        color = if (selectedFilter == filter) {
                            Color(0xFFFFFFFF) // Selected: white text
                        } else {
                            Color(0xFFE5E7EB) // Unselected: light gray text
                        },
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
private fun ProposalCard(
    proposal: Proposal,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp), // 16dp corner radius
        color = Color(0xFF111827), // Card background #111827 or #1F2937
        tonalElevation = 0.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp), // 16-20dp internal padding
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            // Top Row: Title (left) and Status Badge (right) - vertically centered
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Proposal title - 15-16sp, weight 600-700, white, max 2 lines, left aligned
                Text(
                    text = proposal.missionTitle ?: "Mission",
                    fontSize = 15.5.sp, // 15-16sp
                    fontWeight = FontWeight(650), // 600-700
                    color = Color(0xFFFFFFFF),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                
                // Status Badge - right aligned, vertically centered with title
                ProposalStatusBadge(status = proposal.status)
            }
            
            // Name / Client Line - 14sp, weight 500, #E5E7EB, 6dp spacing from title
            Spacer(modifier = Modifier.height(6.dp))
            val clientName = proposal.recruiterName ?: ""
            if (clientName.isNotEmpty()) {
                Text(
                    text = clientName,
                    fontSize = 14.sp,
                    fontWeight = FontWeight(500),
                    color = Color(0xFFE5E7EB)
                )
            }
            
            // Description - 13-14sp, weight 400, #9CA3AF, max 2-3 lines, 6-8dp spacing from name
            Spacer(modifier = Modifier.height(7.dp)) // 6-8dp top margin
            Text(
                text = proposal.message,
                fontSize = 13.5.sp, // 13-14sp
                fontWeight = FontWeight(400),
                color = Color(0xFF9CA3AF),
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 18.sp
            )
            
            // Sent Time - 12-13sp, weight 400, #6B7280, 10-12dp spacing from description
            Spacer(modifier = Modifier.height(11.dp)) // 10-12dp top margin
            Text(
                text = "Sent: ${formatProposalDate(proposal.createdAt)}",
                fontSize = 12.5.sp, // 12-13sp
                fontWeight = FontWeight(400),
                color = Color(0xFF6B7280)
            )
        }
    }
}

@Composable
private fun ProposalStatusBadge(
    status: com.example.matchify.domain.model.ProposalStatus
) {
    val (text, backgroundColor) = when (status) {
        com.example.matchify.domain.model.ProposalStatus.ACCEPTED -> "Accepted" to Color(0xFF15803D) // Green
        com.example.matchify.domain.model.ProposalStatus.VIEWED -> "Viewed" to Color(0xFF16A34A) // Lighter green
        com.example.matchify.domain.model.ProposalStatus.REFUSED -> "Refused" to Color(0xFFB91C1C) // Red
        com.example.matchify.domain.model.ProposalStatus.NOT_VIEWED -> "Not Viewed" to Color(0xFF4B5563) // Gray
    }
    
    Surface(
        modifier = Modifier
            .height(25.dp), // 24-26dp
            shape = RoundedCornerShape(12.dp), // 12dp border radius
        color = backgroundColor
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .padding(horizontal = 11.dp), // 10-12dp horizontal padding
            contentAlignment = Alignment.Center // Center text vertically and horizontally
        ) {
            Text(
                text = text,
                fontSize = 12.sp,
                fontWeight = FontWeight(500),
                color = Color(0xFFFFFFFF), // Always white text
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun EmptyProposalsView() {
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
                tint = Color.White.copy(alpha = 0.5f)
            )
            Text(
                text = "No Proposals",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )
            Text(
                text = "You have not applied to any missions yet.",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.7f)
            )
        }
    }
}

private fun formatProposalDate(dateString: String?): String {
    if (dateString == null) return "recently"
    
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
        inputFormat.timeZone = TimeZone.getTimeZone("UTC")
        val date = inputFormat.parse(dateString)
        
        if (date == null) return "recently"
        
        val now = Date()
        val diff = (now.time - date.time) / (1000 * 60) // diff in minutes
        
        when {
            diff < 1 -> "just now"
            diff < 60 -> "${diff}m ago"
            diff < 1440 -> {
                val hours = diff / 60
                "${hours}h ago"
            }
            diff < 10080 -> {
                val days = diff / 1440
                if (days == 1L) "yesterday" else "${days}d ago"
            }
            else -> {
                val outputFormat = SimpleDateFormat("MMM d, yyyy", Locale.US)
                outputFormat.format(date)
            }
        }
    } catch (e: Exception) {
        "recently"
    }
}

// Recruiter UI with dark theme
@Composable
private fun RecruiterProposalsScreen(
    proposals: List<Proposal>,
    isLoading: Boolean,
    errorMessage: String?,
    missions: List<com.example.matchify.domain.model.Mission>,
    selectedMission: com.example.matchify.domain.model.Mission?,
    isLoadingMissions: Boolean,
    aiSortEnabled: Boolean,
    onMissionSelected: (com.example.matchify.domain.model.Mission?) -> Unit,
    onToggleAiSort: () -> Unit,
    onProposalClick: (String) -> Unit,
    onDrawerItemSelected: (com.example.matchify.ui.missions.components.DrawerMenuItemType) -> Unit,
    viewModel: ProposalsViewModel
) {
    // Get user for avatar
    val context = androidx.compose.ui.platform.LocalContext.current
    val prefs = remember { com.example.matchify.data.local.AuthPreferencesProvider.getInstance().get() }
    val user by prefs.user.collectAsState(initial = null)
    
    // Drawer state
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    
    // Navigation Drawer wraps the entire content
    com.example.matchify.ui.missions.components.NewDrawerContent(
        drawerState = drawerState,
        currentRoute = null,
        onClose = {
            scope.launch {
                drawerState.close()
            }
        },
        onMenuItemSelected = { itemType ->
            scope.launch {
                drawerState.close()
            }
            onDrawerItemSelected(itemType)
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF0F172A))
        ) {
            // Custom AppBar
            com.example.matchify.ui.components.CustomAppBar(
                title = "Proposals",
                profileImageUrl = user?.profileImageUrl,
                onProfileClick = {
                    scope.launch {
                        drawerState.open()
                    }
                }
            )
            
        // Mission Selector & AI Toggle
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Mission Dropdown
            var expanded by remember { mutableStateOf(false) }
            
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { if (!isLoadingMissions) expanded = true },
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFF1E293B),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF374151))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Work,
                            contentDescription = null,
                            tint = Color(0xFF3B82F6),
                            modifier = Modifier.size(20.dp)
                        )
                        
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = selectedMission?.title ?: "Select a mission",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                color = if (selectedMission != null) Color.White else Color(0xFF9CA3AF),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            selectedMission?.let {
                                Text(
                                    text = it.formattedDate,
                                    fontSize = 12.sp,
                                    color = Color(0xFF6B7280)
                                )
                            }
                        }
                        
                        // Unviewed count badge
                        selectedMission?.unviewedCount?.let { count ->
                            if (count > 0) {
                                Surface(
                                    shape = CircleShape,
                                    color = Color(0xFFEF4444)
                                ) {
                                    Text(
                                        text = count.toString(),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                    }
                    
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = null,
                        tint = Color(0xFF9CA3AF)
                    )
                }
            }
            
            // Dropdown Menu
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .background(Color(0xFF1E293B))
            ) {
                missions.forEach { mission ->
                    DropdownMenuItem(
                        text = {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = mission.title,
                                        color = Color.White,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Text(
                                        text = mission.formattedDate,
                                        color = Color(0xFF9CA3AF),
                                        fontSize = 12.sp
                                    )
                                }
                                
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    mission.unviewedCount?.let { count ->
                                        if (count > 0) {
                                            Surface(
                                                shape = CircleShape,
                                                color = Color(0xFFEF4444)
                                            ) {
                                                Text(
                                                    text = count.toString(),
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color.White,
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                )
                                            }
                                        }
                                    }
                                    
                                    if (selectedMission?.missionId == mission.missionId) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = null,
                                            tint = Color(0xFF3B82F6),
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                            }
                        },
                        onClick = {
                            onMissionSelected(mission)
                            expanded = false
                        },
                        colors = MenuDefaults.itemColors(
                            textColor = Color.White
                        )
                    )
                }
            }
            
            // AI Sort Toggle (only when mission selected)
            if (selectedMission != null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onToggleAiSort,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (aiSortEnabled) Color(0xFF3B82F6) else Color(0xFF1E293B),
                            contentColor = if (aiSortEnabled) Color.White else Color(0xFF3B82F6)
                        ),
                        shape = RoundedCornerShape(12.dp),
                        border = if (!aiSortEnabled) androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF3B82F6)) else null
                    ) {
                        Icon(
                            imageVector = Icons.Filled.AutoAwesome,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (aiSortEnabled) "AI Sorting" else "AI Sort",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    
                    // Bouton pour analyser et trouver les meilleures propositions
                    val topProposals by viewModel.topProposals.collectAsState()
                    val isAnalyzing by viewModel.isAnalyzing.collectAsState()
                    
                    Button(
                        onClick = {
                            if (topProposals.isEmpty()) {
                                viewModel.analyzeAndFindTopProposals(topCount = 2)
                            } else {
                                viewModel.clearTopProposals()
                            }
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (topProposals.isNotEmpty()) Color(0xFF10B981) else Color(0xFF1E293B),
                            contentColor = if (topProposals.isNotEmpty()) Color.White else Color(0xFF10B981)
                        ),
                        shape = RoundedCornerShape(12.dp),
                        border = if (topProposals.isEmpty()) androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF10B981)) else null,
                        enabled = !isAnalyzing
                    ) {
                        if (isAnalyzing) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Filled.AutoAwesome,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (topProposals.isNotEmpty()) "Top ${topProposals.size}" else "Find Top",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
        
        // Content
        Box(modifier = Modifier.weight(1f)) {
            when {
                isLoading && proposals.isEmpty() -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = Color(0xFF3B82F6)
                    )
                }
                errorMessage != null -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = errorMessage,
                            color = Color(0xFFEF4444),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 32.dp)
                        )
                    }
                }
                selectedMission == null -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Description,
                            contentDescription = null,
                            modifier = Modifier.size(60.dp),
                            tint = Color(0xFF9CA3AF).copy(alpha = 0.4f)
                        )
                        Text(
                            text = "Select a mission to view proposals",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                        Text(
                            text = "Choose a mission from the dropdown above",
                            fontSize = 15.sp,
                            color = Color(0xFF9CA3AF),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 40.dp)
                        )
                    }
                }
                proposals.isEmpty() -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Description,
                            contentDescription = null,
                            modifier = Modifier.size(60.dp),
                            tint = Color(0xFF9CA3AF).copy(alpha = 0.4f)
                        )
                        Text(
                            text = "No proposals yet",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                        Text(
                            text = "No proposals for this mission yet.",
                            fontSize = 15.sp,
                            color = Color(0xFF9CA3AF)
                        )
                    }
                }
                else -> {
                    val topProposals by viewModel.topProposals.collectAsState()
                    val otherProposals = if (topProposals.isNotEmpty()) {
                        proposals.filter { proposal ->
                            !topProposals.any { it.proposalId == proposal.proposalId }
                        }
                    } else {
                        proposals
                    }
                    
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(
                            start = 16.dp,
                            top = 8.dp,
                            end = 16.dp,
                            bottom = 80.dp
                        ),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Section des meilleures propositions (Top 1-2)
                        if (topProposals.isNotEmpty()) {
                            item {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.AutoAwesome,
                                        contentDescription = null,
                                        tint = Color(0xFF10B981),
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Text(
                                        text = "Meilleures propositions recommandées",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF10B981)
                                    )
                                }
                            }
                            
                            items(topProposals) { proposal ->
                                TopProposalCard(
                                    proposal = proposal,
                                    showAiScore = true,
                                    onClick = { onProposalClick(proposal.proposalId) }
                                )
                            }
                            
                            if (otherProposals.isNotEmpty()) {
                                item {
                                    HorizontalDivider(
                                        modifier = Modifier.padding(vertical = 8.dp),
                                        color = Color(0xFF374151),
                                        thickness = 1.dp
                                    )
                                }
                                
                                item {
                                    Text(
                                        text = "Autres propositions",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = Color(0xFF9CA3AF),
                                        modifier = Modifier.padding(vertical = 4.dp)
                                    )
                                }
                            }
                        }
                        
                        // Autres propositions
                        items(otherProposals) { proposal ->
                            RecruiterProposalCard(
                                proposal = proposal,
                                showAiScore = aiSortEnabled,
                                onClick = { onProposalClick(proposal.proposalId) }
                            )
                        }
                    }
                }
                }
            }
        }
    }
}

/**
 * Carte pour les meilleures propositions (mise en évidence avec bordure verte)
 */
@Composable
private fun TopProposalCard(
    proposal: Proposal,
    showAiScore: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFF1E293B),
        border = androidx.compose.foundation.BorderStroke(2.dp, Color(0xFF10B981)) // Bordure verte pour mise en évidence
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Badge "TOP MATCH" en haut
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = Color(0xFF10B981).copy(alpha = 0.2f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.AutoAwesome,
                            contentDescription = null,
                            tint = Color(0xFF10B981),
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "TOP MATCH",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF10B981)
                        )
                    }
                }
                
                // Score IA si disponible
                if (showAiScore && proposal.aiScore != null) {
                    val (scoreText, scoreColor) = when {
                        proposal.aiScore >= 80 -> "High Match: ${proposal.aiScore}%" to Color(0xFF10B981)
                        proposal.aiScore >= 60 -> "Good Match: ${proposal.aiScore}%" to Color(0xFF3B82F6)
                        else -> "Match: ${proposal.aiScore}%" to Color(0xFF6B7280)
                    }
                    
                    Surface(
                        color = scoreColor.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = scoreText,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = scoreColor
                        )
                    }
                }
            }
            
            // Contenu de la carte (même structure que RecruiterProposalCard)
            RecruiterProposalCardContent(proposal, showAiScore)
        }
    }
}

@Composable
private fun RecruiterProposalCard(
    proposal: Proposal,
    showAiScore: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFF1E293B),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF374151))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            RecruiterProposalCardContent(proposal, showAiScore)
        }
    }
}

@Composable
private fun RecruiterProposalCardContent(
    proposal: Proposal,
    showAiScore: Boolean
) {
    // Top Row: Title and Status
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = proposal.missionTitle ?: "Mission",
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.White,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
        )
        
        ProposalStatusBadge(status = proposal.status)
    }
    
    // AI Score Badge (when enabled) - seulement si pas déjà affiché dans TopProposalCard
    if (showAiScore && proposal.aiScore != null) {
        val (scoreText, scoreColor) = when {
            proposal.aiScore >= 80 -> "High Match: ${proposal.aiScore}%" to Color(0xFF10B981)
            proposal.aiScore >= 60 -> "Good Match: ${proposal.aiScore}%" to Color(0xFF3B82F6)
            else -> "Match: ${proposal.aiScore}%" to Color(0xFF6B7280)
        }
        
        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .background(scoreColor.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
                .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.AutoAwesome,
                contentDescription = null,
                tint = scoreColor,
                modifier = Modifier.size(14.dp)
            )
            Text(
                text = scoreText,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = scoreColor
            )
        }
    }
    
    // Talent Name
    Row(
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Filled.Person,
            contentDescription = null,
            tint = Color(0xFF9CA3AF),
            modifier = Modifier.size(14.dp)
        )
        Text(
            text = proposal.talentFullName,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF9CA3AF)
        )
    }
    
    // Message Preview
    Text(
        text = proposal.message,
        fontSize = 14.sp,
        color = Color(0xFF9CA3AF),
        maxLines = 2,
        overflow = TextOverflow.Ellipsis,
        lineHeight = 20.sp
    )
    
    // Date
    Text(
        text = formatProposalDate(proposal.createdAt),
        fontSize = 12.sp,
        color = Color(0xFF6B7280)
    )
}
