package org.example.noteappapi.routes

import org.example.noteappapi.controller.NoteController
import org.example.noteappapi.model.CreateNoteRequest
import org.example.noteappapi.model.Note
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/notes")
class NoteRoutes(private val controller: NoteController) {

    @GetMapping
    fun getAllNotes(): ResponseEntity<List<Note>> {
        return controller.getAllNotes()
    }

    @GetMapping("/{id}")
    fun getNoteById(@PathVariable id: Long): ResponseEntity<Note> {
        return controller.getNoteById(id)
    }

    @PostMapping
    fun createNote(@RequestBody request: CreateNoteRequest): ResponseEntity<Note> {
        return controller.createNote(request)
    }

    @DeleteMapping("/{id}")
    fun deleteNote(@PathVariable id: Long): ResponseEntity<Map<String, Boolean>> {
        return controller.deleteNote(id)
    }
}