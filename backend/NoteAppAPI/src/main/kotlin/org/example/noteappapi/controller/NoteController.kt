package org.example.noteappapi.controller

import org.example.noteappapi.model.CreateNoteRequest
import org.example.noteappapi.model.Note
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
class NoteController {
    private val notes = mutableListOf<Note>()
    private var nextId = 1L

    init {
        // Initial data
        notes.add(Note(nextId++, "Velkommen", "Dette er din første notat!"))
        notes.add(Note(nextId++, "Shopping", "Melk, brød, ost"))
        notes.add(Note(nextId++, "Trening", "Løpe 5 km i morgen"))
    }

    fun getAllNotes(): ResponseEntity<List<Note>> {
        println("GET /api/notes - Returnerer ${notes.size} notater")
        return ResponseEntity.ok(notes)
    }

    fun getNoteById(id: Long): ResponseEntity<Note> {
        println("GET /api/notes/$id")
        val note = notes.find { it.id == id }
        return if (note != null) {
            ResponseEntity.ok(note)
        } else {
            ResponseEntity.notFound().build()
        }
    }

    fun createNote(request: CreateNoteRequest): ResponseEntity<Note> {
        println("POST /api/notes - Opprettet: ${request.title}")
        if (request.title.isBlank() || request.content.isBlank()) {
            return ResponseEntity.badRequest().build()
        }
        val note = Note(nextId++, request.title, request.content)
        notes.add(note)
        return ResponseEntity.status(HttpStatus.CREATED).body(note)
    }

    fun deleteNote(id: Long): ResponseEntity<Map<String, Boolean>> {
        println("DELETE /api/notes/$id")
        val removed = notes.removeIf { it.id == id }
        return if (removed) {
            ResponseEntity.ok(mapOf("success" to true))
        } else {
            ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(mapOf("success" to false))
        }
    }
}