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
import com.example.matchify.ui.missions.components.MissionCardNew
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
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surface)
                ) {
                    // Top App Bar with profile and actions
                    TopAppBar(
                        title = {
                            Text(
                                text = "Missions",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.SemiBold
                            )
                        },
                        navigationIcon = {
                            // Profile Image on the left
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
                            if (isRecruiter) {
                                IconButton(
                                    onClick = onAddMission
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Add,
                                        contentDescription = "Add Mission",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.surface,
                            titleContentColor = MaterialTheme.colorScheme.onSurface,
                            navigationIconContentColor = MaterialTheme.colorScheme.onSurface,
                            actionIconContentColor = MaterialTheme.colorScheme.onSurface
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                
                // Search Bar
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
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                    ),
                    textStyle = MaterialTheme.typography.bodyMedium,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(onSearch = { /* Handle search */ })
                )
                
                // Material 3 Primary Tabs - Below toolbar
                if (isTalent) {
                    ScrollableTabRow(
                        selectedTabIndex = selectedTabIndex,
                        modifier = Modifier.fillMaxWidth(),
                        containerColor = MaterialTheme.colorScheme.surface,
                        contentColor = MaterialTheme.colorScheme.primary,
                        edgePadding = 52.dp, // Material 3 standard leading offset for scrollable tabs
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
                            isFavoritesTab = false
                        )
                        1 -> MissionTabContent(
                            missions = missions,
                            isLoading = isLoading,
                            isLoadingFavorites = false,
                            isTalent = isTalent,
                            viewModel = viewModel,
                            onMissionClick = onMissionClick,
                            onAddMission = if (isRecruiter) onAddMission else null,
                            isFavoritesTab = false
                        )
                        2 -> MissionTabContent(
                            missions = missions,
                            isLoading = isLoading,
                            isLoadingFavorites = isLoadingFavorites,
                            isTalent = isTalent,
                            viewModel = viewModel,
                            onMissionClick = onMissionClick,
                            onAddMission = if (isRecruiter) onAddMission else null,
                            isFavoritesTab = true
                        )
                        else -> MissionTabContent(
                            missions = missions,
                            isLoading = isLoading,
                            isLoadingFavorites = false,
                            isTalent = isTalent,
                            viewModel = viewModel,
                            onMissionClick = onMissionClick,
                            onAddMission = if (isRecruiter) onAddMission else null,
                            isFavoritesTab = false
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
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(
                    horizontal = 20.dp,
                    vertical = 20.dp
                )
            ) {
                items(missions.size) { index ->
                    val mission = missions[index]
                    MissionCardNew(
                        mission = mission,
                        isFavorite = viewModel.isFavorite(mission),
                        onFavoriteToggle = { viewModel.toggleFavorite(mission) },
                        onClick = { onMissionClick(mission) },
                        modifier = Modifier.fillMaxWidth()
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
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Material 3 Surface for icon container
        Surface(
            modifier = Modifier.size(80.dp),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.surfaceContainerHighest,
            tonalElevation = 0.dp
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                Icon(
                    imageVector = Icons.Filled.Work,
                    contentDescription = null,
                    modifier = Modifier.size(40.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            text = "No missions yet",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        Text(
            text = when {
                isFavoritesTab -> "You can save your favourite or wait until there a new missions for best match and most recent missions"
                isTalent -> "You can save your favourite or wait until there a new missions for best match and most recent missions"
                else -> "Create your first mission offer to get started"
            },
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
        
        if (onAddMission != null) {
            Spacer(modifier = Modifier.height(40.dp))
            
            // Material 3 Filled Button
            Button(
                onClick = onAddMission,
                modifier = Modifier
                    .fillMaxWidth(0.75f)
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 2.dp,
                    pressedElevation = 4.dp
                )
            ) {
                Text(
                    text = "Create Mission",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

