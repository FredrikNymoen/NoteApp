# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

NoteApp is a full-stack note-taking application built with:
- **Frontend**: Android mobile app using Jetpack Compose and Kotlin
- **Backend**: Spring Boot REST API in Kotlin
- **Authentication & Database**: Firebase (Authentication and Firestore)

The application follows MVVM architecture on the frontend and Clean Architecture on the backend, with both layers leveraging Firebase for authentication and data persistence.

## Directory Structure

```
NoteApp/
├── frontend/                          # Android Jetpack Compose app
│   ├── app/src/main/java/com/example/noteapp/
│   │   ├── MainActivity.kt            # App entry point
│   │   ├── data/                      # Data layer (models, networking, repository)
│   │   │   ├── model/                 # Data classes (Note, CreateNoteRequest, etc.)
│   │   │   ├── remote/                # Retrofit/networking (ApiService, RetrofitClient)
│   │   │   └── repository/            # NoteRepository (data access abstraction)
│   │   ├── ui/                        # Presentation layer
│   │   │   ├── navigation/            # Navigation Graph and Screen definitions
│   │   │   ├── screens/               # Screen implementations (auth, notes, addnote, profile)
│   │   │   ├── components/            # Reusable UI components
│   │   │   └── theme/                 # Color, Typography, Theme
│   │   └── viewmodel/                 # ViewModels (AuthViewModel, NoteViewModel)
│   ├── app/build.gradle.kts           # App-level Gradle config
│   ├── build.gradle.kts               # Project-level Gradle config
│   └── settings.gradle.kts            # Module configuration
│
└── backend/
    └── NoteAppAPI/                    # Spring Boot REST API
        ├── src/main/kotlin/org/example/noteappapi/
        │   ├── NoteAppApiApplication.kt  # Spring Boot entry point
        │   ├── controller/            # REST endpoints
        │   ├── model/                 # Data classes
        │   ├── security/              # Firebase token validation filter
        │   ├── config/                # CORS and other configurations
        │   └── routes/                # Route definitions
        └── build.gradle.kts           # Build configuration
```

## Tech Stack

### Frontend
- **Kotlin** 2.0+ with Jetpack Compose
- **Retrofit 2** for HTTP networking
- **Firebase SDK** (Authentication, Firestore)
- **Gradle** with Kotlin DSL
- **Min SDK**: 24, **Target SDK**: 34

### Backend
- **Kotlin** 2.0+
- **Spring Boot** 3.5.6
- **Firebase Admin SDK** (token verification, Firestore access)
- **Spring Security** with custom Firebase token filter
- **Gradle** with Kotlin DSL
- **Port**: 8080

## Common Development Commands

### Frontend (Android)

```bash
cd frontend

# Build debug APK
./gradlew :app:assembleDebug

# Build release APK
./gradlew :app:assembleRelease

# Install and run on device/emulator
./gradlew :app:installDebug

# Run unit tests
./gradlew :app:testDebug

# Run instrumented tests on device/emulator
./gradlew :app:connectedAndroidTest

# Run a single unit test
./gradlew :app:testDebug --tests com.example.noteapp.ExampleUnitTest

# Clean build
./gradlew clean

# Build with verbose output for debugging
./gradlew :app:build --stacktrace
```

### Backend (Spring Boot)

```bash
cd backend/NoteAppAPI

# Run the application
./gradlew bootRun

# Build the project
./gradlew build

# Run tests
./gradlew test

# Clean build
./gradlew clean build

# Run a single test
./gradlew test --tests org.example.noteappapi.NoteAppApiApplicationTests
```

## High-Level Architecture

### Frontend Architecture: MVVM

1. **Views (Composables)**: `ui/screens/` - Pure Compose functions that render UI and call ViewModel methods
2. **ViewModels**: `viewmodel/` - Manage UI state and business logic using `StateFlow` and coroutines
3. **Repository**: `data/repository/NoteRepository` - Abstracts data access (API calls and Firestore)
4. **Remote Data**: `data/remote/ApiService` - Retrofit-based API communication
5. **Navigation**: `ui/navigation/` - Manages screen navigation with Compose Navigation

**Data Flow**: UI observes ViewModel state → User interacts → ViewModel updates Repository → Repository calls API → UI recomposes

### Backend Architecture: Spring Boot with Firebase

1. **Controllers** (`controller/`): Handle HTTP requests and route to business logic
2. **Security Filter** (`security/FirebaseTokenFilter`): Validates Firebase tokens on all requests
3. **Firestore Access**: Firebase Admin SDK queries Firestore for data persistence
4. **CORS Config** (`config/WebConfig`): Allows frontend requests

