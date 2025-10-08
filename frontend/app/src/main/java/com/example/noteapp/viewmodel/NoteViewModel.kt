package com.example.noteapp.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.noteapp.data.model.Note
import com.example.noteapp.data.repository.NoteRepository
import kotlinx.coroutines.launch

class NoteViewModel : ViewModel() {
    private val repository = NoteRepository()

    var notes by mutableStateOf<List<Note>>(emptyList())
        private set

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    init {
        loadNotes()
    }

    fun loadNotes() {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                notes = repository.getNotes()
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
                repository.createNote(title, content)
                loadNotes()
            } catch (e: Exception) {
                errorMessage = "Feil ved lagring: ${e.message}"
            }
        }
    }

    fun deleteNote(id: Long) {
        viewModelScope.launch {
            try {
                repository.deleteNote(id)
                loadNotes()
            } catch (e: Exception) {
                errorMessage = "Feil ved sletting: ${e.message}"
            }
        }
    }
}