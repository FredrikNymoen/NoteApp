package com.example.noteapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.noteapp.data.model.CreateUserRequest
import com.example.noteapp.data.remote.RetrofitClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.userProfileChangeRequest
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class AuthUiState(
    val currentUser: FirebaseUser? = null,
    val isLoggedIn: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class AuthViewModel : ViewModel() {
    private val auth: FirebaseAuth = Firebase.auth

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    init {
        checkCurrentUser()
    }

    private fun checkCurrentUser() {
        val user = auth.currentUser
        _uiState.update { it.copy(currentUser = user, isLoggedIn = user != null) }
    }

    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val result = auth.signInWithEmailAndPassword(email, password).await()
                _uiState.update {
                    it.copy(
                        currentUser = result.user,
                        isLoggedIn = true,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        errorMessage = "Feil ved innlogging: ${e.localizedMessage}",
                        isLoading = false
                    )
                }
            }
        }
    }

    fun signUp(name: String, email: String, password: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val result = auth.createUserWithEmailAndPassword(email, password).await()

                // Oppdater brukernavn
                result.user?.updateProfile(userProfileChangeRequest {
                    displayName = name
                })?.await()

                // Opprett brukerdokument på backend
                result.user?.let { user ->
                    try {
                        val token = "Bearer ${user.getIdToken(false).await().token}"
                        val createUserRequest = CreateUserRequest(
                            uid = user.uid,
                            name = name,
                            email = email
                        )
                        RetrofitClient.apiService.registerUser(token, createUserRequest)
                        android.util.Log.d("AuthViewModel", "Bruker registrert på backend")
                    } catch (apiError: Exception) {
                        android.util.Log.e("AuthViewModel", "Feil ved registrering på backend: ${apiError.message}", apiError)
                        throw apiError
                    }
                }

                _uiState.update {
                    it.copy(
                        currentUser = result.user,
                        isLoggedIn = true,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                android.util.Log.e("AuthViewModel", "Feil ved registrering: ${e.message}", e)
                _uiState.update {
                    it.copy(
                        errorMessage = "Feil ved registrering: ${e.localizedMessage}",
                        isLoading = false
                    )
                }
            }
        }
    }

    fun signOut() {
        auth.signOut()
        _uiState.update { it.copy(currentUser = null, isLoggedIn = false) }
    }

    suspend fun getIdToken(): String? {
        return try {
            auth.currentUser?.getIdToken(true)?.await()?.token
        } catch (e: Exception) {
            null
        }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}