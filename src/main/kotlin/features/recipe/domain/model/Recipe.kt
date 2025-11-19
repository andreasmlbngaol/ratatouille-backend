package com.sukakotlin.features.recipe.domain.model

import kotlinx.datetime.LocalDateTime

data class Recipe(
    val id: Long,
    val authorId: String,
    val name: String,
    val description: String?,
    val isPublic: Boolean,
    val estTimeInMinutes: Int,
    val portion: Int,
    val status: RecipeStatus,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)
