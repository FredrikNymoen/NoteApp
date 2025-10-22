package org.example.noteappapi.model

import com.google.cloud.Timestamp

data class NoteWithAuthor(
    val id: String = "",
    val userId: String = "",
    val title: String = "",
    val content: String = "",
    val userName: String = "",
    val createdAt: Timestamp = Timestamp.now(),
    val updatedAt: Timestamp = Timestamp.now()
)
