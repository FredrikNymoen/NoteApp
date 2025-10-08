package com.example.noteapp.ui.screens.addnote

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.noteapp.viewmodel.NoteViewModel

@Composable
fun AddNoteScreen(
    viewModel: NoteViewModel,
    onNoteSaved: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Tittel") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        OutlinedTextField(
            value = content,
            onValueChange = { content = it },
            label = { Text("Innhold") },
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            maxLines = 8
        )

        Button(
            onClick = {
                if (title.isNotBlank() && content.isNotBlank()) {
                    viewModel.addNote(title, content)
                    title = ""
                    content = ""
                    onNoteSaved()
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = title.isNotBlank() && content.isNotBlank()
        ) {
            Icon(
                Icons.Default.Check,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Lagre notat")
        }

        if (viewModel.errorMessage != null) {
            Text(
                text = viewModel.errorMessage ?: "",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}