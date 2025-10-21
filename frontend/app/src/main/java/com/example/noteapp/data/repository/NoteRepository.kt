// NoteRepository.kt (oppdatert)
package com.example.noteapp.data.repository

import com.example.noteapp.data.remote.ApiService
import com.example.noteapp.data.remote.CreateNoteRequest
import com.example.noteapp.data.remote.NetworkModule
import com.example.noteapp.data.model.Note
import com.example.noteapp.viewmodel.AuthViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class NoteRepository(
    private val authViewModel: AuthViewModel
) {
    private val apiService = NetworkModule.apiService

    private suspend fun getAuthToken(): String {
        val token = authViewModel.getIdToken() ?: throw Exception("Not authenticated")
        return "Bearer $token"
    }

    fun getAllNotes(): Flow<Result<List<Note>>> = flow {
        try {
            val token = getAuthToken()
            val notes = apiService.getAllNotes(token)
            emit(Result.success(notes))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    suspend fun createNote(title: String, content: String): Result<Note> {
        return try {
            val token = getAuthToken()
            val request = CreateNoteRequest(title, content)
            val note = apiService.createNote(token, request)
            Result.success(note)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteNote(noteId: String): Result<Boolean> {
        return try {
            val token = getAuthToken()
            val response = apiService.deleteNote(token, noteId)
            Result.success(response["success"] == true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}