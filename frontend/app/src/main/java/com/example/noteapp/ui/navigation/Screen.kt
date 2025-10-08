package com.example.noteapp.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    object Notes : Screen("notes", "Notater", Icons.AutoMirrored.Filled.List)
    object AddNote : Screen("add_note", "Legg til", Icons.Default.Add)
    object Profile : Screen("profile", "Profil", Icons.Default.Person)
}