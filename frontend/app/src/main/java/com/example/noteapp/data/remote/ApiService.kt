package com.example.noteapp.data.remote

import com.example.noteapp.data.model.CreateNoteRequest
import com.example.noteapp.data.model.Note
import retrofit2.http.*

interface ApiService {
    @GET("api/notes")
    suspend fun getAllNotes(
        @Header("Authorization") token: String
    ): List<Note>

    @GET("api/notes/{id}")
    suspend fun getNoteById(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): Note

    @POST("api/notes")
    suspend fun createNote(
        @Header("Authorization") token: String,
        @Body request: CreateNoteRequest
    ): Note

    @DELETE("api/notes/{id}")
    suspend fun deleteNote(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): Map<String, Boolean>

    @GET("api/auth/verify")
    suspend fun verifyAuth(
        @Header("Authorization") token: String
    ): Map<String, String>
}