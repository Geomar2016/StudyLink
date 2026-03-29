package com.example.studylink.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.studylink.ui.auth.LoginScreen
import com.example.studylink.ui.auth.SplashScreen
import com.example.studylink.ui.home.HomeScreen
import com.example.studylink.ui.profile.ProfileScreen
import com.example.studylink.ui.session.ChatScreen
import com.example.studylink.ui.session.ConversationsScreen
import com.example.studylink.ui.session.CreateSessionScreen
import com.example.studylink.ui.session.SessionDetailScreen
import com.example.studylink.ui.session.SessionsListScreen
import com.example.studylink.ui.settings.SettingsScreen

object Routes {
    const val SPLASH = "splash"
    const val LOGIN = "login"
    const val HOME = "home"
    const val SESSIONS_LIST = "sessions_list"
    const val CREATE_SESSION = "create_session"
    const val SESSION_DETAIL = "session_detail/{sessionId}"
    const val PROFILE = "profile"
    const val SETTINGS = "settings"
    const val CHAT = "chat/{toUserId}/{toUserName}"
    const val CONVERSATIONS = "conversations"
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun NavGraph(
    navController: NavHostController,
    startDestination: String,
    paddingValues: PaddingValues
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = Modifier.padding(paddingValues),
        enterTransition = {
            slideInHorizontally(
                initialOffsetX = { it },
                animationSpec = tween(300)
            ) + fadeIn(animationSpec = tween(300))
        },
        exitTransition = {
            slideOutHorizontally(
                targetOffsetX = { -it },
                animationSpec = tween(300)
            ) + fadeOut(animationSpec = tween(300))
        },
        popEnterTransition = {
            slideInHorizontally(
                initialOffsetX = { -it },
                animationSpec = tween(300)
            ) + fadeIn(animationSpec = tween(300))
        },
        popExitTransition = {
            slideOutHorizontally(
                targetOffsetX = { it },
                animationSpec = tween(300)
            ) + fadeOut(animationSpec = tween(300))
        }
    ) {
        composable(Routes.SPLASH) {
            SplashScreen(navController = navController)
        }
        composable(Routes.LOGIN) {
            LoginScreen(navController = navController)
        }
        composable(Routes.HOME) {
            HomeScreen(navController = navController)
        }
        composable(Routes.SESSIONS_LIST) {
            SessionsListScreen(navController = navController)
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
        composable(Routes.SETTINGS) {
            SettingsScreen(navController = navController)
        }
        composable(Routes.CHAT) { backStackEntry ->
            val toUserId = backStackEntry.arguments?.getString("toUserId") ?: ""
            val toUserName = backStackEntry.arguments?.getString("toUserName") ?: ""
            ChatScreen(
                navController = navController,
                toUserId = toUserId,
                toUserName = toUserName
            )
        }
        composable(Routes.CONVERSATIONS) {
            ConversationsScreen(navController = navController)
        }
    }
}