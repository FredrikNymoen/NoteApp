package com.example.noteapp.ui.screens.notes.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.noteapp.data.model.Note
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

@Composable
fun NoteItem(
    note: Note,
    onDelete: () -> Unit
) {
    var userName by remember { mutableStateOf("Henter navn...") }

    // Hent brukernavn fra Firestore basert på userId
    LaunchedEffect(note.userId) {
        try {
            val userDoc = Firebase.firestore.collection("users")
                .document(note.userId)
                .get()
                .result

            userName = if (userDoc != null && userDoc.exists()) {
                userDoc.getString("name") ?: "Ukjent bruker"
            } else {
                "Ukjent bruker"
            }
        } catch (e: Exception) {
            userName = "Ukjent bruker"
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = note.title,
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = note.content,
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Av: $userName",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            IconButton(onClick = onDelete) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Slett",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}