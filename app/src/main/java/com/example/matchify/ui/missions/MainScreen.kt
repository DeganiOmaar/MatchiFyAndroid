package com.example.matchify.ui.missions

import androidx.compose.foundation.layout.*
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
import com.google.gson.Gson
import androidx.compose.runtime.collectAsState

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
            // Only show bottom bar on main screens
            if (currentRoute in listOf("missions_list", "recruiter_profile", "talent_profile")) {
                MainBottomNavigation(
                    currentRoute = currentRoute,
                    profileRoute = profileRoute,
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
                MissionListScreenNew(
                    onAddMission = {
                        navController.navigate("mission_add")
                    },
                    onEditMission = { mission ->
                        val missionJson = Gson().toJson(mission)
                        navController.navigate("mission_edit/$missionJson")
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
                TalentProfileScreen(
                    viewModel = profileViewModel,
                    onEditProfile = {
                        navController.navigate("edit_talent_profile")
                    },
                    onSettings = onOpenSettings
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
        }
    }
}

