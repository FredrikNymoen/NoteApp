package org.example.noteappapi.model

import com.google.cloud.Timestamp

data class User(
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val createdAt: Timestamp = Timestamp.now()
)
