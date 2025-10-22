package com.example.noteapp.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.userProfileChangeRequest
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class AuthViewModel : ViewModel() {
    private val auth: FirebaseAuth = Firebase.auth
    private val firestore = Firebase.firestore

    var currentUser by mutableStateOf<FirebaseUser?>(null)
        private set

    var isLoggedIn by mutableStateOf(false)
        private set

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    init {
        checkCurrentUser()
    }

    private fun checkCurrentUser() {
        currentUser = auth.currentUser
        isLoggedIn = currentUser != null
    }

    fun signIn(email: String, password: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                val result = auth.signInWithEmailAndPassword(email, password).await()
                currentUser = result.user
                isLoggedIn = true
                onSuccess()
            } catch (e: Exception) {
                errorMessage = "Feil ved innlogging: ${e.localizedMessage}"
            } finally {
                isLoading = false
            }
        }
    }

    fun signUp(name: String, email: String, password: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                val result = auth.createUserWithEmailAndPassword(email, password).await()

                // Oppdater brukernavn
                result.user?.updateProfile(userProfileChangeRequest {
                    displayName = name
                })?.await()

                // Opprett brukerdokument i Firestore
                result.user?.let { user ->
                    val userDoc = hashMapOf(
                        "uid" to user.uid,
                        "name" to name,
                        "email" to email,
                        "createdAt" to com.google.firebase.Timestamp.now()
                    )
                    firestore.collection("users").document(user.uid).set(userDoc).await()
                }

                currentUser = result.user
                isLoggedIn = true
                onSuccess()
            } catch (e: Exception) {
                errorMessage = "Feil ved registrering: ${e.localizedMessage}"
            } finally {
                isLoading = false
            }
        }
    }

    fun signOut() {
        auth.signOut()
        currentUser = null
        isLoggedIn = false
    }

    fun getCurrentUserId(): String? = auth.currentUser?.uid

    suspend fun getIdToken(): String? {
        return try {
            auth.currentUser?.getIdToken(false)?.await()?.token
        } catch (e: Exception) {
            null
        }
    }

    fun clearError() {
        errorMessage = null
    }
}