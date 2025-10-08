package com.example.noteapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.noteapp.ui.screens.addnote.AddNoteScreen
import com.example.noteapp.ui.screens.notes.NotesListScreen
import com.example.noteapp.ui.screens.profile.ProfileScreen
import com.example.noteapp.viewmodel.NoteViewModel

@Composable
fun NavigationGraph(
    navController: NavHostController,
    viewModel: NoteViewModel,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Notes.route,
        modifier = modifier
    ) {
        composable(Screen.Notes.route) {
            NotesListScreen(viewModel)
        }

        composable(Screen.AddNote.route) {
            AddNoteScreen(
                viewModel = viewModel,
                onNoteSaved = {
                    navController.navigate(Screen.Notes.route) {
                        popUpTo(Screen.Notes.route) { inclusive = false }
                    }
                }
            )
        }

        composable(Screen.Profile.route) {
            ProfileScreen()
        }
    }
}