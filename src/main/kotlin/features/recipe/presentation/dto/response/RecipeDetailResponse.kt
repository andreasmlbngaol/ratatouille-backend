package com.sukakotlin.features.recipe.presentation.dto.response

import com.sukakotlin.features.recipe.domain.model.recipe.RecipeDetail
import kotlinx.serialization.Serializable

@Serializable
data class RecipeDetailDto(
    val recipe: RecipeWithImagesDto,
    val ingredients: List<IngredientWithTagDto>,
    val steps: List<StepWithImagesDto>
)

fun RecipeDetail.toDto() = RecipeDetailDto(
    recipe = this.recipe.toDto(),
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