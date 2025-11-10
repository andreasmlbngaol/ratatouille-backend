package com.sukakotlin.config

import com.google.firebase.FirebaseApp
import com.kborowy.authprovider.firebase.firebase
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.UserIdPrincipal

fun Application.configureSecurity() {
    install(Authentication) {
        firebase("firebase-auth") {
            realm = "Ratatouille App API"

            setup {
                firebaseApp = FirebaseApp.getInstance()
            }

            validate { token ->
                UserIdPrincipal(token.uid)
            }
        }
    }
}