package com.sukakotlin.model

import kotlinx.serialization.Serializable

@Serializable
data class Recipe(
    val id: Long,
    val authorId: String,
    val name: String,
    val description: String?,
    val isPublic: Boolean,
    val estTimeInMinutes: Int,
    val portion: Int,
    val status: RecipeStatus,
    val createdAt: Long,
    val updatedAt: Long
)