**Data Flow**: HTTP Request → Security Filter (validate token) → Controller → Firestore query → JSON response

### Authentication Flow

1. User enters credentials on `LoginScreen` or `SignUpScreen`
2. Firebase Authentication handles registration/login
3. Firebase returns ID token to frontend
4. Frontend includes token in API requests: `Authorization: Bearer {token}`
5. Backend `FirebaseTokenFilter` validates token with Firebase Admin SDK
6. User ID extracted from token and available in SecurityContext
7. API methods use user ID to filter data (user sees only their notes)

### API Endpoints

```
POST   /api/auth/verify          # Verify Firebase token
GET    /api/notes                # Get current user's notes
POST   /api/notes                # Create new note for current user
GET    /api/notes/{id}           # Get specific note (if user owns it)
DELETE /api/notes/{id}           # Delete note (if user owns it)
```

## Key Design Patterns & Conventions

### Patterns Used

- **MVVM** (Frontend): ViewModels manage state, Composables are stateless
- **Repository** (Frontend): `NoteRepository` abstracts API calls, allowing testability
- **ViewModel** (Frontend): Scoped to lifecycle, handles coroutines via `viewModelScope`
- **Sealed Classes** (Frontend): `Screen` sealed class for type-safe navigation
- **Spring Security** (Backend): Custom filter for JWT/Firebase token validation
- **Layered Architecture** (Backend): Controllers → business logic → Firestore

### Naming Conventions

- Kotlin: `camelCase` for properties/functions, `PascalCase` for classes
- Packages: `com.example.noteapp` (frontend), `org.example.noteappapi` (backend)
- Android resources: `snake_case` (drawables, colors, strings)
- ViewModels suffix: `ViewModel` (e.g., `AuthViewModel`, `NoteViewModel`)
- Screens suffix: `Screen` (e.g., `LoginScreen`, `NotesListScreen`)

### Error Handling

- **Frontend**: ViewModel catches exceptions and updates `errorMessage` state
- **Backend**: Controllers return appropriate HTTP status codes in `ResponseEntity`
- **UI Feedback**: Errors displayed in dialogs or snackbars

## Current Development Status

**Branch**: `firebase`

Recent focus:
- Implementing Firebase Authentication on both frontend and backend
- Setting up Firestore for backend data persistence
- Transitioning from local storage to cloud-based architecture

## Important Implementation Details

### Frontend State Management

ViewModels use `StateFlow` for reactive UI updates:
```kotlin
private val _uiState = MutableStateFlow<UiState>(...)
val uiState: StateFlow<UiState> = _uiState.asStateFlow()
```

State collections are observed in Composables:
```kotlin
val state by viewModel.uiState.collectAsState()
```

### Backend Firebase Integration

The `FirebaseTokenFilter` intercepts all requests:
1. Extracts token from `Authorization: Bearer {token}` header
2. Verifies with Firebase Admin SDK: `FirebaseAuth.getInstance().verifyIdToken(token)`
3. Extracts user UID from verified token
4. Stores in SecurityContext for access throughout request lifecycle

### Firestore Access Pattern

Backend uses Firebase Admin SDK to access Firestore:
```kotlin
firebaseFirestore.collection("users").document(userId)
    .collection("notes")
    .get()
    .await()
```

Frontend uses Firebase SDK directly for real-time updates where needed.

## Testing

- **Frontend Unit Tests**: `app/src/test/` - Use JUnit 5 and Kotlin Test
- **Frontend UI Tests**: `app/src/androidTest/` - Instrumented tests on device
- **Backend Tests**: `backend/NoteAppAPI/src/test/` - Spring Boot Test + JUnit 5

Run single tests with `--tests` flag (see Commands section above).

## Gradle & Build Configuration

- **JVM Target**: Java 17 across all modules
- **Kotlin Style**: Official (set in `gradle.properties`)
- **Compose Version**: Latest compatible with target SDK
- **Android Gradle Plugin**: Latest stable

## Next Steps for Development

1. Ensure Firebase credentials are properly configured (google-services.json for Android, Firebase Admin SDK credentials for backend)
2. Test full authentication flow end-to-end
3. Implement Firestore security rules to restrict user data access
4. Add unit and integration tests for critical paths
5. Optimize Compose recompositions for performance
6. Consider implementing offline-first caching on frontend
