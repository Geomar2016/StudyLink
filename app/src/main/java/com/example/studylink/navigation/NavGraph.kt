package com.example.studylink.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.studylink.ui.auth.LoginScreen
import com.example.studylink.ui.home.HomeScreen
import com.example.studylink.ui.profile.ProfileScreen
import com.example.studylink.ui.session.CreateSessionScreen
import com.example.studylink.ui.session.SessionDetailScreen

object Routes {
    const val LOGIN = "login"
    const val HOME = "home"
    const val CREATE_SESSION = "create_session"
    const val SESSION_DETAIL = "session_detail/{sessionId}"
    const val PROFILE = "profile"
}

@Composable
fun NavGraph(
    navController: NavHostController,
    startDestination: String,
    paddingValues: PaddingValues
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = Modifier.padding(paddingValues)
    ) {
        composable(Routes.LOGIN) {
            LoginScreen(navController = navController)
        }
        composable(Routes.HOME) {
            HomeScreen(navController = navController)
        }
        composable(Routes.CREATE_SESSION) {
            CreateSessionScreen(navController = navController)
        }
        composable(Routes.SESSION_DETAIL) { backStackEntry ->
            val sessionId = backStackEntry.arguments?.getString("sessionId") ?: ""
            SessionDetailScreen(navController = navController, sessionId = sessionId)
        }
        composable(Routes.PROFILE) {
            ProfileScreen(navController = navController)
        }
    }
}