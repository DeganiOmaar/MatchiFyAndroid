package com.example.matchify.ui.missions.list

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.runtime.getValue
import androidx.compose.material3.rememberDrawerState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.matchify.R
import com.example.matchify.data.local.AuthPreferencesProvider
import com.example.matchify.domain.model.Mission
import com.example.matchify.ui.missions.components.MissionRow
import com.example.matchify.ui.missions.components.NavigationDrawerContent
import com.example.matchify.ui.missions.components.DrawerMenuItemType
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MissionListScreenNew(
    onAddMission: () -> Unit,
    onEditMission: (Mission) -> Unit,
    onMissionClick: (Mission) -> Unit = {},
    onDrawerItemSelected: (com.example.matchify.ui.missions.components.DrawerMenuItemType) -> Unit = {},
    viewModel: MissionListViewModel = viewModel(factory = MissionListViewModelFactory())
) {
    val missions by viewModel.filteredMissions.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val isLoadingFavorites by viewModel.isLoadingFavorites.collectAsState()
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
    
    // Sync ViewModel with drawer state changes (e.g., swipe to dismiss)
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
    
    // Current route for drawer item selection (passed from parent or null)
    val currentRoute: String? = null // Will be passed from parent if needed
    
    // Material 3 Primary Tabs - Always show tabs for talent, structure ready for recruiters too
    val tabs = listOf("Best Matches", "Most Recent", "Favorites")
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
    
    // Sync ViewModel with pager state changes (swipe navigation)
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
    
    // Material 3 Navigation Drawer wraps the entire Scaffold
    NavigationDrawerContent(
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
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Top App Bar - matching Alerts/Proposals exactly
                    TopAppBar(
                        title = { Text("Missions") },
                        navigationIcon = {
                            // Profile Image on the left - clickable to open drawer
                            Surface(
                                modifier = Modifier
                                    .size(40.dp)
                                    .padding(4.dp)
                                    .clickable { 
                                        scope.launch {
                                            drawerState.open()
                                        }
                                        viewModel.openProfileDrawer()
                                    },
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.surface
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
                        },
                        actions = {
                            if (isRecruiter && onAddMission != null) {
                                IconButton(onClick = onAddMission) {
                                    Icon(
                                        imageVector = Icons.Filled.Add,
                                        contentDescription = "Add Mission"
                                    )
                                }
                            }
                        }
                    )
                    
                    // Search Bar - matching Material 3 design patterns
                    OutlinedTextField(
                        value = searchText,
                        onValueChange = { viewModel.updateSearchText(it) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        placeholder = {
                            Text(
                                text = "Search for jobs",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Filled.Search,
                                contentDescription = "Search",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        },
                        trailingIcon = {
                            if (searchText.isNotEmpty()) {
                                IconButton(onClick = { viewModel.updateSearchText("") }) {
                                    Icon(
                                        imageVector = Icons.Filled.Close,
                                        contentDescription = "Clear",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                            focusedBorderColor = MaterialTheme.colorScheme.outline,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                            focusedTextColor = MaterialTheme.colorScheme.onSurface,
                            unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                        ),
                        textStyle = MaterialTheme.typography.bodyMedium,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                        keyboardActions = KeyboardActions(onSearch = { /* Handle search */ })
                    )
                    
                    // Material 3 Primary Tabs - matching Material 3 design patterns
                    if (isTalent) {
                        ScrollableTabRow(
                            selectedTabIndex = selectedTabIndex,
                            modifier = Modifier.fillMaxWidth(),
                            containerColor = MaterialTheme.colorScheme.surface,
                            contentColor = MaterialTheme.colorScheme.primary,
                            edgePadding = 16.dp,
                            indicator = { tabPositions ->
                                if (selectedTabIndex < tabPositions.size) {
                                    TabRowDefaults.SecondaryIndicator(
                                        modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                                        color = MaterialTheme.colorScheme.primary,
                                        height = 3.dp
                                    )
                                }
                            },
                            divider = {
                                HorizontalDivider(
                                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.12f),
                                    thickness = 0.5.dp
                                )
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
                                            style = MaterialTheme.typography.labelLarge,
                                            fontWeight = if (selectedTabIndex == index) {
                                                FontWeight.SemiBold
                                            } else {
                                                FontWeight.Medium
                                            }
                                        )
                                    },
                                    selectedContentColor = MaterialTheme.colorScheme.primary,
                                    unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            },
            containerColor = MaterialTheme.colorScheme.surface
        ) { paddingValues ->
            Box(modifier = Modifier.fillMaxSize()) {
                // Main content with swipe navigation support
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
                            isLoading = isLoading,
                            isLoadingFavorites = false,
                            isTalent = isTalent,
                            viewModel = viewModel,
                            onMissionClick = onMissionClick,
                            onAddMission = if (isRecruiter) onAddMission else null,
                            isFavoritesTab = false,
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
    onEditMission: ((Mission) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val isLoadingList = isLoading || (isTalent && isFavoritesTab && isLoadingFavorites)
    
    when {
        isLoadingList && missions.isEmpty() -> {
            Box(
                modifier = modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
        missions.isEmpty() -> {
            EmptyStateViewNew(
                onAddMission = onAddMission,
                isTalent = isTalent,
                isFavoritesTab = isFavoritesTab,
                modifier = modifier
            )
        }
        else -> {
            LazyColumn(
                modifier = modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(missions) { mission ->
                    // Check if current user is owner of this mission
                    val isOwner = viewModel.isMissionOwner(mission)
                    val isRecruiter = !isTalent
                    
                    MissionRow(
                        mission = mission,
                        isOwner = isOwner && isRecruiter,
                        isRecruiter = isRecruiter,
                        onClick = { onMissionClick(mission) },
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
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
            )
            Text(
                text = "No Missions",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = when {
                    isFavoritesTab -> "You have not saved any favorite missions yet."
                    isTalent -> "No missions available at the moment."
                    else -> "You have not created any missions yet."
                },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

