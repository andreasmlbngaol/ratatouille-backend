package com.sukakotlin.features.recipe.presentation.dto.response

import com.sukakotlin.features.recipe.domain.model.IngredientTag
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

fun IngredientTag.toDto() = IngredientTagDto(
    id = this.id,
    name = this.name
)

fun IngredientTag.toResponse() = IngredientTagResponse(data = this.toDto())