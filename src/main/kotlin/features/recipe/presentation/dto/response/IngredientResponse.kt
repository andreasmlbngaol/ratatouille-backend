package com.sukakotlin.features.recipe.presentation.dto.response

import com.sukakotlin.features.recipe.domain.model.ingredient.Ingredient
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