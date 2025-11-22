package com.sukakotlin.features.recipe.presentation.dto.response

import com.sukakotlin.features.recipe.domain.model.recipe.Recipe
import com.sukakotlin.features.recipe.domain.model.recipe.RecipeStatus
import com.sukakotlin.features.recipe.domain.model.recipe.RecipeWithImages
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class RecipeDto(
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

@Serializable
data class RecipeResponse(
    val success: Boolean = true,
    val message: String? = null,
    val data: RecipeDto
)

fun Recipe.toDto() = RecipeDto(
    id = this.id,
    authorId = this.authorId,
    name = this.name,
    description = this.description,
    isPublic = this.isPublic,
    estTimeInMinutes = this.estTimeInMinutes,
    portion = this.portion,
    status = this.status,
    createdAt = this.createdAt,
    updatedAt = this.updatedAt
)

fun Recipe.toResponse() = RecipeResponse(data = this.toDto())

@Serializable
data class RecipeWithImagesDto(
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
    val images: List<ImageDto>
)

fun RecipeWithImages.toDto() = RecipeWithImagesDto(
    id = this.id,
    authorId = this.authorId,
    name = this.name,
    description = this.description,
    isPublic = this.isPublic,
    estTimeInMinutes = this.estTimeInMinutes,
    portion = this.portion,
    status = this.status,
    createdAt = this.createdAt,
    updatedAt = this.updatedAt,
    images = this.images.map { it.toDto() },
)

@Serializable
data class RecipeWithImagesResponse(
    val success: Boolean = true,
    val message: String? = null,
    val data: RecipeWithImagesDto
)

fun RecipeWithImages.toResponse() = RecipeWithImagesResponse(data = this.toDto())