package com.sukakotlin.features.user.domain.model

import kotlinx.datetime.LocalDateTime

data class User(
    val id: String,
    val name: String,
    val email: String,
    val profilePictureUrl: String?,
    val coverPictureUrl: String?,
    val bio: String?,
    val isEmailVerified: Boolean,
    val createdAt: LocalDateTime
)