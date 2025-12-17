package com.sukakotlin.dto

import kotlinx.serialization.Serializable

@Serializable
data class UpdateRecipeRequest(
    val name: String,
    val description: String? = null,
    val isPublic: Boolean,
    val estTimeInMinutes: Int,
    val portion: Int
)