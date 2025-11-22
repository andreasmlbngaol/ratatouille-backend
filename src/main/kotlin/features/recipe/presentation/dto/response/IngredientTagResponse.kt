package com.sukakotlin.features.recipe.presentation.dto.response

import com.sukakotlin.features.recipe.domain.model.ingredient.IngredientTag
import kotlinx.serialization.Serializable

@Serializable
data class IngredientTagDto(
    val id: Long,
    val name: String
)

@Serializable
data class IngredientTagResponse(
    val success: Boolean = true,
    val message: String? = null,
    val data: IngredientTagDto
)

@Serializable
data class ListIngredientTagResponse(
    val success: Boolean = true,
    val message: String? = null,
    val data: List<IngredientTagDto>
)


fun IngredientTag.toDto() = IngredientTagDto(
    id = this.id,
    name = this.name
)

fun IngredientTag.toResponse() = IngredientTagResponse(data = this.toDto())
fun List<IngredientTag>.toResponse() = ListIngredientTagResponse(data = this.map { it.toDto() })