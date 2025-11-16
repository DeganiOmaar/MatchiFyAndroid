package com.example.matchify.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.matchify.ui.auth.chooserole.ChooseRoleScreen
import com.example.matchify.ui.auth.forgot.ForgotPasswordScreen
import com.example.matchify.ui.auth.forgot.ForgotPasswordViewModel
import com.example.matchify.ui.auth.forgot.ForgotPasswordViewModelFactory
import com.example.matchify.ui.auth.login.LoginScreen
import com.example.matchify.ui.auth.login.LoginViewModel
import com.example.matchify.ui.auth.login.LoginViewModelFactory
import com.example.matchify.ui.auth.reset.ResetPasswordScreen
import com.example.matchify.ui.auth.reset.ResetPasswordViewModel
import com.example.matchify.ui.auth.reset.ResetPasswordViewModelFactory
import com.example.matchify.ui.auth.signup.recruiter.RecruiterSignupScreen
import com.example.matchify.ui.auth.signup.recruiter.RecruiterSignupViewModel
import com.example.matchify.ui.auth.signup.recruiter.RecruiterSignupViewModelFactory
import com.example.matchify.ui.auth.signup.talent.TalentSignupScreen
import com.example.matchify.ui.auth.signup.talent.TalentSignupViewModel
import com.example.matchify.ui.auth.signup.talent.TalentSignupViewModelFactory
import com.example.matchify.ui.auth.verify.VerifyCodeScreen
import com.example.matchify.ui.auth.verify.VerifyCodeViewModel
import com.example.matchify.ui.auth.verify.VerifyCodeViewModelFactory
import com.example.matchify.ui.home.HomeScreen
import com.example.matchify.ui.recruiter.profile.RecruiterProfileScreen
import com.example.matchify.ui.recruiter.profile.RecruiterProfileViewModel
import com.example.matchify.ui.recruiter.profile.RecruiterProfileViewModelFactory
import com.example.matchify.ui.recruiter.edit.EditRecruiterProfileScreen
import com.example.matchify.ui.recruiter.edit.EditRecruiterProfileViewModel
import com.example.matchify.ui.recruiter.edit.EditRecruiterProfileViewModelFactory

@Composable
fun AppNavGraph(
    navController: NavHostController,
    startDestination: String
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {

        // LOGIN
        composable("login") {
            val viewModel: LoginViewModel = viewModel(factory = LoginViewModelFactory())
            LoginScreen(
                onForgotPassword = { navController.navigate("forgot") },
                onSignUp = { navController.navigate("choose_role") },
                onLoginSuccess = { route ->
                    navController.navigate(route) {
                        popUpTo("login") { inclusive = true }
                    }
                },
                viewModel = viewModel
            )
        }

        // CHOOSE ROLE
        composable("choose_role") {
            ChooseRoleScreen(
                onTalentSelected = { navController.navigate("signup_talent") },
                onRecruiterSelected = { navController.navigate("signup_recruiter") }
            )
        }

        // SIGN UP TALENT
        composable("signup_talent") {
            val viewModel: TalentSignupViewModel = viewModel(factory = TalentSignupViewModelFactory())
            TalentSignupScreen(
                onLogin = { navController.popBackStack() },
                onSuccess = {
                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                viewModel = viewModel
            )
        }

        // SIGN UP RECRUITER
        composable("signup_recruiter") {
            val viewModel: RecruiterSignupViewModel = viewModel(factory = RecruiterSignupViewModelFactory())
            RecruiterSignupScreen(
                onLoginClick = { navController.popBackStack() },
                onSignupSuccess = {
                    navController.navigate("main") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                viewModel = viewModel
            )
        }

        // FORGOT PASSWORD
        composable("forgot") {
            val viewModel: ForgotPasswordViewModel = viewModel(factory = ForgotPasswordViewModelFactory())
            ForgotPasswordScreen(
                onCodeSent = { email ->
                    navController.navigate("verify/$email")
                },
                onBackToLogin = { navController.popBackStack() },
                viewModel = viewModel
            )
        }

        // VERIFY CODE
        composable("verify/{email}") { backStack ->
            val email = backStack.arguments?.getString("email") ?: ""
            val viewModel: VerifyCodeViewModel = viewModel(factory = VerifyCodeViewModelFactory())
            VerifyCodeScreen(
                email = email,
                onVerified = {
                    navController.navigate("reset")
                },
                viewModel = viewModel
            )
        }

        // RESET PASSWORD
        composable("reset") {
            val viewModel: ResetPasswordViewModel = viewModel(factory = ResetPasswordViewModelFactory())
            ResetPasswordScreen(
                onResetSuccess = {
                    navController.navigate("login") {
                        popUpTo("reset") { inclusive = true }
                    }
                },
                viewModel = viewModel
            )
        }

        // HOME (Talents)
        composable("home") {
            HomeScreen()
        }

        // MAIN SCREEN (Missions + Profile with Bottom Navigation)
        composable("main") {
            com.example.matchify.ui.missions.MainScreen()
        }

        // RECRUITER PROFILE (kept for backward compatibility)
        composable("recruiter_profile") {
            val vm: RecruiterProfileViewModel = viewModel(factory = RecruiterProfileViewModelFactory())
            RecruiterProfileScreen(
                viewModel = vm,
                onEditProfile = { navController.navigate("edit_recruiter_profile") }
            )
        }

        // EDIT RECRUITER PROFILE
        composable("edit_recruiter_profile") {
            val context = LocalContext.current
            val profileViewModel: RecruiterProfileViewModel = viewModel(factory = RecruiterProfileViewModelFactory())
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
                    // Refresh profile after edit
                    profileViewModel.refreshProfile()
                    navController.popBackStack()
                }
            )
        }
    }
}