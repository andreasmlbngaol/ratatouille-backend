package com.sukakotlin.features.user.presentation.dto

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class UserDto(
    val id: String,
    val name: String,
    val email: String,
    val profilePictureUrl: String?,
    val coverPictureUrl: String?,
    val bio: String?,
    val isEmailVerified: Boolean,
    val createdAt: LocalDateTime
)
