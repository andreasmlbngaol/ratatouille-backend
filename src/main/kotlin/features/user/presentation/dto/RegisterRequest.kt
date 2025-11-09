package com.sukakotlin.features.user.presentation.dto

import kotlinx.serialization.Serializable

@Serializable
data class RegisterRequest(
    val uid: String,
    val idToken: String,
    val name: String,
    val email: String,
    val profilePictureUrl: String?,
)