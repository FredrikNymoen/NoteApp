package com.example.noteapp.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.noteapp.data.model.Note
import com.example.noteapp.data.repository.NoteRepository
import kotlinx.coroutines.launch

class NoteViewModel(
    private val authViewModel: AuthViewModel
) : ViewModel() {
    private val repository = NoteRepository(authViewModel)

    var notes by mutableStateOf<List<Note>>(emptyList())
        private set

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    private var notesLoaded = false

    fun ensureNotesLoaded() {
        if (!notesLoaded) {
            loadNotes()
            notesLoaded = true
        }
    }

    fun loadNotes() {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                repository.getAllNotes().collect { result ->
                    result.onSuccess { notesList ->
                        notes = notesList
                    }.onFailure { exception ->
                        errorMessage = "Feil ved lasting: ${exception.localizedMessage}"
                    }
                }
            } catch (e: Exception) {
                errorMessage = "Feil ved lasting: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    fun addNote(title: String, content: String) {
        viewModelScope.launch {
            try {
                val result = repository.createNote(title, content)
                result.onSuccess {
                    loadNotes()
                }.onFailure { exception ->
                    errorMessage = "Feil ved lagring: ${exception.localizedMessage}"
                }
            } catch (e: Exception) {
                errorMessage = "Feil ved lagring: ${e.message}"
            }
        }
    }

    fun deleteNote(noteId: String) {
        viewModelScope.launch {
            try {
                val result = repository.deleteNote(noteId)
                result.onSuccess {
                    loadNotes()
                }.onFailure { exception ->
                    errorMessage = "Feil ved sletting: ${exception.localizedMessage}"
                }
            } catch (e: Exception) {
                errorMessage = "Feil ved sletting: ${e.message}"
            }
        }
    }
}