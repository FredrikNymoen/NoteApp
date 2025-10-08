package org.example.noteappapi.model

data class Note(
    val id: Long,
    val title: String,
    val content: String,
    val timestamp: Long = System.currentTimeMillis()
)