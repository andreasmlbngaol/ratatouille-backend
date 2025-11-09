package com.sukakotlin.config

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import io.ktor.server.application.Application
import io.ktor.server.application.log
import java.io.FileInputStream

fun Application.configureFirebase() {
    if(FirebaseApp.getApps().isNotEmpty()) {
        return log.info("Firebase App already initialized")
    }

    try {
        val serviceAccountPath = environment.config.propertyOrNull("firebase.serviceAccountPath")?.getString()
            ?: "serviceAccountKey.json"
        val serviceAccount = FileInputStream(serviceAccountPath)

        val options = FirebaseOptions.builder()
            .setCredentials(GoogleCredentials.fromStream(serviceAccount))
            .build()

        FirebaseApp.initializeApp(options)
        log.info("Firebase App initialized successfully")
    } catch (e: Exception) {
        log.error("Failed to initialize Firebase App", e)
        throw e
    }
}