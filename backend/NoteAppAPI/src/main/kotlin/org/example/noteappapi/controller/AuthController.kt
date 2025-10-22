package org.example.noteappapi.controller

import com.google.cloud.firestore.Firestore
import org.example.noteappapi.model.CreateUserRequest
import org.example.noteappapi.model.User
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/auth")
class AuthController(private val firestore: Firestore) {

    private val COLLECTION_NAME = "users"

    // Firebase Authentication håndteres på frontend
    // Backend verifiserer bare tokens

    @GetMapping("/verify")
    fun verifyToken(): ResponseEntity<Map<String, String>> {
        return ResponseEntity.ok(mapOf("status" to "authenticated"))
    }

    @PostMapping("/register")
    fun registerUser(@RequestBody request: CreateUserRequest): ResponseEntity<User> {
        // Validering av inndata
        if (request.uid.isBlank() || request.name.isBlank() || request.email.isBlank()) {
            return ResponseEntity.badRequest().build()
        }

        val user = User(
            uid = request.uid,
            name = request.name,
            email = request.email
        )

        try {
            firestore.collection(COLLECTION_NAME).document(request.uid).set(user).get()
            return ResponseEntity.status(HttpStatus.CREATED).body(user)
        } catch (e: Exception) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()
        }
    }
}