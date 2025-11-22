package com.sukakotlin.features.recipe.presentation.dto.response

import com.sukakotlin.features.recipe.domain.model.ingredient.IngredientWithTag
import kotlinx.serialization.Serializable

@Serializable
data class IngredientWithTagDto(
    val id: Long,
    val recipeId: Long,
    val amount: Double?,
    val unit: String?,
    val alternative: String?,
    val tag: IngredientTagDto
)

fun IngredientWithTag.toDto() = IngredientWithTagDto(
    id = this.id,
    recipeId = this.recipeId,
    amount = this.amount,
    unit = this.unit,
    alternative = this.alternative,
    tag = this.tag.toDto(),
)

@Serializable
data class IngredientWithTagResponse(
    val success: Boolean = true,
    val message: String? = null,
    val data: IngredientWithTagDto
)
fun IngredientWithTag.toResponse() = IngredientWithTagResponse(data = this.toDto())

@Serializable
data class ListIngredientWithTagResponse(
    val success: Boolean = true,
    val message: String? = null,
    val data: List<IngredientWithTagDto>
)
fun List<IngredientWithTag>.toResponse() = ListIngredientWithTagResponse(data = this.map { it.toDto() })