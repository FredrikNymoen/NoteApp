package org.example.noteappapi.model

data class CreateNoteRequest(
    val title: String,
    val content: String
)