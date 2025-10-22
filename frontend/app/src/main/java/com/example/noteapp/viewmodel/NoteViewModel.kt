// NoteViewModel.kt (oppdatert)
package com.example.noteapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.noteapp.data.model.Note
import com.example.noteapp.data.repository.NoteRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class NoteUiState(
    val notes: List<Note> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
class NoteViewModel(
    private val repository: NoteRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(NoteUiState())
    val uiState: StateFlow<NoteUiState> = _uiState

    init {
        loadNotes()
    }

    fun loadNotes() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            repository.getAllNotes().collect { result ->
                result.fold(
                    onSuccess = { notes ->
                        _uiState.update {
                            it.copy(
                                notes = notes,
                                isLoading = false
                            )
                        }
                    },
                    onFailure = { exception ->
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                error = exception.message
                            )
                        }
                    }
                )
            }
        }
    }

    fun createNote(title: String, content: String) {
        viewModelScope.launch {
            repository.createNote(title, content).fold(
                onSuccess = {
                    loadNotes() // Reload notes after creation
                },
                onFailure = { exception ->
                    _uiState.update {
                        it.copy(error = exception.message)
                    }
                }
            )
        }
    }

    fun deleteNote(noteId: String) {
        viewModelScope.launch {
            repository.deleteNote(noteId).fold(
                onSuccess = {
                    loadNotes() // Reload notes after deletion
                },
                onFailure = { exception ->
                    _uiState.update {
                        it.copy(error = exception.message)
                    }
                }
            )
        }
    }
}