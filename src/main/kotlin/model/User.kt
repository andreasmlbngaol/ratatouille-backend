package com.sukakotlin.model

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: String,
    val name: String,
    val email: String,
    val profilePictureUrl: String?,
    val coverPictureUrl: String?,
    val bio: String?,
    val isEmailVerified: Boolean,
    val createdAt: Long,
    val updatedAt: Long
)

val emptyUser = User("", "", "", null, null, null, false, 0, 0)