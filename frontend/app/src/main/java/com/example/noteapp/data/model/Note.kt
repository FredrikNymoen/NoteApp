package com.example.noteapp.data.model

data class Note(
    val id: Long,
    val title: String,
    val content: String,
    val timestamp: Long
)