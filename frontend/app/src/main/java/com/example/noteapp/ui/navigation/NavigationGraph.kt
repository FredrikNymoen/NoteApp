// NavigationGraph.kt (Oppdatert med auth flow)
package com.example.noteapp.ui.navigation

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.noteapp.ui.screens.addnote.AddNoteScreen
import com.example.noteapp.ui.screens.auth.LoginScreen
import com.example.noteapp.ui.screens.auth.SignUpScreen
import com.example.noteapp.ui.screens.notes.NotesListScreen
import com.example.noteapp.ui.screens.profile.ProfileScreen
import com.example.noteapp.viewmodel.AuthViewModel
import com.example.noteapp.viewmodel.NoteViewModel

@Composable
fun NavigationGraph(
    navController: NavHostController,
    noteViewModel: NoteViewModel,
    authViewModel: AuthViewModel,
    modifier: Modifier = Modifier,
    startDestination: String
) {
    val authUiState by authViewModel.uiState.collectAsState()

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        // Auth screens
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginClick = { email, password ->
                    authViewModel.signIn(email, password)
                },
                onSignUpNavigate = {
                    navController.navigate(Screen.SignUp.route)
                },
                isLoading = authUiState.isLoading,
                errorMessage = authUiState.errorMessage
            )

            // Clear error when leaving screen
            DisposableEffect(Unit) {
                onDispose {
                    authViewModel.clearError()
                }
            }
        }

        composable(Screen.SignUp.route) {
            SignUpScreen(
                onSignUpClick = { name, email, password ->
                    authViewModel.signUp(name, email, password)
                },
                onLoginNavigate = {
                    navController.navigateUp()
                },
                isLoading = authUiState.isLoading,
                errorMessage = authUiState.errorMessage
            )

            // Clear error when leaving screen
            DisposableEffect(Unit) {
                onDispose {
                    authViewModel.clearError()
                }
            }
        }

        // Main app screens
        composable(Screen.Notes.route) {
            NotesListScreen(noteViewModel)
        }

        composable(Screen.AddNote.route) {
            AddNoteScreen(
                viewModel = noteViewModel,
                onNoteSaved = {
                    navController.navigate(Screen.Notes.route) {
                        popUpTo(Screen.Notes.route) { inclusive = false }
                    }
                }
            )
        }

        composable(Screen.Profile.route) {
            ProfileScreen(
                authViewModel = authViewModel,
                onSignOut = {
                    authViewModel.signOut()
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
    }
}