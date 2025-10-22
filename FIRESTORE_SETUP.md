# Firestore Database Setup Guide

This guide explains how to set up the Firestore database structure and security rules for the NoteApp application.

## Database Collections Structure

The application uses two main collections in Firestore:

### 1. `users` Collection

Stores user profile information created during signup.

**Document Structure:**
```
users/
├── {userId}/
│   ├── uid: string          # Firebase UID (same as document ID)
│   ├── name: string         # User's full name (from signup form)
│   ├── email: string        # User's email address
│   └── createdAt: timestamp # Account creation timestamp
```

**Example:**
```json
{
  "uid": "abc123def456",
  "name": "Per Olsen",
  "email": "per@example.com",
  "createdAt": "2025-10-22T10:30:00Z"
}
```

### 2. `notes` Collection

Stores user notes.

**Document Structure:**
```
notes/
├── {noteId}/
│   ├── id: string           # Note ID (same as document ID)
│   ├── userId: string       # Reference to user who created the note
│   ├── title: string        # Note title
│   ├── content: string      # Note content
│   ├── createdAt: timestamp # Creation timestamp
│   └── updatedAt: timestamp # Last update timestamp
```

**Example:**
```json
{
  "id": "note123",
  "userId": "abc123def456",
  "title": "Kjøpeliste",
  "content": "Melk, brød, ost",
  "createdAt": "2025-10-22T09:00:00Z",
  "updatedAt": "2025-10-22T10:00:00Z"
}
```

## Firestore Security Rules

Security rules control who can read, write, and delete data in Firestore. Follow these steps to set up the rules:

### Step 1: Access Firestore Security Rules

1. Go to [Firebase Console](https://console.firebase.google.com/)
2. Select your project
3. Click **Firestore Database** in the left sidebar
4. Click the **Rules** tab at the top

### Step 2: Replace with These Security Rules

```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    // Allow authenticated users to read and write their own user document
    match /users/{userId} {
      allow read, write: if request.auth.uid == userId;
    }

    // Allow authenticated users to read any user document (for fetching usernames)
    match /users/{document=**} {
      allow read: if request.auth != null;
    }

    // Allow authenticated users to read and write their own notes
    match /notes/{noteId} {
      allow read, write: if request.auth.uid == resource.data.userId;
    }

    // Allow authenticated users to create new notes
    match /notes/{document=**} {
      allow create: if request.auth != null;
    }
  }
}
```

### Step 3: Publish the Rules

1. Click **Publish** button in the top right
2. Click **Publish** in the confirmation dialog
3. Wait for the rules to be deployed (usually takes a few seconds)

## Rule Explanations

### Users Collection Rules

```javascript
// Users can only read/write their own user document
match /users/{userId} {
  allow read, write: if request.auth.uid == userId;
}

// Any authenticated user can read all user documents (to fetch usernames)
match /users/{document=**} {
  allow read: if request.auth != null;
}
```

- **First rule**: Only the user themselves can read/write their own profile
- **Second rule**: Any logged-in user can read all user documents (needed to display author names on notes)

### Notes Collection Rules

```javascript
// Users can read/write notes they own
match /notes/{noteId} {
  allow read, write: if request.auth.uid == resource.data.userId;
}

// Any authenticated user can create new notes
match /notes/{document=**} {
  allow create: if request.auth != null;
}
```

- **First rule**: Users can only read/write notes they created
- **Second rule**: Logged-in users can create new notes

## Application Data Flow

### During Signup (Frontend)

1. User enters name, email, and password
2. Firebase Auth creates account
3. Frontend creates user document in `users` collection with user's name
4. User is redirected to Notes screen

### When Creating a Note (Backend → Frontend)

1. Frontend sends note data to backend API
2. Backend creates note in Firestore `notes` collection
3. Frontend fetches notes from backend API
4. When displaying each note, frontend fetches author name from `users` collection using the note's `userId`

### When Displaying Notes (Frontend)

1. Frontend fetches notes from backend API
2. For each note, frontend queries `users` collection to get the author's name
3. NoteItem composable displays: note title, content, and author name

## Troubleshooting

### Issue: "PERMISSION_DENIED" during signup

**Cause**: Firestore security rules don't allow writing to the `users` collection

**Solution**:
1. Verify the security rules are correctly published
2. Ensure the rules include the `users` collection write permissions
3. Check that `request.auth.uid == userId` in the write rule

### Issue: Author names show as "Ukjent bruker" (Unknown user)

**Cause**: Frontend can't read user documents from `users` collection

**Solution**:
1. Verify the security rules include the read rule for all user documents
2. Check network tab in browser dev tools or Logcat to see if Firestore reads are failing
3. Ensure the user document was created during signup

### Issue: Users can see other users' notes

**Cause**: Security rules allow users to read all notes

**Solution**: Ensure the notes read rule includes `request.auth.uid == resource.data.userId`

### Issue: "FAILED_PRECONDITION" error

**Cause**: Firestore database is not initialized

**Solution**:
1. Go to Firestore Console
2. Click "Create Database"
3. Choose a region (same region as your backend is recommended)
4. Select "Start in production mode"
5. Click "Create"

## Testing Security Rules

You can test your security rules using the Firebase Console:

1. Click the **Rules** tab in Firestore Console
2. Click **Simulate** in the top right
3. Enter test conditions (collection, document, operation)
4. Click **Run** to see if the operation is allowed

Example test:
- **Collection**: `users`
- **Document**: `test-user-123`
- **Operation**: `write`
- **Request auth UID**: `test-user-123`
- Expected: **Allow** ✓

## Next Steps

1. Create Firestore database (if not already created)
2. Apply the security rules from Step 2 above
3. Test signup flow:
   - Sign up with a test account
   - Check Firestore Console to verify user document was created
   - Check that user name displays correctly on notes
4. Test note creation:
   - Create a few test notes
   - Verify they appear in your notes list with correct author names
   - Verify you can only see notes you created
