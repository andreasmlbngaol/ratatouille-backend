package com.sukakotlin.features.user.domain.service

import com.sukakotlin.features.user.domain.model.TokenVerificationResult

interface AuthService {
    suspend fun verifyIdToken(idToken: String): TokenVerificationResult
}

