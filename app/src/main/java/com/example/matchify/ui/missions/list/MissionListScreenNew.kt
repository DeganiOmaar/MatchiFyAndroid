package com.example.matchify.ui.missions.list

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
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
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MissionListScreenNew(
    onAddMission: () -> Unit,
    onEditMission: (Mission) -> Unit,
    onMissionClick: (Mission) -> Unit = {},
    onDrawerItemSelected: (DrawerMenuItemType) -> Unit = {},
    onNavigateToAlerts: () -> Unit = {},
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

    // Refresh missions when the screen is shown
    LaunchedEffect(Unit) {
        android.util.Log.d("MissionListScreenNew", "Refreshing missions on screen entry")
        viewModel.refreshMissions()
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
                        .background(Color(0xFF0F172A)) // Dark navy background
                ) {
                    // Header Section - Avatar left, Bell right
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 18.dp), // Top padding 16-20px
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Circular profile avatar - 40-44px
                        Surface(
                            modifier = Modifier
                                .size(42.dp) // 40-44px
                                .clickable { 
                                    scope.launch {
                                        drawerState.open()
                                    }
                                    viewModel.openProfileDrawer()
                                },
                            shape = CircleShape,
                            color = Color(0xFF1E293B)
                        ) {
                            Box {
                                val profileImageUrl = user?.profileImageUrl
                                if (profileImageUrl != null) {
                                    AsyncImage(
                                        model = profileImageUrl,
                                        contentDescription = "Profile",
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                } else {
                                    Image(
                                        painter = painterResource(R.drawable.avatar),
                                        contentDescription = "Avatar",
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                }
                            }
                        }
                        
                        // Notification bell - 22-24px, white
                        IconButton(
                            onClick = { onNavigateToAlerts() },
                            modifier = Modifier.size(40.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Notifications,
                                contentDescription = "Notifications",
                                tint = Color.White,
                                modifier = Modifier.size(23.dp) // 22-24px
                            )
                        }
                    }
                    
                    // Search Bar - 48-52px height, #1E293B background, 12px radius
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .height(50.dp), // 48-52px
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFF1E293B)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 14.dp), // Left padding 12-16px
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Search icon on left - #64748B, 18-20px
                            Icon(
                                imageVector = Icons.Filled.Search,
                                contentDescription = "Search",
                                tint = Color(0xFF64748B),
                                modifier = Modifier.size(19.dp) // 18-20px
                            )
                            
                            // Text input
                            BasicTextField(
                                value = searchText,
                                onValueChange = { viewModel.updateSearchText(it) },
                                modifier = Modifier.weight(1f),
                                textStyle = MaterialTheme.typography.bodyMedium.copy(
                                    fontSize = 14.5.sp, // 14-15px
                                    fontWeight = FontWeight(400),
                                    color = Color.White
                                ),
                                singleLine = true,
                                decorationBox = { innerTextField ->
                                    if (searchText.isEmpty()) {
                                        Text(
                                            text = "Search by keyword, skill…",
                                            fontSize = 14.5.sp,
                                            fontWeight = FontWeight(400),
                                            color = Color(0xFF94A3B8)
                                        )
                                    }
                                    innerTextField()
                                },
                                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                                keyboardActions = KeyboardActions(onSearch = { /* Handle search */ })
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp)) // Spacing between search and tabs
                    
                    // Tabs - Talent only: Best Match, Most Recent, Favourites
                    if (isTalent) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 0.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            tabs.forEachIndexed { index, title ->
                                val isSelected = selectedTabIndex == index
                                Column(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable {
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
                                        }
                                        .padding(vertical = 12.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(
                                            text = title,
                                            fontSize = 14.sp,
                                            fontWeight = if (isSelected) FontWeight(600) else FontWeight(500),
                                            color = if (isSelected) Color(0xFFFFFFFF) else Color(0xFF94A3B8)
                                        )
                                        
                                        // Blue underline for active tab - 2px height, 6-8px spacing, width based on text
                                        if (isSelected) {
                                            Spacer(modifier = Modifier.height(7.dp)) // 6-8px spacing
                                            // Underline width approximately matches text width (70-80% of tab width)
                                            Box(
                                                modifier = Modifier
                                                    .height(2.dp)
                                                    .fillMaxWidth(0.75f)
                                                    .align(Alignment.CenterHorizontally)
                                                    .background(Color(0xFF3B82F6))
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            },
            floatingActionButton = {
                if (isRecruiter) {
                    FloatingActionButton(
                        onClick = onAddMission,
                        containerColor = Color(0xFF3B82F6),
                        contentColor = Color.White,
                        shape = CircleShape
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = "Add Mission"
                        )
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
                                onEditMission = onEditMission
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
                                onEditMission = onEditMission
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
                                onEditMission = onEditMission
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
                                onEditMission = onEditMission
                            )
                        }
                    }
                } else {
                    // Recruiter view (no tabs)
                    MissionTabContent(
                        missions = missions,
                        isLoading = isLoading,
                        isLoadingFavorites = false,
                        isTalent = isTalent,
                        viewModel = viewModel,
                        onMissionClick = onMissionClick,
                        onAddMission = if (isRecruiter) onAddMission else null,
                        isFavoritesTab = false,
                        onEditMission = onEditMission,
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
    modifier: Modifier = Modifier
) {
    val isLoadingList = isLoading || (isTalent && isFavoritesTab && isLoadingFavorites)
    
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
            LazyColumn(
                modifier = modifier
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
                                if (isRecruiter && isOwner) {
                                    viewModel.deleteMission(mission)
                                }
                            }
                        )
                        
                        // Divider between cards - #1E293B, 1px, spacing 12-16px
                        if (index < missions.size - 1) {
                            Spacer(modifier = Modifier.height(14.dp)) // 12-16px spacing
                            HorizontalDivider(
                                color = Color(0xFF1E293B),
                                thickness = 1.dp
                            )
                        }
                    }
                }
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
                color = Color.White.copy(alpha = 0.7f),
                modifier = Modifier.padding(horizontal = 40.dp),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            
            if (!isTalent && onAddMission != null) {
                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    onClick = onAddMission,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF3B82F6),
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(14.dp),
                    contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp)
                ) {
                    Text(
                        text = "Create Mission",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}
