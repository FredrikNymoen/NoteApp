// Updated NoteController.kt
package org.example.noteappapi.controller

import com.google.cloud.firestore.Firestore
import org.example.noteappapi.model.CreateNoteRequest
import org.example.noteappapi.model.Note
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController
@RequestMapping("/api/notes")
class NoteController(private val firestore: Firestore) {

    private val COLLECTION_NAME = "notes"

    @GetMapping
    fun getAllNotes(): ResponseEntity<List<Note>> {
        val userId = SecurityContextHolder.getContext().authentication.principal as String

        val notes = firestore.collection(COLLECTION_NAME)
            .whereEqualTo("userId", userId)
            .get()
            .get()
            .documents
            .map { doc ->
                doc.toObject(Note::class.java).copy(id = doc.id)
            }

        return ResponseEntity.ok(notes)
    }

    @GetMapping("/{id}")
    fun getNoteById(@PathVariable id: String): ResponseEntity<Note> {
        val userId = SecurityContextHolder.getContext().authentication.principal as String

        val doc = firestore.collection(COLLECTION_NAME).document(id).get().get()

        return if (doc.exists()) {
            val note = doc.toObject(Note::class.java)?.copy(id = doc.id)
            if (note?.userId == userId) {
                ResponseEntity.ok(note)
            } else {
                ResponseEntity.status(HttpStatus.FORBIDDEN).build()
            }
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @PostMapping
    fun createNote(@RequestBody request: CreateNoteRequest): ResponseEntity<Note> {
        val userId = SecurityContextHolder.getContext().authentication.principal as String

        if (request.title.isBlank() || request.content.isBlank()) {
            return ResponseEntity.badRequest().build()
        }

        // Get user's display name from Firestore
        val userDoc = firestore.collection("users").document(userId).get().get()
        val userName = if (userDoc.exists()) {
            userDoc.getString("name") ?: "Unknown"
        } else {
            "Unknown"
        }

        val noteId = UUID.randomUUID().toString()
        val note = Note(
            id = noteId,
            userId = userId,
            userName = userName,
            title = request.title,
            content = request.content
        )

        firestore.collection(COLLECTION_NAME).document(noteId).set(note).get()

        return ResponseEntity.status(HttpStatus.CREATED).body(note)
    }

    @DeleteMapping("/{id}")
    fun deleteNote(@PathVariable id: String): ResponseEntity<Map<String, Boolean>> {
        val userId = SecurityContextHolder.getContext().authentication.principal as String

        val doc = firestore.collection(COLLECTION_NAME).document(id).get().get()

        return if (doc.exists()) {
            val note = doc.toObject(Note::class.java)
            if (note?.userId == userId) {
                firestore.collection(COLLECTION_NAME).document(id).delete().get()
                ResponseEntity.ok(mapOf("success" to true))
            } else {
                ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(mapOf("success" to false))
            }
        } else {
            ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(mapOf("success" to false))
        }
    }
}