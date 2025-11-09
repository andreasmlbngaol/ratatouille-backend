package com.sukakotlin.features.user.data.service

import com.google.firebase.auth.FirebaseAuth
import com.sukakotlin.features.user.domain.model.TokenVerificationResult
import com.sukakotlin.features.user.domain.service.AuthService

class AuthServiceImpl: AuthService {
    override suspend fun verifyIdToken(idToken: String): TokenVerificationResult {
        return try {
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
}