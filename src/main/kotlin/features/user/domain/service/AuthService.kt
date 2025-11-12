package com.sukakotlin.features.user.domain.service

import com.sukakotlin.features.user.domain.model.auth.TokenVerificationResult

interface AuthService {
    suspend fun verifyIdToken(idToken: String): TokenVerificationResult
}

