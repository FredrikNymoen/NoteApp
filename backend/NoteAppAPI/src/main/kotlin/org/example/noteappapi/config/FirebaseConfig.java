package org.example.noteappapi.config;

// FirebaseConfig.kt

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.io.FileInputStream;
import javax.annotation.PostConstruct;

@Configuration
class FirebaseConfig {

    @PostConstruct
    fun initialize() {
        try {
            // Last ned serviceAccountKey.json fra Firebase Console
            val serviceAccount = FileInputStream("path/to/serviceAccountKey.json")

            val options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build()

            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
            }
        } catch (e: Exception) {
            e.printStackTrace();
        }
    }

    @Bean
    fun firebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Bean
    fun firestore() = FirestoreClient.getFirestore()
}

