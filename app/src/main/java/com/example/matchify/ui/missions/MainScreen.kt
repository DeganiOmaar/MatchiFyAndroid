package com.example.matchify.ui.missions

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.matchify.domain.model.Mission
import com.example.matchify.ui.missions.add.MissionAddScreen
import com.example.matchify.ui.missions.add.MissionAddViewModel
import com.example.matchify.ui.missions.add.MissionAddViewModelFactory
import com.example.matchify.ui.missions.edit.MissionEditScreen
import com.example.matchify.ui.missions.edit.MissionEditViewModel
import com.example.matchify.ui.missions.edit.MissionEditViewModelFactory
import com.example.matchify.ui.missions.list.MissionListScreenNew
import com.example.matchify.ui.missions.list.MissionListViewModel
import com.example.matchify.ui.missions.list.MissionListViewModelFactory
import com.example.matchify.ui.missions.navigation.MainBottomNavigation
import com.example.matchify.ui.recruiter.profile.RecruiterProfileScreen
import com.example.matchify.ui.recruiter.profile.RecruiterProfileViewModel
import com.example.matchify.ui.recruiter.profile.RecruiterProfileViewModelFactory
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.matchify.ui.recruiter.edit.EditRecruiterProfileScreen
import com.example.matchify.ui.recruiter.edit.EditRecruiterProfileViewModel
import com.example.matchify.ui.recruiter.edit.EditRecruiterProfileViewModelFactory
import com.example.matchify.ui.talent.profile.TalentProfileScreen
import com.example.matchify.ui.talent.profile.TalentProfileViewModel
import com.example.matchify.ui.talent.profile.TalentProfileViewModelFactory
import com.example.matchify.ui.talent.profile.AIProfileAnalysisScreen
import com.example.matchify.ui.talent.edit.EditTalentProfileScreen
import com.example.matchify.ui.talent.edit.EditTalentProfileViewModel
import com.example.matchify.ui.talent.edit.EditTalentProfileViewModelFactory
import com.example.matchify.data.local.AuthPreferencesProvider
import com.example.matchify.ui.missions.components.DrawerMenuItemType
import com.google.gson.Gson
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import com.example.matchify.ui.stats.MyStatsScreen
import com.example.matchify.ui.stats.MyStatsViewModel
import com.example.matchify.ui.stats.MyStatsViewModelFactory
import androidx.compose.runtime.LaunchedEffect
import com.example.matchify.domain.session.AuthSessionManager
import android.util.Log
import androidx.compose.ui.Alignment

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    navController: NavHostController = rememberNavController(),
    onOpenSettings: () -> Unit = {},
    onLogout: () -> Unit = {}
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    
    // Get user role
    val context = LocalContext.current
    val prefs = remember { AuthPreferencesProvider.getInstance().get() }
    val userRole by prefs.role.collectAsState(initial = "recruiter")
    val authSessionManager = remember { AuthSessionManager.getInstance() }
    
    // Determine profile route based on role
    val profileRoute = if (userRole == "talent") "talent_profile" else "recruiter_profile"

    // ViewModel de profil partagé pour le recruteur dans tout le NavHost interne.
    // Cela permet de garder les mêmes données entre l'écran profil et l'écran d'édition,
    // et de rafraîchir facilement le profil après une mise à jour.
    val recruiterProfileViewModel: RecruiterProfileViewModel = viewModel(
        factory = RecruiterProfileViewModelFactory()
    )

    // Scroll-to-top state for each screen
    var scrollToTopKey by remember { mutableStateOf(0) }
    
    Scaffold(
        bottomBar = {
            // Show bottom bar on main screens (Missions, Proposals, Alerts, Messages)
            if (currentRoute in listOf("missions_list", "proposals_list", "alerts_list", "messages_list")) {
                MainBottomNavigation(
                    currentRoute = currentRoute,
                    onNavigate = { route ->
                        navController.navigate(route) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onScrollToTop = { route ->
                        // Trigger scroll to top by updating key
                        scrollToTopKey++
                    }
                )
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = "missions_list",
            modifier = Modifier.padding(paddingValues)
        ) {
            composable("missions_list") {
                val listViewModel: MissionListViewModel = viewModel(
                    factory = MissionListViewModelFactory()
                )
                val drawerNavItem by listViewModel.drawerNavigationItem.collectAsState()
                val scope = rememberCoroutineScope()
                val prefs = remember { AuthPreferencesProvider.getInstance().get() }
                
                LaunchedEffect(drawerNavItem) {
                    drawerNavItem?.let { itemType ->
                        when (itemType) {
                            DrawerMenuItemType.PROFILE -> {
                                val profileRoute = if (userRole == "talent") "talent_profile" else "recruiter_profile"
                                navController.navigate(profileRoute) {
                                    popUpTo("missions_list") { saveState = true }
                                    launchSingleTop = true
                                }
                            }
                            DrawerMenuItemType.MY_STATS -> {
                                navController.navigate("my_stats") {
                                    popUpTo("missions_list") { saveState = true }
                                    launchSingleTop = true
                                }
                            }
                            DrawerMenuItemType.CREATE_OFFER -> {
                                navController.navigate("category_selection") {
                                    popUpTo("missions_list") { saveState = true }
                                    launchSingleTop = true
                                }
                            }
                            DrawerMenuItemType.CREATE_MISSION -> {
                                navController.navigate("mission_add") {
                                    popUpTo("missions_list") { saveState = true }
                                    launchSingleTop = true
                                }
                            }
                            DrawerMenuItemType.MY_OFFERS -> {
                                navController.navigate("my_offers") {
                                    popUpTo("missions_list") { saveState = true }
                                    launchSingleTop = true
                                }
                            }
                            DrawerMenuItemType.BROWSE_OFFERS -> {
                                navController.navigate("browse_offers") {
                                    popUpTo("missions_list") { saveState = true }
                                    launchSingleTop = true
                                }
                            }
                            DrawerMenuItemType.INTERVIEWS -> {
                                navController.navigate("interviews_list") {
                                    popUpTo("missions_list") { saveState = true }
                                    launchSingleTop = true
                                }
                            }
                            DrawerMenuItemType.FAVORITE_TALENTS -> {
                                navController.navigate("favorite_talents") {
                                    popUpTo("missions_list") { saveState = true }
                                    launchSingleTop = true
                                }
                            }
                            DrawerMenuItemType.SETTINGS -> {
                                onOpenSettings()
                            }
                            DrawerMenuItemType.THEME -> {
                                navController.navigate("theme") {
                                    popUpTo("missions_list") { saveState = true }
                                    launchSingleTop = true
                                }
                            }
                            DrawerMenuItemType.CHAT_BOT -> {
                                navController.navigate("chatbot") {
                                    popUpTo("missions_list") { saveState = true }
                                    launchSingleTop = true
                                }
                            }
                            DrawerMenuItemType.LOG_OUT -> {
                                scope.launch {
                                    val result = runCatching { authSessionManager.logout() }
                                    if (result.isFailure) {
                                        Log.e("MainScreen", "Logout from drawer failed", result.exceptionOrNull())
                                    }
                                    onLogout()
                                }
                            }
                        }
                        listViewModel.clearDrawerNavigationItem()
                    }
                }
                
                MissionListScreenNew(
                    onAddMission = {
                        navController.navigate("mission_add")
                    },
                    onEditMission = { mission ->
                        val missionJson = Gson().toJson(mission)
                        navController.navigate("mission_edit/$missionJson")
                    },
                    onMissionClick = { mission ->
                        navController.navigate("mission_details/${mission.missionId}")
                    },
                    onTalentProfileClick = { talentId ->
                        if (talentId == "search_all") {
                            navController.navigate("talent_search")
                        } else {
                            navController.navigate("talent_profile_by_id/$talentId")
                        }
                    },
                    onDrawerItemSelected = { itemType ->
                        listViewModel.setDrawerNavigationItem(itemType)
                    },
                    onNavigateToAlerts = {
                        navController.navigate("alerts_list") {
                            popUpTo("missions_list") { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    viewModel = listViewModel
                )
            }

            composable("mission_add") {
                val addViewModel: MissionAddViewModel = viewModel(
                    factory = MissionAddViewModelFactory()
                )
                MissionAddScreen(
                    onBack = { navController.popBackStack() },
                    onMissionCreated = {
                        // La mission sera rechargée automatiquement via le realtime client
                        // ou lors du retour sur l'écran de liste
                        navController.popBackStack()
                    },
                    viewModel = addViewModel
                )
            }

            composable("mission_edit/{missionJson}") { backStackEntry ->
                val missionJson = backStackEntry.arguments?.getString("missionJson") ?: ""
                val mission = try {
                    Gson().fromJson(missionJson, Mission::class.java)
                } catch (e: Exception) {
                    null
                }

                mission?.let { missionData ->
                    val editViewModel: MissionEditViewModel = viewModel(
                        factory = MissionEditViewModelFactory(missionData)
                    )
                    MissionEditScreen(
                        mission = missionData,
                        onBack = { navController.popBackStack() },
                        onMissionUpdated = {
                            navController.popBackStack()
                        },
                        viewModel = editViewModel
                    )
                } ?: run {
                    // Error state - go back
                    navController.popBackStack()
                }
            }

            composable("recruiter_profile") {
                RecruiterProfileScreen(
                    viewModel = recruiterProfileViewModel,
                    onEditProfile = {
                        navController.navigate("edit_recruiter_profile")
                    },
                    onSettings = onOpenSettings,
                    onBack = {
                        navController.popBackStack()
                    }
                )
            }
            
            composable("talent_profile") {
                val profileViewModel: TalentProfileViewModel = viewModel(
                    factory = TalentProfileViewModelFactory()
                )
                
                // Refresh projects when screen appears
                LaunchedEffect(Unit) {
                    profileViewModel.loadProjects()
                }
                
                TalentProfileScreen(
                    viewModel = profileViewModel,
                    onEditProfile = {
                        navController.navigate("edit_talent_profile")
                    },
                    onSettings = onOpenSettings,
                    onBack = {
                        navController.popBackStack()
                    },
                    onProjectClick = { project ->
                        // Use actual ID (_id or id), not projectId which can be a random UUID
                        val actualId = project.id ?: project.id_alt ?: ""
                        if (actualId.isNotEmpty()) {
                            navController.navigate("project_details/$actualId")
                        }
                    },
                    onAddProject = {
                        navController.navigate("add_project")
                    },
                    onAnalyzeProfile = {
                        navController.navigate("ai_profile_analysis")
                    }
                )
            }
            
            composable("edit_recruiter_profile") {
                val context = LocalContext.current
                val user by recruiterProfileViewModel.user.collectAsState()
                
                val editViewModel: EditRecruiterProfileViewModel = viewModel(
                    factory = EditRecruiterProfileViewModelFactory(context)
                )
                
                // Load initial data when user is available
                LaunchedEffect(user) {
                    user?.let { editViewModel.loadInitial(it) }
                }
                
                EditRecruiterProfileScreen(
                    viewModel = editViewModel,
                    onBack = {
                        recruiterProfileViewModel.refreshProfile()
                        navController.popBackStack()
                    }
                )
            }
            
            composable("edit_talent_profile") {
                val context = LocalContext.current
                val profileViewModel: TalentProfileViewModel = viewModel(
                    factory = TalentProfileViewModelFactory()
                )
                val user by profileViewModel.user.collectAsState()
                
                val editViewModel: EditTalentProfileViewModel = viewModel(
                    factory = EditTalentProfileViewModelFactory(context)
                )
                
                // Load initial data when user is available
                LaunchedEffect(user) {
                    user?.let { editViewModel.loadInitial(it) }
                }
                
                EditTalentProfileScreen(
                    viewModel = editViewModel,
                    onBack = {
                        profileViewModel.refreshProfile()
                        navController.popBackStack()
                    }
                )
            }
            
            composable("ai_profile_analysis") {
                AIProfileAnalysisScreen(
                    onBack = { navController.popBackStack() }
                )
            }
            
            // Écran de test pour valider le modèle IA
            composable("test_talent_filtering") {
                com.example.matchify.ui.talent.filtering.test.TalentFilteringTestScreen(
                    onBack = { navController.popBackStack() }
                )
            }
            
            composable("proposals_list") {
                val scope = rememberCoroutineScope()
                val proposalsViewModel: com.example.matchify.ui.proposals.ProposalsViewModel = viewModel(
                    factory = com.example.matchify.ui.proposals.ProposalsViewModelFactory()
                )
                
                // Rafraîchir les proposals quand on revient sur cette page
                LaunchedEffect(currentRoute) {
                    if (currentRoute == "proposals_list") {
                        proposalsViewModel.loadProposals()
                    }
                }
                
                com.example.matchify.ui.proposals.ProposalsScreen(
                    onProposalClick = { proposalId ->
                        navController.navigate("proposal_details/$proposalId")
                    },
                    viewModel = proposalsViewModel,
                    onDrawerItemSelected = { itemType ->
                        when (itemType) {
                            DrawerMenuItemType.PROFILE -> {
                                val profileRoute = if (userRole == "talent") "talent_profile" else "recruiter_profile"
                                navController.navigate(profileRoute) {
                                    popUpTo("proposals_list") { saveState = true }
                                    launchSingleTop = true
                                }
                            }
                            DrawerMenuItemType.MY_STATS -> {
                                navController.navigate("my_stats") {
                                    popUpTo("proposals_list") { saveState = true }
                                    launchSingleTop = true
                                }
                            }
                            DrawerMenuItemType.CREATE_OFFER -> {
                                navController.navigate("category_selection") {
                                    popUpTo("proposals_list") { saveState = true }
                                    launchSingleTop = true
                                }
                            }
                            DrawerMenuItemType.CREATE_MISSION -> {
                                navController.navigate("mission_add") {
                                    popUpTo("proposals_list") { saveState = true }
                                    launchSingleTop = true
                                }
                            }
                            DrawerMenuItemType.MY_OFFERS -> {
                                navController.navigate("my_offers") {
                                    popUpTo("proposals_list") { saveState = true }
                                    launchSingleTop = true
                                }
                            }
                            DrawerMenuItemType.BROWSE_OFFERS -> {
                                navController.navigate("browse_offers") {
                                    popUpTo("proposals_list") { saveState = true }
                                    launchSingleTop = true
                                }
                            }
                            DrawerMenuItemType.INTERVIEWS -> {
                                navController.navigate("interviews_list") {
                                    popUpTo("proposals_list") { saveState = true }
                                    launchSingleTop = true
                                }
                            }
                            DrawerMenuItemType.FAVORITE_TALENTS -> {
                                navController.navigate("favorite_talents") {
                                    popUpTo("proposals_list") { saveState = true }
                                    launchSingleTop = true
                                }
                            }
                            DrawerMenuItemType.SETTINGS -> {
                                onOpenSettings()
                            }
                            DrawerMenuItemType.THEME -> {
                                navController.navigate("theme") {
                                    popUpTo("proposals_list") { saveState = true }
                                    launchSingleTop = true
                                }
                            }
                            DrawerMenuItemType.CHAT_BOT -> {
                                navController.navigate("chatbot") {
                                    popUpTo("proposals_list") { saveState = true }
                                    launchSingleTop = true
                                }
                            }
                            DrawerMenuItemType.LOG_OUT -> {
                                scope.launch {
                                    val result = runCatching { authSessionManager.logout() }
                                    if (result.isFailure) {
                                        Log.e("MainScreen", "Logout from drawer failed", result.exceptionOrNull())
                                    }
                                    onLogout()
                                }
                            }
                        }
                    }
                )
            }
            
            composable("alerts_list") {
                val scope = rememberCoroutineScope()
                com.example.matchify.ui.alerts.AlertsScreen(
                    onAlertClick = { proposalId ->
                        navController.navigate("proposal_details/$proposalId")
                    },
                    onDrawerItemSelected = { itemType ->
                        when (itemType) {
                            DrawerMenuItemType.PROFILE -> {
                                val profileRoute = if (userRole == "talent") "talent_profile" else "recruiter_profile"
                                navController.navigate(profileRoute) {
                                    popUpTo("alerts_list") { saveState = true }
                                    launchSingleTop = true
                                }
                            }
                            DrawerMenuItemType.MY_STATS -> {
                                navController.navigate("my_stats") {
                                    popUpTo("alerts_list") { saveState = true }
                                    launchSingleTop = true
                                }
                            }
                            DrawerMenuItemType.CREATE_OFFER -> {
                                navController.navigate("category_selection") {
                                    popUpTo("alerts_list") { saveState = true }
                                    launchSingleTop = true
                                }
                            }
                            DrawerMenuItemType.CREATE_MISSION -> {
                                navController.navigate("mission_add") {
                                    popUpTo("alerts_list") { saveState = true }
                                    launchSingleTop = true
                                }
                            }
                            DrawerMenuItemType.MY_OFFERS -> {
                                navController.navigate("my_offers") {
                                    popUpTo("alerts_list") { saveState = true }
                                    launchSingleTop = true
                                }
                            }
                            DrawerMenuItemType.BROWSE_OFFERS -> {
                                navController.navigate("browse_offers") {
                                    popUpTo("alerts_list") { saveState = true }
                                    launchSingleTop = true
                                }
                            }
                            DrawerMenuItemType.INTERVIEWS -> {
                                navController.navigate("interviews_list") {
                                    popUpTo("alerts_list") { saveState = true }
                                    launchSingleTop = true
                                }
                            }
                            DrawerMenuItemType.FAVORITE_TALENTS -> {
                                navController.navigate("favorite_talents") {
                                    popUpTo("alerts_list") { saveState = true }
                                    launchSingleTop = true
                                }
                            }
                            DrawerMenuItemType.SETTINGS -> {
                                onOpenSettings()
                            }
                            DrawerMenuItemType.THEME -> {
                                navController.navigate("theme") {
                                    popUpTo("alerts_list") { saveState = true }
                                    launchSingleTop = true
                                }
                            }
                            DrawerMenuItemType.CHAT_BOT -> {
                                navController.navigate("chatbot") {
                                    popUpTo("alerts_list") { saveState = true }
                                    launchSingleTop = true
                                }
                            }
                            DrawerMenuItemType.LOG_OUT -> {
                                scope.launch {
                                    val result = runCatching { authSessionManager.logout() }
                                    if (result.isFailure) {
                                        Log.e("MainScreen", "Logout from drawer failed", result.exceptionOrNull())
                                    }
                                    onLogout()
                                }
                            }
                        }
                    }
                )
            }
            
            composable("proposal_details/{proposalId}") { backStackEntry ->
                val proposalId = backStackEntry.arguments?.getString("proposalId") ?: ""
                com.example.matchify.ui.proposals.details.ProposalDetailsScreen(
                    proposalId = proposalId,
                    onBack = { navController.popBackStack() },
                    onTalentProfileClick = { talentId ->
                        navController.navigate("talent_profile_view/$talentId")
                    },
                    onConversationClick = { conversationId ->
                        navController.navigate("conversation_chat/$conversationId")
                    },
                    onScheduleInterview = { proposalId ->
                        navController.navigate("create_interview/$proposalId")
                    }
                )
            }
            
            composable("create_interview/{proposalId}") { backStackEntry ->
                val proposalId = backStackEntry.arguments?.getString("proposalId") ?: ""
                com.example.matchify.ui.interviews.CreateInterviewScreen(
                    proposalId = proposalId,
                    onBack = { navController.popBackStack() },
                    onSuccess = {
                        // Optionally navigate to interviews list after creation
                        // navController.navigate("interviews_list")
                    }
                )
            }
            
            composable("interviews_list") {
                com.example.matchify.ui.interviews.InterviewsListScreen(
                    onBack = { navController.popBackStack() },
                    onInterviewClick = { interviewId ->
                        // Navigate to interview details if needed
                        // navController.navigate("interview_details/$interviewId")
                    }
                )
            }
            
            composable("rating/{talentId}/{talentName}/{missionId}") { backStackEntry ->
                val talentId = backStackEntry.arguments?.getString("talentId") ?: ""
                val talentName = backStackEntry.arguments?.getString("talentName") ?: ""
                val missionId = backStackEntry.arguments?.getString("missionId") ?: ""
                com.example.matchify.ui.ratings.RatingScreen(
                    talentId = talentId,
                    talentName = if (talentName == "null") null else talentName,
                    missionId = if (missionId == "null") null else missionId,
                    onBack = { navController.popBackStack() }
                )
            }
            
            composable("rating/{talentId}") { backStackEntry ->
                val talentId = backStackEntry.arguments?.getString("talentId") ?: ""
                com.example.matchify.ui.ratings.RatingScreen(
                    talentId = talentId,
                    talentName = null,
                    missionId = null,
                    onBack = { navController.popBackStack() }
                )
            }
            
            composable("messages_list") {
                // Shared ViewModel for MessagesScreen to maintain state and refresh properly
                val messagesViewModel: com.example.matchify.ui.messages.MessagesViewModel = viewModel(
                    factory = com.example.matchify.ui.messages.MessagesViewModelFactory()
                )
                val badgeCountViewModel: com.example.matchify.ui.alerts.BadgeCountViewModel = viewModel(
                    factory = com.example.matchify.ui.alerts.BadgeCountViewModelFactory()
                )
                
                // Refresh conversations and badge counts when returning to this screen
                LaunchedEffect(currentRoute) {
                    if (currentRoute == "messages_list") {
                        messagesViewModel.loadConversations()
                        badgeCountViewModel.loadCounts()
                    }
                }
                
                val scope = rememberCoroutineScope()
                com.example.matchify.ui.messages.MessagesScreen(
                    onConversationClick = { conversationId ->
                        navController.navigate("conversation_chat/$conversationId")
                    },
                    onDrawerItemSelected = { itemType ->
                        when (itemType) {
                            DrawerMenuItemType.PROFILE -> {
                                val profileRoute = if (userRole == "talent") "talent_profile" else "recruiter_profile"
                                navController.navigate(profileRoute) {
                                    popUpTo("messages_list") { saveState = true }
                                    launchSingleTop = true
                                }
                            }
                            DrawerMenuItemType.MY_STATS -> {
                                navController.navigate("my_stats") {
                                    popUpTo("messages_list") { saveState = true }
                                    launchSingleTop = true
                                }
                            }
                            DrawerMenuItemType.CREATE_OFFER -> {
                                navController.navigate("category_selection") {
                                    popUpTo("messages_list") { saveState = true }
                                    launchSingleTop = true
                                }
                            }
                            DrawerMenuItemType.CREATE_MISSION -> {
                                navController.navigate("mission_add") {
                                    popUpTo("messages_list") { saveState = true }
                                    launchSingleTop = true
                                }
                            }
                            DrawerMenuItemType.MY_OFFERS -> {
                                navController.navigate("my_offers") {
                                    popUpTo("messages_list") { saveState = true }
                                    launchSingleTop = true
                                }
                            }
                            DrawerMenuItemType.BROWSE_OFFERS -> {
                                navController.navigate("browse_offers") {
                                    popUpTo("messages_list") { saveState = true }
                                    launchSingleTop = true
                                }
                            }
                            DrawerMenuItemType.INTERVIEWS -> {
                                navController.navigate("interviews_list") {
                                    popUpTo("messages_list") { saveState = true }
                                    launchSingleTop = true
                                }
                            }
                            DrawerMenuItemType.FAVORITE_TALENTS -> {
                                navController.navigate("favorite_talents") {
                                    popUpTo("messages_list") { saveState = true }
                                    launchSingleTop = true
                                }
                            }
                            DrawerMenuItemType.SETTINGS -> {
                                onOpenSettings()
                            }
                            DrawerMenuItemType.THEME -> {
                                navController.navigate("theme") {
                                    popUpTo("messages_list") { saveState = true }
                                    launchSingleTop = true
                                }
                            }
                            DrawerMenuItemType.CHAT_BOT -> {
                                navController.navigate("chatbot") {
                                    popUpTo("messages_list") { saveState = true }
                                    launchSingleTop = true
                                }
                            }
                            DrawerMenuItemType.LOG_OUT -> {
                                scope.launch {
                                    val result = runCatching { authSessionManager.logout() }
                                    if (result.isFailure) {
                                        Log.e("MainScreen", "Logout from drawer failed", result.exceptionOrNull())
                                    }
                                    onLogout()
                                }
                            }
                        }
                    },
                    viewModel = messagesViewModel
                )
            }
            
            composable("conversation_chat/{conversationId}") { backStackEntry ->
                val conversationId = backStackEntry.arguments?.getString("conversationId") ?: ""
                
                // Load conversation to get missionId and talentId for contract creation
                val conversationViewModel = androidx.lifecycle.viewmodel.compose.viewModel<com.example.matchify.ui.conversations.ConversationChatViewModel>(
                    factory = com.example.matchify.ui.conversations.ConversationChatViewModelFactory(conversationId)
                )
                val conversation by conversationViewModel.conversation.collectAsState()
                
                LaunchedEffect(Unit) {
                    conversationViewModel.loadConversation()
                }
                
                com.example.matchify.ui.conversations.ConversationChatScreen(
                    conversationId = conversationId,
                    onBack = { navController.popBackStack() },
                    onCreateContractClick = {
                        // Navigate to create contract screen with missionId and talentId from conversation
                        // Recruiter creates contract, so we need the talentId (not recruiterId)
                        val missionId = conversation?.missionId ?: ""
                        val talentId = conversation?.talentId ?: "" // Always use talentId for contract creation
                        if (missionId.isNotEmpty() && talentId.isNotEmpty()) {
                            // Save conversationId in savedStateHandle for reload after contract creation
                            navController.currentBackStackEntry?.savedStateHandle?.set("conversationId", conversationId)
                            navController.navigate("create_contract/$missionId/$talentId") {
                                launchSingleTop = true
                            }
                        }
                    },
                    onContractReview = { contractId ->
                        // Navigate based on role: recruiter sees detail, talent sees review
                        if (conversationViewModel.isRecruiter) {
                            navController.navigate("contract_detail/$contractId") {
                                launchSingleTop = true
                            }
                        } else {
                            navController.navigate("contract_review/$contractId") {
                                launchSingleTop = true
                            }
                        }
                    },
                    viewModel = conversationViewModel
                )
            }
            
            composable("talents_filter/{missionId}") { backStackEntry ->
                val missionId = backStackEntry.arguments?.getString("missionId") ?: ""
                com.example.matchify.ui.talent.filtering.TalentFilteringScreen(
                    missionId = missionId,
                    onTalentClick = { talentId ->
                        navController.navigate("talent_profile_by_id/$talentId") {
                            popUpTo("talents_filter/$missionId") { saveState = true }
                            launchSingleTop = true
                        }
                    },
                    onBack = { navController.popBackStack() }
                )
            }
            
            composable("mission_details/{missionId}") { backStackEntry ->
                val missionId = backStackEntry.arguments?.getString("missionId") ?: ""
                com.example.matchify.ui.missions.details.MissionDetailsScreen(
                    missionId = missionId,
                    onBack = { navController.popBackStack() },
                    onCreateProposal = { missionId, missionTitle ->
                        navController.navigate("create_proposal/$missionId/$missionTitle")
                    }
                )
            }
            
            composable("create_proposal/{missionId}/{missionTitle}") { backStackEntry ->
                val missionId = backStackEntry.arguments?.getString("missionId") ?: ""
                val missionTitle = backStackEntry.arguments?.getString("missionTitle") ?: ""
                    .replace("%20", " ")
                    .replace("%2F", "/")
                com.example.matchify.ui.proposals.create.CreateProposalScreen(
                    missionId = missionId,
                    missionTitle = missionTitle,
                    onBack = { navController.popBackStack() },
                    onSuccess = {
                        navController.popBackStack(route = "mission_details/$missionId", inclusive = false)
                    }
                )
            }
            
            composable("my_stats") {
                val statsViewModel: MyStatsViewModel = viewModel(factory = MyStatsViewModelFactory())
                MyStatsScreen(
                    onBack = { navController.popBackStack() },
                    viewModel = statsViewModel
                )
            }
            
            // Settings is now handled by the parent AppNavGraph
            // composable("settings") { ... } removed to avoid nesting issues
            
            composable("add_project") {
                val context = androidx.compose.ui.platform.LocalContext.current
                
                com.example.matchify.ui.portfolio.add.AddEditProjectScreen(
                    project = null,
                    onBack = { navController.popBackStack() },
                    onProjectSaved = {
                        navController.popBackStack()
                    },
                    viewModel = androidx.lifecycle.viewmodel.compose.viewModel(
                        factory = com.example.matchify.ui.portfolio.add.AddEditProjectViewModelFactory(
                            project = null,
                            context = context
                        )
                    )
                )
            }
            
            composable("edit_project/{projectId}") { backStackEntry ->
                val projectId = backStackEntry.arguments?.getString("projectId") ?: ""
                val context = androidx.compose.ui.platform.LocalContext.current
                
                // Load project ViewModel
                val projectDetailsViewModel: com.example.matchify.ui.portfolio.details.ProjectDetailsViewModel = 
                    androidx.lifecycle.viewmodel.compose.viewModel(
                        factory = com.example.matchify.ui.portfolio.details.ProjectDetailsViewModelFactory(projectId)
                    )
                val project by projectDetailsViewModel.project.collectAsState()
                
                // Show AddEditProjectScreen when project is loaded
                if (project != null) {
                    com.example.matchify.ui.portfolio.add.AddEditProjectScreen(
                        project = project,
                        onBack = { navController.popBackStack() },
                        onProjectSaved = {
                            navController.popBackStack()
                        },
                        viewModel = androidx.lifecycle.viewmodel.compose.viewModel(
                            factory = com.example.matchify.ui.portfolio.add.AddEditProjectViewModelFactory(
                                project = project,
                                context = context
                            )
                        )
                    )
                } else {
                    // Show loading while project is being loaded
                    androidx.compose.material3.Scaffold(
                        topBar = {
                            androidx.compose.material3.TopAppBar(
                                title = {
                                    androidx.compose.material3.Text("Edit Project")
                                },
                                navigationIcon = {
                                    androidx.compose.material3.IconButton(onClick = { navController.popBackStack() }) {
                                        androidx.compose.material3.Icon(
                                            imageVector = Icons.Default.ArrowBack,
                                            contentDescription = "Back"
                                        )
                                    }
                                }
                            )
                        }
                    ) { paddingValues ->
                        Box(
                            modifier = androidx.compose.ui.Modifier
                                .fillMaxSize()
                                .padding(paddingValues),
                            contentAlignment = androidx.compose.ui.Alignment.Center
                        ) {
                            androidx.compose.material3.CircularProgressIndicator()
                        }
                    }
                }
            }
            
            composable("project_details/{projectId}") { backStackEntry ->
                val projectId = backStackEntry.arguments?.getString("projectId") ?: ""
                com.example.matchify.ui.portfolio.details.ProjectDetailsScreen(
                    projectId = projectId,
                    onBack = { navController.popBackStack() },
                    onEditProject = { project ->
                        // Use actual ID for edit route
                        val actualId = project.id ?: project.id_alt ?: project.projectId
                        navController.navigate("edit_project/$actualId")
                    },
                    onDeleteProject = {
                        // Project deleted - refresh portfolio list
                        // This will be handled by reloading the profile/portfolio data
                    },
                    navController = navController
                )
            }
            
            composable("talent_profile_view/{talentId}") { backStackEntry ->
                val talentId = backStackEntry.arguments?.getString("talentId") ?: ""
                com.example.matchify.ui.talent.profilebyid.TalentProfileByIDScreen(
                    talentId = talentId,
                    onBack = { navController.popBackStack() },
                    onRateClick = { id, name ->
                        navController.navigate("rating/$id")
                    }
                )
            }
            
            composable("talent_profile_by_id/{talentId}") { backStackEntry ->
                val talentId = backStackEntry.arguments?.getString("talentId") ?: ""
                com.example.matchify.ui.talent.profilebyid.TalentProfileByIDScreen(
                    talentId = talentId,
                    onBack = { navController.popBackStack() },
                    onRateClick = { id, name ->
                        navController.navigate("rating/$id")
                    }
                )
            }
            
            composable("talent_search") {
                com.example.matchify.ui.talent.search.TalentSearchScreen(
                    onBack = { navController.popBackStack() },
                    onTalentClick = { talentId ->
                        navController.navigate("talent_profile_by_id/$talentId")
                    }
                )
            }
            
            composable("theme") {
                com.example.matchify.ui.theme.ThemeScreen(
                    onBack = { navController.popBackStack() }
                )
            }
            
            composable("chatbot") {
                com.example.matchify.ui.chatbot.ChatBotScreen(
                    onBack = { navController.popBackStack() }
                )
            }
            
            composable("favorite_talents") {
                com.example.matchify.ui.talent.favorites.FavoriteTalentsScreen(
                    onBack = { navController.popBackStack() },
                    onTalentClick = { talentId ->
                        navController.navigate("talent_profile_view/$talentId")
                    }
                )
            }
            
            // Offers
            composable("category_selection") {
                com.example.matchify.ui.offers.category.CategorySelectionScreen(
                    onBack = { navController.popBackStack() },
                    onCategorySelected = { category ->
                        val categoryEnum = com.example.matchify.domain.model.OfferCategory.values()
                            .firstOrNull { it.displayName == category }
                        if (categoryEnum != null) {
                            navController.navigate("create_offer/${categoryEnum.name}")
                        }
                    }
                )
            }
            
            composable("create_offer/{category}") { backStackEntry ->
                val categoryName = backStackEntry.arguments?.getString("category") ?: ""
                val category = try {
                    com.example.matchify.domain.model.OfferCategory.valueOf(categoryName)
                } catch (e: Exception) {
                    com.example.matchify.domain.model.OfferCategory.DEVELOPMENT
                }
                
                com.example.matchify.ui.offers.create.CreateOfferScreen(
                    category = category,
                    onBack = { navController.popBackStack() },
                    onOfferCreated = {
                        navController.popBackStack(route = "missions_list", inclusive = false)
                    }
                )
            }
            
            composable("my_offers") {
                com.example.matchify.ui.offers.myoffers.MyOffersScreen(
                    onBack = { navController.popBackStack() },
                    onOfferClick = { offerId ->
                        navController.navigate("edit_offer/$offerId")
                    }
                )
            }
            
            composable("edit_offer/{offerId}") { backStackEntry ->
                val offerId = backStackEntry.arguments?.getString("offerId") ?: ""
                // Load offer and show edit screen
                val viewModel: com.example.matchify.ui.offers.myoffers.MyOffersViewModel = 
                    androidx.lifecycle.viewmodel.compose.viewModel(
                        factory = com.example.matchify.ui.offers.myoffers.MyOffersViewModelFactory()
                    )
                
                LaunchedEffect(Unit) {
                    viewModel.loadMyOffers()
                }
                
                val offer = viewModel.offers.firstOrNull { it.id == offerId }
                
                if (offer != null) {
                    com.example.matchify.ui.offers.edit.EditOfferScreen(
                        offer = offer,
                        onBack = { navController.popBackStack() },
                        onOfferUpdated = {
                            navController.popBackStack()
                        }
                    )
                } else {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }
            
            composable("browse_offers") {
                com.example.matchify.ui.offers.browse.BrowseOffersScreen(
                    onBack = { navController.popBackStack() },
                    onOfferClick = { offerId ->
                        navController.navigate("offer_details/$offerId")
                    }
                )
            }
            
            composable("offer_details/{offerId}") { backStackEntry ->
                val offerId = backStackEntry.arguments?.getString("offerId") ?: ""
                // Load offer and show details
                val viewModel: com.example.matchify.ui.offers.browse.BrowseOffersViewModel = 
                    androidx.lifecycle.viewmodel.compose.viewModel(
                        factory = com.example.matchify.ui.offers.browse.BrowseOffersViewModelFactory()
                    )
                
                LaunchedEffect(Unit) {
                    viewModel.loadOffers()
                }
                
                val offer = viewModel.offers.firstOrNull { it.id == offerId }
                
                if (offer != null) {
                    com.example.matchify.ui.offers.details.OfferDetailsScreen(
                        offer = offer,
                        onBack = { navController.popBackStack() },
                        onNavigateToChat = { conversationId ->
                            navController.navigate("conversation_chat/$conversationId")
                        }
                    )
                } else {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }
            
            // Contracts
            composable("create_contract/{missionId}/{talentId}") { backStackEntry ->
                val missionId = backStackEntry.arguments?.getString("missionId") ?: ""
                val talentId = backStackEntry.arguments?.getString("talentId") ?: ""
                val conversationId = backStackEntry.savedStateHandle.get<String>("conversationId") ?: ""
                
                com.example.matchify.ui.contracts.CreateContractScreen(
                    missionId = missionId,
                    talentId = talentId,
                    onBack = { navController.popBackStack() },
                    onContractCreated = {
                        // Return to conversation screen automatically (same as iOS)
                        navController.popBackStack()
                    }
                )
            }
            
            composable("contract_detail/{contractId}") { backStackEntry ->
                val contractId = backStackEntry.arguments?.getString("contractId") ?: ""
                com.example.matchify.ui.contracts.ContractDetailScreen(
                    contractId = contractId,
                    onBack = { navController.popBackStack() }
                )
            }
            
            composable("contract_review/{contractId}") { backStackEntry ->
                val contractId = backStackEntry.arguments?.getString("contractId") ?: ""
                // Load contract first
                val contractDetailViewModel: com.example.matchify.ui.contracts.ContractDetailViewModel = 
                    androidx.lifecycle.viewmodel.compose.viewModel(
                        factory = com.example.matchify.ui.contracts.ContractDetailViewModelFactory(contractId)
                    )
                val contract by contractDetailViewModel.contract.collectAsState()
                
                LaunchedEffect(Unit) {
                    contractDetailViewModel.loadContract()
                }
                
                if (contract != null) {
                    // If contract is already signed by both, show detail screen instead
                    if (contract!!.status == com.example.matchify.domain.model.Contract.ContractStatus.SIGNED_BY_BOTH) {
                        com.example.matchify.ui.contracts.ContractDetailScreen(
                            contractId = contractId,
                            onBack = { navController.popBackStack() }
                        )
                    } else {
                        // Contract not yet signed - show review screen for talent to sign
                        com.example.matchify.ui.contracts.ContractReviewScreen(
                            contract = contract!!,
                            onBack = { navController.popBackStack() },
                            onSigned = {
                                navController.popBackStack()
                            },
                            onDeclined = {
                                navController.popBackStack()
                            }
                        )
                    }
                } else {
                    Box(
                        modifier = androidx.compose.ui.Modifier.fillMaxSize(),
                        contentAlignment = androidx.compose.ui.Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }
        }
    }
}

