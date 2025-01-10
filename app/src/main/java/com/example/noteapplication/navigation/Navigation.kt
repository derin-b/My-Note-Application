package com.example.noteapplication.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.noteapplication.presentation.landing.LandingScreen
import com.example.noteapplication.presentation.login.LoginScreen
import com.example.noteapplication.presentation.register.RegisterScreen
import com.example.noteapplication.presentation.NoteViewmodel
import com.example.noteapplication.presentation.noteDetail.NoteDetail
import com.example.noteapplication.presentation.notes.NoteScreen
import com.example.noteapplication.utils.Constants

sealed class NavRoute{
    data class LandingScreen(val name: String = "landing"): NavRoute()
    data class LoginScreen(val name: String = "login"): NavRoute()
    data class RegisterScreen(val name: String = "register"): NavRoute()
    data class NoteScreen(val name: String = "notes"): NavRoute()
    data class NoteDetailScreen(val noteId: String, val name: String = "note_detail/$noteId") : NavRoute()
}
@Composable
fun Navigation(navHostController: NavHostController, startDestination: String, noteViewmodel: NoteViewmodel) {
    // navHost defines the navigation host with a given startDestination
    NavHost(navController = navHostController, startDestination = startDestination ){
        // LOGIN_SCREENS navigation graph
        navigation(startDestination = NavRoute.LandingScreen().name, route = Constants.NavGraphs.LOGIN_SCREENS) {
            // landing screen composable
            composable(route = NavRoute.LandingScreen().name) {
                LandingScreen(
                    // navigate to Login screen when 'Login' is clicked
                    onLoginClick = {
                        navHostController.navigateToSingleTop(NavRoute.LoginScreen().name)
                    },
                    // navigate to Register screen when 'Register' is clicked
                    onRegisterClick = {
                        navHostController.navigateToSingleTop(NavRoute.RegisterScreen().name)
                    }
                )
            }
            composable(route = NavRoute.LoginScreen().name) {
                LoginScreen(
                    // navigate to NOTE_SCREENS graph after successful login
                        // and pop up the login screens from the back stack
                    onLoginClick = {
                        navHostController.navigate(route = Constants.NavGraphs.NOTE_SCREENS) {
                            popUpTo(route = Constants.NavGraphs.LOGIN_SCREENS)
                        }
                                   },
                    noteViewmodel = noteViewmodel
                )
            }
            composable(route = NavRoute.RegisterScreen().name) {
                RegisterScreen(
                    // navigate back to the Login screen when 'Login' is clicked
                    onLoginClick = { navHostController.navigateToSingleTop(NavRoute.LoginScreen().name) },
                    // navigate to NOTE_SCREENS graph after successful registration
                    // and pop up the login screens from the back stack
                    onRegisterClick = {
                        navHostController.navigate(route = Constants.NavGraphs.NOTE_SCREENS){
                            popUpTo(route = Constants.NavGraphs.LOGIN_SCREENS)
                        }},
                    noteViewmodel = noteViewmodel
                )
            }
        }

        // NOTE_SCREENS navigation graph
        navigation(startDestination = NavRoute.NoteScreen().name, route = Constants.NavGraphs.NOTE_SCREENS) {
            composable(route = NavRoute.NoteScreen().name) {
                NoteScreen(
                    noteViewmodel = noteViewmodel,
                    // navigate to Note Detail screen with the specific noteId
                    onClick = {noteId ->
                        navHostController.navigate(
                            NavRoute.NoteDetailScreen(noteId).name ){
                            // avoid creating multiple instances of this screen
                        launchSingleTop = true
                    } },
                    onLogOut = {
                        // navigate to the Landing screen on logout
                        navHostController.navigateToSingleTop(NavRoute.LandingScreen().name)
                    }
                )
            }
            composable(route = "note_detail/{noteId}") {
                // retrieve the noteId argument passed from the previous screen
                val noteId = it.arguments?.getString("noteId") ?: ""

                NoteDetail(
                    noteId = noteId,
                    noteViewmodel = noteViewmodel,
                    // navigate back to the previous screen
                    onBackClick = {
                        navHostController.navigateUp()
                    }
                )
            }
        }
    }
}

/**
 * Extension function for `NavHostController` to navigate to a route with specific behavior.
 * Ensures the navigation is handled in a way that avoids duplicate destinations and restores the state.
 *
 * @param route The destination route to navigate to.
 */
fun NavHostController.navigateToSingleTop(route: String) {
    navigate(route) {
        // Ensure we navigate to the start destination of the graph and avoid adding it multiple times
        popUpTo(graph.findStartDestination().id) {
            saveState = true // Save the current state to restore it later
        }
        launchSingleTop = true // Avoid creating multiple instances of the same destination
        restoreState = true // Restore previously saved state when navigating back
    }
}
