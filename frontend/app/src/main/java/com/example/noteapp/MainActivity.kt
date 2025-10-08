package com.example.noteapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.noteapp.ui.components.BottomNavigationBar
import com.example.noteapp.ui.navigation.NavigationGraph
import com.example.noteapp.ui.navigation.Screen
import com.example.noteapp.viewmodel.NoteViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                NoteApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteApp(viewModel: NoteViewModel = viewModel()) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val screens = listOf(
        Screen.Notes,
        Screen.AddNote,
        Screen.Profile
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        screens.find { it.route == currentRoute }?.title ?: "Min Note App"
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        },
        bottomBar = {
            BottomNavigationBar(
                navController = navController,
                screens = screens,
                currentRoute = currentRoute
            )
        }
    ) { padding ->
        NavigationGraph(
            navController = navController,
            viewModel = viewModel,
            modifier = Modifier.padding(padding)
        )
    }
}