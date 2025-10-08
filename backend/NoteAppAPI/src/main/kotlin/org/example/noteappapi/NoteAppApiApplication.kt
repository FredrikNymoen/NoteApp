package org.example.noteappapi

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class NoteAppApiApplication

fun main(args: Array<String>) {
    runApplication<NoteAppApiApplication>(*args)
}