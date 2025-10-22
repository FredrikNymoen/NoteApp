# Firebase Setup Guide for NoteApp

## Quick Start Checklist

- [ ] Create Firebase Project
- [ ] Generate Backend Service Account Key
- [ ] Place firebase-key.json in backend folder
- [ ] Generate Frontend google-services.json
- [ ] Place google-services.json in frontend/app folder
- [ ] Enable Authentication in Firebase Console
- [ ] Create Firestore Database
- [ ] Configure .env variables
- [ ] Test backend with `./gradlew bootRun`
- [ ] Test frontend with Android Studio

---

## Step 1: Create Firebase Project

1. Go to [Firebase Console](https://console.firebase.google.com/)
2. Click **"Create Project"** or **"Add project"**
3. Enter project name: `NoteApp` (or your preference)
4. Continue through the wizard
5. Select or create Google Cloud project
6. Enable Google Analytics (optional)
7. Click **"Create project"** and wait for setup to complete

---

## Step 2: Backend Setup (Service Account Key)

### 2.1 Generate Service Account Key

1. Go to **Firebase Console → Project Settings** (gear icon)
2. Click **"Service Accounts"** tab
3. Make sure **Firebase Admin SDK** is selected
4. Click **"Generate New Private Key"**
5. A JSON file will download automatically

### 2.2 Add Key to Backend

1. Copy the downloaded JSON file
2. Paste it in: `backend/NoteAppAPI/firebase-key.json`
3. **IMPORTANT**: Never commit this file to Git!
   - It's already in `.gitignore` so you're protected

### 2.3 Configure Environment Variable (Optional but Recommended)

Instead of hardcoding the path, set an environment variable:

**Windows (Command Prompt):**
```cmd
set FIREBASE_SERVICE_ACCOUNT_KEY=C:\path\to\your\firebase-key.json
```

**Windows (PowerShell):**
```powershell
$env:FIREBASE_SERVICE_ACCOUNT_KEY="C:\path\to\your\firebase-key.json"
```

**Linux/Mac:**
```bash
export FIREBASE_SERVICE_ACCOUNT_KEY="/path/to/your/firebase-key.json"
```

Or add to `.env` file:
```
FIREBASE_SERVICE_ACCOUNT_KEY=firebase-key.json
```

---

## Step 3: Frontend Setup (google-services.json)

### 3.1 Register Android App in Firebase

1. Go to **Firebase Console → Project Settings**
2. Click **"Your apps"** section
3. Click **"Add app"** → Select **Android**
4. Fill in details:
   - **Package name**: `com.example.noteapp`
   - **App nickname** (optional): NoteApp
   - **SHA-1 certificate hash** (optional, leave blank for now)
5. Click **"Register app"**
6. Click **"Download google-services.json"**

### 3.2 Add google-services.json to Frontend

1. Copy the downloaded file
2. Paste it at: `frontend/app/google-services.json`
3. **IMPORTANT**: Never commit this file to Git!
   - It's already in `.gitignore` so you're protected

### 3.3 Verify Android Gradle Plugin

Make sure your `frontend/app/build.gradle.kts` has Firebase dependencies:

```kotlin
dependencies {
    // Firebase
    implementation(platform("com.google.firebase:firebase-bom:32.7.0"))
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.firebase:firebase-firestore-ktx")
}
```

---

## Step 4: Enable Authentication in Firebase

1. Go to **Firebase Console → Authentication**
2. Click **"Get started"**
3. Enable **"Email/Password"**:
   - Click Email/Password
   - Toggle "Enabled"
   - Click **"Save"**
4. (Optional) Enable **"Google"**:
   - Click Google
   - Toggle "Enabled"
   - Select your support email
   - Click **"Save"**

---

## Step 5: Create Firestore Database

1. Go to **Firebase Console → Firestore Database**
2. Click **"Create database"**
3. Choose location (pick closest to you)
4. Select **"Start in test mode"** (for development only!)
   - This allows anyone with your project ID to read/write
   - **DO NOT use in production!**
5. Click **"Create"**

### Firestore Security Rules (Important!)

Once database is created, set security rules:

1. Go to **Firestore Database → Rules**
2. Replace default rules with:

```
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /users/{userId}/notes/{noteId} {
      allow read, write: if request.auth.uid == userId;
    }
  }
}
```

Click **"Publish"**

This ensures users can only access their own notes!

---

## Step 6: Environment Variables Setup

### Create .env File

Copy the example file and fill in your values:

```bash
# Copy example to actual .env
cp .env.example .env
```

Edit `.env` and add your values:

```
FIREBASE_SERVICE_ACCOUNT_KEY=backend/NoteAppAPI/firebase-key.json
SERVER_PORT=8080
SPRING_PROFILES_ACTIVE=dev
```

**Note**: `.env` is in `.gitignore`, so it won't be committed

---

## Step 7: Test Backend

### Start Backend Server

```bash
cd backend/NoteAppAPI
./gradlew bootRun
```

You should see:
```
Started NoteAppApiApplication in X seconds
```

### Test Firebase Initialization

Once running, check for errors in console. If you see:
- ✅ No Firebase errors = configuration is correct
- ❌ File not found errors = check firebase-key.json path
- ❌ Authentication errors = check your Firebase credentials

### Test API Endpoint

```bash
# Test if server is running
curl http://localhost:8080/api/notes

# You should get an unauthorized error (expected without auth token)
```

---

## Step 8: Test Frontend

### Configure API URL

In `frontend/app/src/main/java/com/example/noteapp/data/remote/RetrofitClient.kt`:

For **Android Emulator**:
```kotlin
const val BASE_URL = "http://10.0.2.2:8080/api"
```

For **Real Device** (replace with your machine's IP):
```kotlin
const val BASE_URL = "http://192.168.1.100:8080/api"
```

For **Production**:
```kotlin
const val BASE_URL = "https://your-api.com/api"
```

### Run Android App

1. Open `frontend/` in Android Studio
2. Make sure `google-services.json` is in `app/` folder
3. Click **Run** or press `Shift + F10`
4. Test login/signup flow

---

## File Structure After Setup

```
NoteApp/
├── .env                                    # Your environment variables (not committed)
├── .env.example                           # Example for team members
├── .gitignore                             # Includes .env and firebase-key.json
│
├── backend/NoteAppAPI/
│   ├── firebase-key.json                  # Service account key (not committed)
│   ├── .env                               # Backend env vars (not committed)
│   ├── .env.example                       # Example for team members
│   └── src/main/resources/
│       └── application.properties         # References env variables
│
└── frontend/app/
    ├── google-services.json               # Google services config (not committed)
    └── .env.example                       # Example for team members
```

---

## Common Issues & Troubleshooting

### Issue: "firebase-key.json not found"
**Solution**:
- Verify the file exists at `backend/NoteAppAPI/firebase-key.json`
- Check FIREBASE_SERVICE_ACCOUNT_KEY env variable is set correctly
- Restart your IDE/terminal after setting env variables

### Issue: "Invalid service account"
**Solution**:
- Download a new private key from Firebase Console
- Replace old `firebase-key.json` with new one

### Issue: Android app crashes on login
**Solution**:
- Verify `google-services.json` is in `frontend/app/`
- Make sure Authentication is enabled in Firebase Console
- Check that Email/Password auth method is enabled

### Issue: "CORS errors" from frontend
**Solution**:
- Backend CORS is configured in `SecurityConfig.kt`
- Make sure backend is running on correct port (8080)
- For emulator use: `http://10.0.2.2:8080/api`
- For real device: use your machine's local IP address

### Issue: Firestore rules rejected requests
**Solution**:
- Make sure auth token is sent in requests
- Check that user is authenticated (not null)
- Update Firestore security rules as shown in Step 5

---

## Security Checklist

✅ `.env` and `firebase-key.json` are in `.gitignore`
✅ Firestore rules restrict access to user's own data
✅ Authentication is enabled on backend (`FirebaseTokenFilter`)
✅ CORS is configured to allow frontend requests
✅ Test mode is for development only

**Before Production:**
- [ ] Update Firestore security rules (don't use test mode)
- [ ] Set up proper API rate limiting
- [ ] Configure CORS for production domain only
- [ ] Enable App Check in Firebase Console
- [ ] Use environment-specific configurations

---

## Next Steps

1. ✅ Complete Firebase setup above
2. ✅ Test backend: `./gradlew bootRun`
3. ✅ Test frontend with Android emulator
4. ✅ Test full auth flow (signup → login → create note)
5. ✅ Check Firestore Console to verify data is saved
6. ✅ Ready for development!

---

## Useful Firebase Console Links

- [Firebase Console](https://console.firebase.google.com/)
- [Project Settings](https://console.firebase.google.com/project/_/settings/general/)
- [Authentication](https://console.firebase.google.com/project/_/authentication/users)
- [Firestore Database](https://console.firebase.google.com/project/_/firestore/data)
- [Service Accounts](https://console.firebase.google.com/project/_/settings/serviceaccounts/adminsdk)

(Replace `_` with your project ID)

---

## Support

For Firebase documentation, visit:
- [Firebase Android Setup](https://firebase.google.com/docs/android/setup)
- [Firebase Authentication](https://firebase.google.com/docs/auth)
- [Cloud Firestore](https://firebase.google.com/docs/firestore)
