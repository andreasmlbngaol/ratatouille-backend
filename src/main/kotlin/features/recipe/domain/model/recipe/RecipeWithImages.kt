package com.sukakotlin.features.recipe.domain.model.recipe

import com.sukakotlin.features.recipe.domain.model.Image
import kotlinx.datetime.LocalDateTime

data class RecipeWithImages(
    val id: Long,
    val authorId: String,
    val name: String,
    val description: String?,
    val isPublic: Boolean,
    val estTimeInMinutes: Int,
    val portion: Int,
    val status: RecipeStatus,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
    val images: List<Image>
)
