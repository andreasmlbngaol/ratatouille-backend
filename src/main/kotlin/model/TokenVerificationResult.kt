package com.sukakotlin.model

data class TokenVerificationResult(
    val isValid: Boolean,
    val uid: String,
    val email: String?,
    val name: String?,
    val pictureUrl: String?,
    val isEmailVerified: Boolean,
    val errorMessage: String? = null
)