package com.example.matchify.ui.missions.list

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.runtime.getValue
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.DismissDirection
import androidx.compose.material.DismissValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Work
import androidx.compose.material.rememberDismissState
import androidx.compose.material.SwipeToDismiss
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import android.content.Intent
import android.speech.RecognizerIntent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import com.example.matchify.ui.missions.components.ProfileDrawer

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
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

    var missionToDelete by remember { mutableStateOf<Mission?>(null) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    
    // Speech recognition launcher
    val speechLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val resultCode = result.resultCode
        val data = result.data
        
        if (resultCode == android.app.Activity.RESULT_OK && data != null) {
            val results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            results?.get(0)?.let { spokenText ->
                viewModel.updateSearchText(spokenText)
            }
        }
    }
    
    // Function to start voice recognition
    fun startVoiceRecognition() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, java.util.Locale.getDefault())
            putExtra(RecognizerIntent.EXTRA_PROMPT, "Parlez pour rechercher...")
        }
        
        try {
            speechLauncher.launch(intent)
        } catch (e: Exception) {
            // Speech recognition not available
        }
    }
    
    val drawerWidth = 280.dp
    val drawerOffsetPx = with(LocalDensity.current) { drawerWidth.toPx() }
    val drawerOffset = animateFloatAsState(
        targetValue = if (showDrawer) 0f else -drawerOffsetPx,
        animationSpec = tween(durationMillis = 300),
        label = "drawer_animation"
    )
    
    // Couleur de fond et navbar
    val backgroundColor = Color(0xFF61A5C2)
    
    // Animation infinie pour le background
    val infiniteTransition = rememberInfiniteTransition(label = "background_animation")
    
    // Animation pour les cercles flottants
    val circle1Y by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "circle1_y"
    )
    
    val circle2Y by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "circle2_y"
    )
    
    val circle3X by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(5000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "circle3_x"
    )
    
    val circle1Scale by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "circle1_scale"
    )
    
    val circle2Scale by infiniteTransition.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "circle2_scale"
    )
    
    val circle3Scale by infiniteTransition.animateFloat(
        initialValue = 0.7f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "circle3_scale"
    )
    
    val circle1Alpha by infiniteTransition.animateFloat(
        initialValue = 0.15f,
        targetValue = 0.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "circle1_alpha"
    )
    
    val circle2Alpha by infiniteTransition.animateFloat(
        initialValue = 0.1f,
        targetValue = 0.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "circle2_alpha"
    )
    
    val circle3Alpha by infiniteTransition.animateFloat(
        initialValue = 0.12f,
        targetValue = 0.28f,
        animationSpec = infiniteRepeatable(
            animation = tween(1900, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "circle3_alpha"
    )
    
    Box(modifier = Modifier.fillMaxSize()) {
        // Background animé avec cercles flottants
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor)
        ) {
            // Cercle 1 - En haut à gauche, se déplace verticalement
            Box(
                modifier = Modifier
                    .offset(
                        x = (-50).dp,
                        y = (circle1Y * 200).dp - 50.dp
                    )
                    .size((150 * circle1Scale).dp)
                    .background(
                        color = Color.White.copy(alpha = circle1Alpha),
                        shape = CircleShape
                    )
            )
            
            // Cercle 2 - En haut à droite, se déplace verticalement
            Box(
                modifier = Modifier
                    .offset(
                        x = 300.dp,
                        y = (circle2Y * 180).dp - 40.dp
                    )
                    .size((120 * circle2Scale).dp)
                    .background(
                        color = Color.White.copy(alpha = circle2Alpha),
                        shape = CircleShape
                    )
            )
            
            // Cercle 3 - Au milieu, se déplace horizontalement
            Box(
                modifier = Modifier
                    .offset(
                        x = (circle3X * 250).dp + 50.dp,
                        y = 300.dp
                    )
                    .size((100 * circle3Scale).dp)
                    .background(
                        color = Color.White.copy(alpha = circle3Alpha),
                        shape = CircleShape
                    )
            )
            
            // Cercle 4 - En bas à gauche, rotation et scale
            Box(
                modifier = Modifier
                    .offset(x = 20.dp, y = 500.dp)
                    .size((80 * circle1Scale).dp)
                    .background(
                        color = Color.White.copy(alpha = circle2Alpha),
                        shape = CircleShape
                    )
            )
            
            // Cercle 5 - En bas à droite
            Box(
                modifier = Modifier
                    .offset(x = 280.dp, y = 600.dp)
                    .size((90 * circle3Scale).dp)
                    .background(
                        color = Color.White.copy(alpha = circle1Alpha),
                        shape = CircleShape
                    )
            )
        }
        
        // Main content
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            // Top Section - Profile Image and Post Button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Profile Image - Réduit et mieux intégré
                Surface(
                    modifier = Modifier
                        .size(36.dp)
                        .clickable { viewModel.openProfileDrawer() },
                    shape = CircleShape,
                    color = Color.White.copy(alpha = 0.2f),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, Color.White.copy(alpha = 0.3f))
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
                
                // Add Mission button for Recruiters - Style moderne avec gradient
                if (isRecruiter) {
                    val buttonInteractionSource = remember { MutableInteractionSource() }
                    val isButtonPressed by buttonInteractionSource.collectIsPressedAsState()
                    
                    val buttonScale by animateFloatAsState(
                        targetValue = if (isButtonPressed) 0.95f else 1f,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessMedium
                        ),
                        label = "button_scale"
                    )
                    
                    val buttonShadow by animateFloatAsState(
                        targetValue = if (isButtonPressed) 8f else 12f,
                        animationSpec = tween(durationMillis = 200),
                        label = "button_shadow"
                    )
                    
                    Box(
                        modifier = Modifier
                            .height(40.dp)
                            .scale(buttonScale)
                            .shadow(
                                elevation = buttonShadow.dp,
                                shape = RoundedCornerShape(20.dp),
                                spotColor = Color.Black.copy(alpha = 0.3f),
                                ambientColor = Color.Black.copy(alpha = 0.2f)
                            )
                            .clickable(
                                interactionSource = buttonInteractionSource,
                                indication = null
                            ) {
                                onAddMission()
                            }
                            .background(
                                brush = Brush.horizontalGradient(
                                    colors = listOf(
                                        Color(0xFF4A90E2),
                                        Color(0xFF61A5C2),
                                        Color(0xFF7DB8D6)
                                    )
                                ),
                                shape = RoundedCornerShape(20.dp)
                            )
                            .padding(horizontal = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Add,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp),
                                tint = Color.White
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Post a job",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = Color.White
                            )
                        }
                    }
                }
            }
            
            // Search Bar - Compacte, discrète avec ombre douce
            OutlinedTextField(
                value = searchText,
                onValueChange = { newText -> viewModel.updateSearchText(newText) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
                    .shadow(
                        elevation = 2.dp,
                        shape = RoundedCornerShape(24.dp),
                        spotColor = Color.Black.copy(alpha = 0.1f)
                    ),
                placeholder = {
                    Text(
                        text = "Search for jobs",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                        fontSize = 15.sp
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = "Search",
                        modifier = Modifier.size(20.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                },
                trailingIcon = {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Microphone icon for voice input
                        IconButton(
                            onClick = { startVoiceRecognition() },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Mic,
                                contentDescription = "Recherche vocale",
                                modifier = Modifier.size(18.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                        
                        // Clear icon (only show when there's text)
                        if (searchText.isNotEmpty()) {
                            IconButton(
                                onClick = { viewModel.updateSearchText("") },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Close,
                                    contentDescription = "Clear",
                                    modifier = Modifier.size(18.dp),
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                                )
                            }
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(24.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                ),
                textStyle = MaterialTheme.typography.bodyMedium.copy(fontSize = 15.sp),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = { /* Handle search */ })
            )
            
            // Tabs Section - Material 3 TabRow (Talent only) - Espaces réduits
            if (isTalent) {
                val tabs = listOf("Best Matches", "Most Recent", "Favorites")
                val selectedTabIndex = when (selectedTab) {
                    MissionTab.BEST_MATCHES -> 0
                    MissionTab.MOST_RECENT -> 1
                    MissionTab.FAVORITES -> 2
                }
                
                TabRow(
                    selectedTabIndex = selectedTabIndex,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    containerColor = Color.Transparent,
                    contentColor = MaterialTheme.colorScheme.primary,
                    indicator = { tabPositions ->
                        if (selectedTabIndex < tabPositions.size) {
                            TabRowDefaults.Indicator(
                                modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                                color = Color.White,
                                height = 2.5.dp
                            )
                        }
                    },
                    divider = {}
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTabIndex == index,
                            onClick = {
                                val newTab = when (index) {
                                    0 -> MissionTab.BEST_MATCHES
                                    1 -> MissionTab.MOST_RECENT
                                    2 -> MissionTab.FAVORITES
                                    else -> MissionTab.MOST_RECENT
                                }
                                viewModel.selectTab(newTab)
                                // Load favorites if switching to Favorites tab
                                if (newTab == MissionTab.FAVORITES) {
                                    viewModel.loadFavorites()
                                }
                            },
                            text = {
                                Text(
                                    text = title,
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = if (selectedTabIndex == index) {
                                        FontWeight.SemiBold
                                    } else {
                                        FontWeight.Normal
                                    },
                                    fontSize = 13.sp
                                )
                            },
                            selectedContentColor = Color.White,
                            unselectedContentColor = Color.White.copy(alpha = 0.7f)
                        )
                    }
                }
            }
            
            // Missions List
            val isLoadingList = isLoading || (isTalent && selectedTab == MissionTab.FAVORITES && isLoadingFavorites)
            when {
                isLoadingList && missions.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                missions.isEmpty() -> {
                    EmptyStateViewNew(
                        onAddMission = if (isRecruiter) onAddMission else null,
                        isTalent = isTalent,
                        isFavoritesTab = isTalent && selectedTab == MissionTab.FAVORITES
                    )
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(
                            horizontal = 16.dp,
                            vertical = 12.dp
                        )
                    ) {
                        items(missions.size, key = { missions[it].missionId }) { index ->
                            val mission = missions[index]
                            val isOwner = viewModel.isMissionOwner(mission)
                            
                            // Swipe to delete - only for recruiters who own the mission
                            if (isRecruiter && isOwner) {
                                SwipeToDeleteMission(
                                    mission = mission,
                                    onDelete = {
                                        missionToDelete = mission
                                        showDeleteDialog = true
                                    },
                                    onEdit = {
                                        onEditMission(mission)
                                    },
                                    onFavoriteToggle = { viewModel.toggleFavorite(mission) },
                                    isFavorite = viewModel.isFavorite(mission),
                                    onClick = { onMissionClick(mission) },
                                    isDialogOpen = showDeleteDialog && missionToDelete?.missionId == mission.missionId
                                )
                            } else {
                                MissionCardNew(
                                    mission = mission,
                                    isFavorite = viewModel.isFavorite(mission),
                                    onFavoriteToggle = { viewModel.toggleFavorite(mission) },
                                    onClick = {
                                        onMissionClick(mission)
                                    },
                                    onMenuClick = {},
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }
                }
            }
        }
        
        // Drawer Overlay
        AnimatedVisibility(
            visible = showDrawer,
            modifier = Modifier
                .fillMaxSize()
                .zIndex(1000f)
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                // Background overlay
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.3f))
                        .clickable { viewModel.closeProfileDrawer() }
                )
                
                // Drawer content sliding from left
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(drawerWidth)
                        .offset(x = drawerOffset.value.dp)
                        .zIndex(1001f)
                ) {
                    ProfileDrawer(
                        onClose = { viewModel.closeProfileDrawer() },
                        onMenuItemSelected = { itemType ->
                            viewModel.closeProfileDrawer()
                            onDrawerItemSelected(itemType)
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
        
        // Delete Confirmation Dialog
        if (showDeleteDialog && missionToDelete != null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .zIndex(2000f)
            ) {
                AlertDialog(
                    onDismissRequest = {
                        showDeleteDialog = false
                        missionToDelete = null
                    },
                title = {
                    Text(
                        text = "Supprimer la mission",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                text = {
                    Text(
                        text = "Êtes-vous sûr de vouloir supprimer la mission \"${missionToDelete?.title}\" ? Cette action est irréversible.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            missionToDelete?.let { mission ->
                                viewModel.deleteMission(mission)
                            }
                            showDeleteDialog = false
                            missionToDelete = null
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Text("Supprimer")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            showDeleteDialog = false
                            missionToDelete = null
                        }
                    ) {
                        Text("Annuler")
                    }
                },
                containerColor = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(20.dp)
                )
            }
        }
    }
}


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SwipeToDeleteMission(
    mission: Mission,
    onDelete: () -> Unit,
    onEdit: () -> Unit,
    onFavoriteToggle: () -> Unit,
    isFavorite: Boolean,
    onClick: () -> Unit,
    isDialogOpen: Boolean
) {
    // Use mission ID and dialog state as key to force recreation
    key(mission.missionId, isDialogOpen) {
        // Always start with Default state to prevent persistent red background
        val dismissState = rememberDismissState(
            initialValue = DismissValue.Default
        )
        
        // Detect swipe and show dialog
        LaunchedEffect(dismissState.currentValue) {
            if (dismissState.currentValue == DismissValue.DismissedToEnd) {
                // Show confirmation dialog
                onDelete()
            }
        }
        
        SwipeToDismiss(
            state = dismissState,
            directions = setOf(DismissDirection.EndToStart), // Swipe left to delete
            background = {
                // Only show background when swiping in the correct direction
                val backgroundColor = if (dismissState.dismissDirection == DismissDirection.EndToStart) {
                    MaterialTheme.colorScheme.error
                } else {
                    Color.Transparent
                }
                
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(backgroundColor)
                        .clickable { onDelete() },
                    contentAlignment = Alignment.CenterEnd
                ) {
                    // Only show icon when swiping
                    if (dismissState.dismissDirection == DismissDirection.EndToStart) {
                        Icon(
                            imageVector = Icons.Filled.Delete,
                            contentDescription = "Supprimer",
                            tint = Color.White,
                            modifier = Modifier
                                .size(48.dp)
                                .padding(end = 24.dp)
                        )
                    }
                }
            },
            dismissContent = {
                MissionCardNew(
                    mission = mission,
                    isFavorite = isFavorite,
                    onFavoriteToggle = onFavoriteToggle,
                    onClick = onClick,
                    onMenuClick = onEdit,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        )
    }
}


@Composable
fun EmptyStateViewNew(
    onAddMission: (() -> Unit)?,
    isTalent: Boolean = false,
    isFavoritesTab: Boolean = false
) {
    Column(
        modifier = Modifier
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
    }
}

