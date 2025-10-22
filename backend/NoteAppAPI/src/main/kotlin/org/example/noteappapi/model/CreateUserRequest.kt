package org.example.noteappapi.model

data class CreateUserRequest(
    val uid: String,
    val name: String,
    val email: String
)
