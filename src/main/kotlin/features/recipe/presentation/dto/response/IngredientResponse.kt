package com.sukakotlin.features.recipe.presentation.dto.response

import com.sukakotlin.features.recipe.domain.model.Ingredient
import com.sukakotlin.features.recipe.domain.model.IngredientWithTag
import kotlinx.serialization.Serializable

@Serializable
data class IngredientDto(
    val id: Long,
    val recipeId: Long,
    val tagId: Long,
    val amount: Double?,
    val unit: String?,
    val alternative: String?
)

fun Ingredient.toDto() = IngredientDto(
    id = this.id,
    recipeId = this.recipeId,
    tagId = this.tagId,
    amount = this.amount,
    unit = this.unit,
    alternative = this.alternative
)

@Serializable
data class IngredientWithTagDto(
    val ingredient: IngredientDto,
    val tag: IngredientTagDto
)

fun IngredientWithTag.toDto() = IngredientWithTagDto(
    ingredient = this.ingredient.toDto(),
    tag = this.tag.toDto()
)

@Serializable
data class IngredientWithTagResponse(
    val success: Boolean = true,
    val message: String? = null,
    val data: IngredientWithTagDto
)

fun IngredientWithTag.toResponse() = IngredientWithTagResponse(data = this.toDto())