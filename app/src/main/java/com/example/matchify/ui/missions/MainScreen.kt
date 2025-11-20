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
import com.example.matchify.ui.missions.list.MissionListScreen
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    navController: NavHostController = rememberNavController(),
    onOpenSettings: () -> Unit = {}
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    
    // Get user role
    val context = LocalContext.current
    val prefs = remember { AuthPreferencesProvider.getInstance().get() }
    val userRole by prefs.role.collectAsState(initial = "recruiter")
    
    // Determine profile route based on role
    val profileRoute = if (userRole == "talent") "talent_profile" else "recruiter_profile"

    Scaffold(
        bottomBar = {
            // Show bottom bar on main screens (Missions, Proposals, Messages)
            if (currentRoute in listOf("missions_list", "proposals_list", "messages_list")) {
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
                            DrawerMenuItemType.SETTINGS -> {
                                navController.navigate("settings") {
                                    popUpTo("missions_list") { saveState = true }
                                    launchSingleTop = true
                                }
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
                                    prefs.logout()
                                    navController.navigate("login") {
                                        popUpTo(navController.graph.startDestinationId) { inclusive = true }
                                        launchSingleTop = true
                                    }
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
                    onDrawerItemSelected = { itemType ->
                        listViewModel.setDrawerNavigationItem(itemType)
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
                val context = LocalContext.current
                val profileViewModel: RecruiterProfileViewModel = viewModel(
                    factory = RecruiterProfileViewModelFactory()
                )
                RecruiterProfileScreen(
                    viewModel = profileViewModel,
                    onEditProfile = {
                        navController.navigate("edit_recruiter_profile")
                    },
                    onSettings = onOpenSettings
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
                    onProjectClick = { project ->
                        // Use actual ID (_id or id), not projectId which can be a random UUID
                        val actualId = project.id ?: project.id_alt ?: ""
                        if (actualId.isNotEmpty()) {
                            navController.navigate("project_details/$actualId")
                        }
                    },
                    onAddProject = {
                        navController.navigate("add_project")
                    }
                )
            }
            
            composable("edit_recruiter_profile") {
                val context = LocalContext.current
                val profileViewModel: RecruiterProfileViewModel = viewModel(
                    factory = RecruiterProfileViewModelFactory()
                )
                val user by profileViewModel.user.collectAsState()
                
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
                        profileViewModel.refreshProfile()
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
            
            composable("proposals_list") {
                com.example.matchify.ui.proposals.ProposalsScreen(
                    onProposalClick = { proposalId ->
                        navController.navigate("proposal_details/$proposalId")
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
                    }
                )
            }
            
            composable("messages_list") {
                com.example.matchify.ui.messages.MessagesScreen(
                    onConversationClick = { conversationId ->
                        navController.navigate("conversation_chat/$conversationId")
                    }
                )
            }
            
            composable("conversation_chat/{conversationId}") { backStackEntry ->
                val conversationId = backStackEntry.arguments?.getString("conversationId") ?: ""
                com.example.matchify.ui.conversations.ConversationChatScreen(
                    conversationId = conversationId,
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
                MyStatsScreen(viewModel = statsViewModel)
            }
            
            composable("settings") {
                val context = LocalContext.current
                val settingsViewModel: com.example.matchify.ui.settings.SettingsViewModel = viewModel(
                    factory = com.example.matchify.ui.settings.SettingsViewModelFactory(AuthPreferencesProvider.getInstance().get())
                )
                com.example.matchify.ui.settings.SettingsScreen(
                    viewModel = settingsViewModel,
                    onBack = { navController.popBackStack() },
                    onLogoutSuccess = {
                        navController.navigate("login") {
                            popUpTo(navController.graph.startDestinationId) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                )
            }
            
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
                    onBack = { navController.popBackStack() }
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
        }
    }
}

