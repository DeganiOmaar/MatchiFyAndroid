package com.example.matchify.ui.missions.list

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Work
import androidx.compose.material.icons.rounded.ArrowForward
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.matchify.R
import com.example.matchify.data.local.AuthPreferencesProvider
import com.example.matchify.domain.model.Mission
import com.example.matchify.ui.missions.components.MissionCardNew
import com.example.matchify.ui.missions.components.NewDrawerContent
import com.example.matchify.ui.missions.components.DrawerMenuItemType
import com.example.matchify.ui.components.CustomAppBar
import com.example.matchify.data.remote.ApiService
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MissionListScreenNew(
    onAddMission: () -> Unit,
    onEditMission: (Mission) -> Unit,
    onMissionClick: (Mission) -> Unit = {},
    onDrawerItemSelected: (DrawerMenuItemType) -> Unit = {},
    onNavigateToAlerts: () -> Unit = {},
    onTalentProfileClick: (String) -> Unit = {},
    viewModel: MissionListViewModel = viewModel(factory = MissionListViewModelFactory())
) {
    val missions by viewModel.filteredMissions.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val isLoadingFavorites by viewModel.isLoadingFavorites.collectAsState()
    val isLoadingBestMatches by viewModel.isLoadingBestMatches.collectAsState()
    val searchText by viewModel.searchText.collectAsState()
    val selectedTab by viewModel.selectedTab.collectAsState()
    val showDrawer by viewModel.showProfileDrawer.collectAsState()

    // Get user role and profile image
    val context = LocalContext.current
    val prefs = remember { AuthPreferencesProvider.getInstance().get() }
    val userRole by prefs.role.collectAsState(initial = "recruiter")
    val user by prefs.user.collectAsState(initial = null)
    val isRecruiter = userRole == "recruiter"
    val isTalent = userRole == "talent"

    // Material 3 Drawer State
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // Sync drawer state with ViewModel
    LaunchedEffect(showDrawer) {
        if (showDrawer && drawerState.isClosed) {
            drawerState.open()
        } else if (!showDrawer && drawerState.isOpen) {
            drawerState.close()
        }
    }

    // Sync ViewModel with drawer state changes
    LaunchedEffect(drawerState.currentValue) {
        val isOpen = drawerState.isOpen
        if (isOpen != showDrawer) {
            if (isOpen) {
                viewModel.openProfileDrawer()
            } else {
                viewModel.closeProfileDrawer()
            }
        }
    }

    // Current route for drawer item selection
    val currentRoute: String? = null

    // Tabs for Talent: Best Match, Most Recent (default), Favourites
    val tabs = listOf("Best Match", "Most Recent", "Favourites")
    val pagerState = rememberPagerState(
        initialPage = when (selectedTab) {
            MissionTab.BEST_MATCHES -> 0
            MissionTab.MOST_RECENT -> 1
            MissionTab.FAVORITES -> 2
        },
        pageCount = { if (isTalent) 3 else 1 }
    )
    val coroutineScope = rememberCoroutineScope()

    // Sync pager state with ViewModel tab selection
    LaunchedEffect(selectedTab) {
        val targetPage = when (selectedTab) {
            MissionTab.BEST_MATCHES -> 0
            MissionTab.MOST_RECENT -> 1
            MissionTab.FAVORITES -> 2
        }
        if (pagerState.currentPage != targetPage) {
            pagerState.animateScrollToPage(targetPage)
        }
    }

    // Load missions when screen appears and when returning to screen
    LaunchedEffect(Unit) {
        viewModel.loadMissions()
    }
    
    // Recharger les missions quand on revient sur cet écran
    // Le realtime client devrait aussi mettre à jour automatiquement
    
    // Sync ViewModel with pager state changes
    LaunchedEffect(pagerState.currentPage) {
        val newTab = when (pagerState.currentPage) {
            0 -> MissionTab.BEST_MATCHES
            1 -> MissionTab.MOST_RECENT
            2 -> MissionTab.FAVORITES
            else -> MissionTab.MOST_RECENT
        }
        if (selectedTab != newTab) {
            viewModel.selectTab(newTab)
            if (newTab == MissionTab.FAVORITES) {
                viewModel.loadFavorites()
            }
        }
    }

    val selectedTabIndex = when (selectedTab) {
        MissionTab.BEST_MATCHES -> 0
        MissionTab.MOST_RECENT -> 1
        MissionTab.FAVORITES -> 2
    }

    // Navigation Drawer wraps the entire Scaffold
    NewDrawerContent(
        drawerState = drawerState,
        currentRoute = currentRoute,
        onClose = {
            scope.launch {
                drawerState.close()
            }
            viewModel.closeProfileDrawer()
        },
        onMenuItemSelected = { itemType ->
            scope.launch {
                drawerState.close()
            }
            viewModel.closeProfileDrawer()
            onDrawerItemSelected(itemType)
        }
    ) {
        Scaffold(
            topBar = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF0F172A))
                ) {
                    // Reusable AppBar
                    CustomAppBar(
                        title = "Missions",
                        profileImageUrl = user?.profileImageUrl,
                        onProfileClick = {
                            scope.launch {
                                drawerState.open()
                            }
                            viewModel.openProfileDrawer()
                        }
                    )

                    // Search Bar - Simplified
                    OutlinedTextField(
                        value = searchText,
                        onValueChange = { viewModel.updateSearchText(it) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .height(56.dp),
                        placeholder = {
                            Text(
                                text = "Rechercher...",
                                color = Color(0xFFE2E8F0),
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Medium
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Filled.Search,
                                contentDescription = "Search",
                                tint = Color(0xFFCBD5E1),
                                modifier = Modifier.size(24.dp)
                            )
                        },
                        textStyle = androidx.compose.ui.text.TextStyle(
                            color = Color.White,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Normal
                        ),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color(0xFF1E293B),
                            unfocusedContainerColor = Color(0xFF1E293B),
                            focusedBorderColor = Color(0xFF3B82F6),
                            unfocusedBorderColor = Color(0xFF64748B),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            cursorColor = Color(0xFF3B82F6),
                            unfocusedPlaceholderColor = Color(0xFFE2E8F0),
                            focusedPlaceholderColor = Color(0xFFE2E8F0)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp)) // Spacing between search and tabs

                    // Tabs - Talent only: Simplified
                    if (isTalent) {
                        TabRow(
                            selectedTabIndex = selectedTabIndex,
                            modifier = Modifier.padding(horizontal = 16.dp),
                            containerColor = Color(0xFF0F172A),
                            contentColor = Color.White,
                            indicator = { tabPositions ->
                                if (selectedTabIndex < tabPositions.size) {
                                    TabRowDefaults.Indicator(
                                        modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                                        color = Color(0xFF3B82F6)
                                    )
                                }
                            }
                        ) {
                            tabs.forEachIndexed { index, title ->
                                Tab(
                                    selected = selectedTabIndex == index,
                                    onClick = {
                                        coroutineScope.launch {
                                            val newTab = when (index) {
                                                0 -> MissionTab.BEST_MATCHES
                                                1 -> MissionTab.MOST_RECENT
                                                2 -> MissionTab.FAVORITES
                                                else -> MissionTab.MOST_RECENT
                                            }
                                            viewModel.selectTab(newTab)
                                            pagerState.animateScrollToPage(index)
                                            if (newTab == MissionTab.FAVORITES) {
                                                viewModel.loadFavorites()
                                            }
                                        }
                                    },
                                    text = {
                                        Text(
                                            text = title,
                                            fontSize = 14.sp
                                        )
                                    }
                                )
                            }
                        }
                    }
                }
            },
            containerColor = Color(0xFF0F172A) // Dark navy background
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFF0F172A))
            ) {
                // Main content with swipe navigation support for Talent
                if (isTalent) {
                    HorizontalPager(
                        state = pagerState,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                        userScrollEnabled = true
                    ) { page ->
                        when (page) {
                            0 -> MissionTabContent(
                                missions = missions,
                                isLoading = isLoading || isLoadingBestMatches,
                                isLoadingFavorites = false,
                                isTalent = isTalent,
                                viewModel = viewModel,
                                onMissionClick = onMissionClick,
                                onAddMission = if (isRecruiter) onAddMission else null,
                                isFavoritesTab = false,
                                isBestMatchTab = true,
                                onEditMission = onEditMission,
                                onTalentProfileClick = onTalentProfileClick
                            )
                            1 -> MissionTabContent(
                                missions = missions,
                                isLoading = isLoading,
                                isLoadingFavorites = false,
                                isTalent = isTalent,
                                viewModel = viewModel,
                                onMissionClick = onMissionClick,
                                onAddMission = if (isRecruiter) onAddMission else null,
                                isFavoritesTab = false,
                                onEditMission = onEditMission,
                                onTalentProfileClick = onTalentProfileClick
                            )
                            2 -> MissionTabContent(
                                missions = missions,
                                isLoading = isLoading,
                                isLoadingFavorites = isLoadingFavorites,
                                isTalent = isTalent,
                                viewModel = viewModel,
                                onMissionClick = onMissionClick,
                                onAddMission = if (isRecruiter) onAddMission else null,
                                isFavoritesTab = true,
                                onEditMission = onEditMission,
                                onTalentProfileClick = onTalentProfileClick
                            )
                            else -> MissionTabContent(
                                missions = missions,
                                isLoading = isLoading,
                                isLoadingFavorites = false,
                                isTalent = isTalent,
                                viewModel = viewModel,
                                onMissionClick = onMissionClick,
                                onAddMission = if (isRecruiter) onAddMission else null,
                                isFavoritesTab = false,
                                onEditMission = onEditMission,
                                onTalentProfileClick = onTalentProfileClick
                            )
                        }
                    }
                } else {
                    // Recruiter view: Split screen - Missions on top, Recommended Talents on bottom
                    RecruiterSplitView(
                        missions = missions,
                        isLoading = isLoading,
                        viewModel = viewModel,
                        onMissionClick = onMissionClick,
                        onAddMission = onAddMission,
                        onEditMission = onEditMission,
                        onTalentProfileClick = onTalentProfileClick,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                    )
                }
            }
        }
    }
}

