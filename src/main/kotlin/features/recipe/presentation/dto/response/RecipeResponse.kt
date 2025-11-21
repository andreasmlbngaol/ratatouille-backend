package com.sukakotlin.features.recipe.presentation.dto.response

import com.sukakotlin.features.recipe.domain.model.Recipe
import com.sukakotlin.features.recipe.domain.model.RecipeDetail
import com.sukakotlin.features.recipe.domain.model.RecipeStatus
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
data class RecipeDetailDto(
    val recipe: RecipeDto,
    val images: List<ImageDto>,
    val ingredients: List<IngredientWithTagDto>,
    val steps: List<StepWithImagesDto>
)

fun RecipeDetail.toDto() = RecipeDetailDto(
    recipe = this.recipe.toDto(),
    images = this.images.map { it.toDto() },
    ingredients = this.ingredients.map { it.toDto() },
    steps = this.steps.map { it.toDto() }
)

@Serializable
data class RecipeDetailResponse(
    val success: Boolean = true,
    val message: String? = null,
    val data: RecipeDetailDto
)

fun RecipeDetail.toResponse(
    message: String? = null
) = RecipeDetailResponse(message = message, data = this.toDto())