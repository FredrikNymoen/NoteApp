package com.example.noteapp.data.model

import com.google.firebase.Timestamp

data class User(
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val createdAt: Timestamp = Timestamp.now()
)

data class CreateUserRequest(
    val uid: String,
    val name: String,
    val email: String
)
