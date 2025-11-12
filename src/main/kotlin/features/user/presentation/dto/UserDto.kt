package com.sukakotlin.features.user.presentation.dto

import com.sukakotlin.features.user.domain.model.auth.User
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

fun User.toDto() = UserDto(
    id = this.id,
    name = this.name,
    email = this.email,
    profilePictureUrl = this.profilePictureUrl,
    coverPictureUrl = this.coverPictureUrl,
    bio = this.bio,
    isEmailVerified = this.isEmailVerified,
    createdAt = this.createdAt
)