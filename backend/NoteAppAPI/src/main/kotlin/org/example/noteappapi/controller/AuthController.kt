package org.example.noteappapi.controller

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/auth")
class AuthController {

    // Firebase Authentication håndteres på frontend
    // Backend verifiserer bare tokens

    @GetMapping("/verify")
    fun verifyToken(): ResponseEntity<Map<String, String>> {
        return ResponseEntity.ok(mapOf("status" to "authenticated"))
    }
}