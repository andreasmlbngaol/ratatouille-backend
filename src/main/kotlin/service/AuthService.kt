package com.sukakotlin.service

import com.google.firebase.auth.FirebaseAuth
import com.sukakotlin.model.TokenVerificationResult

class AuthService {
    fun verifyIdToken(idToken: String) = try {
        val decodedToken = FirebaseAuth.getInstance()
            .verifyIdToken(idToken)

        TokenVerificationResult(
            isValid = true,
            uid = decodedToken.uid,
            email = decodedToken.email,
            name = decodedToken.name,
            pictureUrl = decodedToken.picture,
            isEmailVerified = decodedToken.isEmailVerified,
            errorMessage = null
        )
    } catch (e: Exception) {
        TokenVerificationResult(
            isValid = false,
            uid = "",
            email = null,
            name = null,
            pictureUrl = null,
            isEmailVerified = false,
            errorMessage = e.message
        )
    }
}