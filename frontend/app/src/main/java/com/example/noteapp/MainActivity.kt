// MainActivity.kt (oppdatert)
package com.example.noteapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.noteapp.ui.components.BottomNavigationBar
import com.example.noteapp.ui.navigation.NavigationGraph
import com.example.noteapp.ui.navigation.Screen
import com.example.noteapp.viewmodel.AuthViewModel
import com.example.noteapp.viewmodel.NoteViewModel
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Firebase
        FirebaseApp.initializeApp(this)

        setContent {
            MaterialTheme {
                NoteApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteApp() {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = viewModel()
    val noteViewModel: NoteViewModel = viewModel { NoteViewModel(authViewModel) }

    val authUiState by authViewModel.uiState.collectAsState()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Check if user is logged in
    val startDestination = if (authUiState.isLoggedIn) {
        Screen.Notes.route
    } else {
        Screen.Login.route
    }

    // Screens for bottom navigation (only shown when logged in)
    val bottomNavScreens = listOf(
        Screen.Notes,
        Screen.AddNote,
        Screen.Profile
    )

    // Check if we should show bottom navigation
    val shouldShowBottomNav = currentRoute in bottomNavScreens.map { it.route }
    val shouldShowTopBar = currentRoute != Screen.Login.route && currentRoute != Screen.SignUp.route

    Scaffold(
        topBar = {
            if (shouldShowTopBar) {
                TopAppBar(
                    title = {
                        Text(
                            bottomNavScreens.find { it.route == currentRoute }?.title
                                ?: "Min Note App"
                        )
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                )
            }
        },
        bottomBar = {
            if (shouldShowBottomNav) {
                BottomNavigationBar(
                    navController = navController,
                    screens = bottomNavScreens,
                    currentRoute = currentRoute
                )
            }
        }
    ) { padding ->
        NavigationGraph(
            navController = navController,
            noteViewModel = noteViewModel,
            authViewModel = authViewModel,
            modifier = Modifier.padding(padding),
            startDestination = startDestination
        )
    }
}
