package com.sukakotlin.features.user.presentation.util

import com.sukakotlin.features.user.domain.model.User
import com.sukakotlin.features.user.presentation.dto.UserDto

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