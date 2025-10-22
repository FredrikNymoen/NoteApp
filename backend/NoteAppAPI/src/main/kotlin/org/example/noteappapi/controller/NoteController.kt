// Updated NoteController.kt
package org.example.noteappapi.controller

import com.google.cloud.firestore.Firestore
import org.example.noteappapi.model.CreateNoteRequest
import org.example.noteappapi.model.Note
import org.example.noteappapi.model.NoteWithAuthor
import org.example.noteappapi.model.User
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController
@RequestMapping("/api/notes")
class NoteController(private val firestore: Firestore) {

    private val COLLECTION_NAME = "notes"
    private val USERS_COLLECTION = "users"

    @GetMapping
    fun getAllNotes(): ResponseEntity<List<NoteWithAuthor>> {
        val userId = SecurityContextHolder.getContext().authentication.principal as String

        val notes = firestore.collection(COLLECTION_NAME)
            .whereEqualTo("userId", userId)
            .get()
            .get()
            .documents
            .mapNotNull { doc ->
                val note = doc.toObject(Note::class.java)?.copy(id = doc.id)
                note?.let { addAuthorName(it) }
            }

        return ResponseEntity.ok(notes)
    }

    @GetMapping("/{id}")
    fun getNoteById(@PathVariable id: String): ResponseEntity<NoteWithAuthor> {
        val userId = SecurityContextHolder.getContext().authentication.principal as String

        val doc = firestore.collection(COLLECTION_NAME).document(id).get().get()

        return if (doc.exists()) {
            val note = doc.toObject(Note::class.java)?.copy(id = doc.id)
            if (note?.userId == userId) {
                val noteWithAuthor = note.let { addAuthorName(it) }
                ResponseEntity.ok(noteWithAuthor)
            } else {
                ResponseEntity.status(HttpStatus.FORBIDDEN).build()
            }
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @PostMapping
    fun createNote(@RequestBody request: CreateNoteRequest): ResponseEntity<NoteWithAuthor> {
        val userId = SecurityContextHolder.getContext().authentication.principal as String

        if (request.title.isBlank() || request.content.isBlank()) {
            return ResponseEntity.badRequest().build()
        }

        val noteId = UUID.randomUUID().toString()
        val note = Note(
            id = noteId,
            userId = userId,
            title = request.title,
            content = request.content
        )

        firestore.collection(COLLECTION_NAME).document(noteId).set(note).get()

        val noteWithAuthor = addAuthorName(note)
        return ResponseEntity.status(HttpStatus.CREATED).body(noteWithAuthor)
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

    private fun addAuthorName(note: Note): NoteWithAuthor {
        val userName = try {
            val userDoc = firestore.collection(USERS_COLLECTION)
                .document(note.userId)
                .get()
                .get()

            if (userDoc.exists()) {
                userDoc.getString("name") ?: "Ukjent bruker"
            } else {
                "Ukjent bruker"
            }
        } catch (e: Exception) {
            "Ukjent bruker"
        }

        return NoteWithAuthor(
            id = note.id,
            userId = note.userId,
            title = note.title,
            content = note.content,
            userName = userName,
            createdAt = note.createdAt,
            updatedAt = note.updatedAt
        )
    }
}