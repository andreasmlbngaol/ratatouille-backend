package com.sukakotlin.features.user.presentation.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class UpdateProfileRequest(
    val name: String? = null,
    val bio: String? = null
)