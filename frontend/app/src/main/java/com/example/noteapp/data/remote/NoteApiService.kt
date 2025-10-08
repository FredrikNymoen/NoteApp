package com.example.noteapp.data.remote

import com.example.noteapp.data.model.CreateNoteRequest
import com.example.noteapp.data.model.Note
import retrofit2.http.*

interface NoteApiService {
    @GET("api/notes")
    suspend fun getNotes(): List<Note>

    @POST("api/notes")
    suspend fun createNote(@Body request: CreateNoteRequest): Note

    @DELETE("api/notes/{id}")
    suspend fun deleteNote(@Path("id") id: Long): Map<String, Boolean>
}