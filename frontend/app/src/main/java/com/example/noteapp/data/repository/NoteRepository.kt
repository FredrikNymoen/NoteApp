package com.example.noteapp.data.repository

import com.example.noteapp.data.model.CreateNoteRequest
import com.example.noteapp.data.model.Note
import com.example.noteapp.data.remote.RetrofitClient

class NoteRepository {
    private val api = RetrofitClient.apiService

    suspend fun getNotes(): List<Note> {
        return api.getNotes()
    }

    suspend fun createNote(title: String, content: String): Note {
        return api.createNote(CreateNoteRequest(title, content))
    }

    suspend fun deleteNote(id: Long) {
        api.deleteNote(id)
    }
}