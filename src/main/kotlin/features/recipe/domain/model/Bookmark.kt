package com.sukakotlin.features.recipe.domain.model

import java.time.LocalDateTime

data class Bookmark(
    val id: Long,
    val userId: String,
    val recipeId: Long,
    val createdAt: LocalDateTime
)