@Composable
fun MissionTabContent(
    missions: List<Mission>,
    isLoading: Boolean,
    isLoadingFavorites: Boolean,
    isTalent: Boolean,
    viewModel: MissionListViewModel,
    onMissionClick: (Mission) -> Unit,
    onAddMission: (() -> Unit)?,
    isFavoritesTab: Boolean,
    isBestMatchTab: Boolean = false,
    onEditMission: ((Mission) -> Unit)? = null,
    onTalentProfileClick: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val isLoadingList = isLoading || (isTalent && isFavoritesTab && isLoadingFavorites)
    
    // État pour la confirmation de suppression
    var missionToDelete by remember { mutableStateOf<Mission?>(null) }

    when {
        isLoadingList && missions.isEmpty() -> {
            Box(
                modifier = modifier
                    .fillMaxSize()
                    .background(Color(0xFF0F172A)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = Color(0xFF3B82F6)
                )
            }
        }
        missions.isEmpty() -> {
            EmptyStateViewNew(
                onAddMission = onAddMission,
                isTalent = isTalent,
                isFavoritesTab = isFavoritesTab,
                modifier = modifier
                    .background(Color(0xFF0F172A))
            )
        }
        else -> {
            Box(modifier = modifier.fillMaxSize()) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFF0F172A)),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(0.dp)
                ) {
                    items(missions.size) { index ->
                        val mission = missions[index]
                        val isOwner = viewModel.isMissionOwner(mission)
                        val isRecruiter = !isTalent
                        val isFavorite = viewModel.isFavorite(mission)

                        // Simplified without SwipeToDismiss - just show the card
                        Column {
                            MissionCardNew(
                                mission = mission,
                                isFavorite = isFavorite,
                                onFavoriteToggle = { viewModel.toggleFavorite(mission) },
                                onClick = { onMissionClick(mission) },
                                isOwner = isOwner && isRecruiter,
                                isRecruiter = isRecruiter,
                                onEdit = {
                                    if (isRecruiter && isOwner && onEditMission != null) {
                                        onEditMission(mission)
                                    }
                                },
                                onDelete = {
                                    missionToDelete = mission
                                }
                            )

                            // Divider between cards
                            if (index < missions.size - 1) {
                                Spacer(modifier = Modifier.height(14.dp))
                                HorizontalDivider(
                                    color = Color(0xFF1E293B),
                                    thickness = 1.dp
                                )
                            }
                        }

                        }
                    }
                }
                
                // Confirmation Alert Dialog (en dehors du LazyColumn)
                missionToDelete?.let { mission ->
                    val isOwner = viewModel.isMissionOwner(mission)
                    val isRecruiter = !isTalent
                    AlertDialog(
                        onDismissRequest = { missionToDelete = null },
                        title = {
                            Text(
                                text = "Supprimer la mission",
                                fontWeight = FontWeight.Bold
                            )
                        },
                        text = {
                            Text("Êtes-vous sûr de vouloir supprimer cette mission ? Cette action est irréversible.")
                        },
                        confirmButton = {
                            TextButton(
                                onClick = {
                                    if (isRecruiter && isOwner) {
                                        viewModel.deleteMission(mission)
                                    }
                                    missionToDelete = null
                                },
                                colors = ButtonDefaults.textButtonColors(
                                    contentColor = MaterialTheme.colorScheme.error
                                )
                            ) {
                                Text("Supprimer", fontWeight = FontWeight.SemiBold)
                            }
                        },
                        dismissButton = {
                            TextButton(
                                onClick = { missionToDelete = null }
                            ) {
                                Text("Annuler")
                            }
                        },
                        containerColor = MaterialTheme.colorScheme.surface,
                        icon = {
                            Icon(
                                imageVector = Icons.Rounded.Delete,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    )
                }
            }
        }
    }
@Composable
fun EmptyStateViewNew(
    onAddMission: (() -> Unit)?,
    isTalent: Boolean = false,
    isFavoritesTab: Boolean = false,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Work,
                contentDescription = null,
                modifier = Modifier.size(60.dp),
                tint = Color.White.copy(alpha = 0.5f)
            )
            Text(
                text = "No Missions",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )
            Text(
                text = when {
                    isFavoritesTab -> "You have not saved any favorite missions yet."
                    isTalent -> "No missions available at the moment."
                    else -> "You have not created any missions yet."
                },
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.7f)
            )
        }
    }
}

/**
 * Section horizontale scrollable pour afficher les talents recommandés pour une mission
 * NOTE: Cette fonction n'est plus utilisée, conservée pour référence
 */
@Composable
private fun RecommendedTalentsSection(
    missionId: String,
    onTalentClick: (String) -> Unit
) {
    // Cette fonction n'est plus utilisée - utilisez RecommendedTalentsSectionIntegrated à la place
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Cette section n'est plus utilisée",
            color = Color(0xFF94A3B8)
        )
    }
}

/**
 * Carte compacte pour afficher un profil de talent recommandé
 * NOTE: Cette fonction n'est plus utilisée, conservée pour référence
 */
@Composable
private fun TalentProfileCard(
    talent: com.example.matchify.domain.model.UserModel,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .width(200.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFF1E293B),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            Color(0xFF334155)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Header: Avatar + Name + Score
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Avatar placeholder
                    Surface(
                        modifier = Modifier.size(40.dp),
                        shape = CircleShape,
                        color = Color(0xFF3B82F6).copy(alpha = 0.2f)
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Person,
                                contentDescription = null,
                                tint = Color(0xFF3B82F6),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = talent.fullName,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFFFFFFFF),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = talent.location ?: "Non spécifiée",
                            fontSize = 11.sp,
                            color = Color(0xFF94A3B8)
                        )
                    }
                }
            }
            
            // View Profile button
            Button(
                onClick = onClick,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF10B981),
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(vertical = 6.dp)
            ) {
                Text(
                    text = "View Profile",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

/**
 * Vue divisée pour les recruteurs : Missions en haut, Talents recommandés en bas
 */

@Composable
private fun RecruiterSplitView(
    missions: List<Mission>,
    isLoading: Boolean,
    viewModel: MissionListViewModel,
    onMissionClick: (Mission) -> Unit,
    onAddMission: (() -> Unit)?,
    onEditMission: ((Mission) -> Unit)?,
    onTalentProfileClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val isLoadingList = isLoading
    val searchText by viewModel.searchText.collectAsState()
    
    // État pour la confirmation de suppression
    var missionToDelete by remember { mutableStateOf<Mission?>(null) }
    
    Box(
        modifier = modifier.fillMaxSize()
    ) {
        when {
            isLoadingList && missions.isEmpty() -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = Color(0xFF3B82F6)
                    )
                }
            }
            missions.isEmpty() -> {
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    // État vide pour les missions
                    EmptyStateViewNew(
                        onAddMission = onAddMission,
                        isTalent = false,
                        isFavoritesTab = false,
                        modifier = Modifier.weight(1f)
                    )
                    
                    // Section Talents même sans missions - seulement si pas de recherche
                    if (searchText.isEmpty()) {
                        RecommendedTalentsSectionIntegrated(
                            onTalentProfileClick = onTalentProfileClick
                        )
                    }
                }
            }
            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(0.dp)
                ) {
                    items(missions.size) { index ->
                        val mission = missions[index]
                        val isOwner = viewModel.isMissionOwner(mission)
                        val isFavorite = viewModel.isFavorite(mission)
                        
                        // Simplified without SwipeToDismiss
                        Column {
                            MissionCardNew(
                                mission = mission,
                                isFavorite = isFavorite,
                                onFavoriteToggle = { viewModel.toggleFavorite(mission) },
                                onClick = { onMissionClick(mission) },
                                isOwner = isOwner,
                                isRecruiter = true,
                                onEdit = {
                                    if (isOwner && onEditMission != null) {
                                        onEditMission(mission)
                                    }
                                },
                                onDelete = {
                                    missionToDelete = mission
                                }
                            )
                            
                            // Divider between cards
                            if (index < missions.size - 1) {
                                Spacer(modifier = Modifier.height(14.dp))
                                HorizontalDivider(
                                    color = Color(0xFF1E293B),
                                    thickness = 1.dp
                                )
                            }
                        }
                    }
                    
                    // Section Talents intégrée dans le scroll des missions - seulement si pas de recherche
                    if (searchText.isEmpty()) {
                        item {
                            Spacer(modifier = Modifier.height(20.dp))
                            RecommendedTalentsSectionIntegrated(
                                onTalentProfileClick = onTalentProfileClick
                            )
                            Spacer(modifier = Modifier.height(20.dp))
                        }
                    }
                }
            }
        }
    }
    
    // Confirmation Alert Dialog
    missionToDelete?.let { mission ->
        AlertDialog(
            onDismissRequest = { missionToDelete = null },
            title = { Text("Supprimer la mission") },
            text = { Text("Êtes-vous sûr de vouloir supprimer cette mission ?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteMission(mission)
                        missionToDelete = null
                    }
                ) {
                    Text("Supprimer", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { missionToDelete = null }) {
                    Text("Annuler")
                }
            },
            containerColor = Color(0xFF1E293B),
            titleContentColor = Color.White,
            textContentColor = Color(0xFF94A3B8)
        )
    }
}

/**
 * Section Talents intégrée dans le scroll des missions
 */
@Composable
private fun RecommendedTalentsSectionIntegrated(
    onTalentProfileClick: (String) -> Unit
) {
    val viewModel: MissionListViewModel = viewModel(factory = MissionListViewModelFactory())
    val recommendedTalents by viewModel.recommendedTalents.collectAsState()
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Talents recommandés",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            
            TextButton(
                onClick = { onTalentProfileClick("search_all") }
            ) {
                Text(
                    text = "Voir tout",
                    color = Color(0xFF3B82F6),
                    fontSize = 14.sp
                )
            }
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        // List
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (recommendedTalents.isEmpty()) {
                // Loading or empty state
                 Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color(0xFF3B82F6)
                    )
                }
            } else {
                recommendedTalents.forEach { talent ->
                    com.example.matchify.ui.talent.search.components.TalentSearchCard(
                        talent = talent,
                        onClick = { onTalentProfileClick(talent.talentId) }
                    )
                }
            }
        }
    }
}
