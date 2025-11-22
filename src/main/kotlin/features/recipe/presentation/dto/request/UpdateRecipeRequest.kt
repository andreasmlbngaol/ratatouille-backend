package com.sukakotlin.features.recipe.presentation.dto.request

import com.sukakotlin.features.recipe.domain.model.recipe.RecipeStatus
import kotlinx.serialization.Serializable

@Serializable
data class UpdateRecipeRequest(
    val name: String? = null,
    val description: String? = null,
    val isPublic: Boolean? = null,
    val estTimeInMinutes: Int? = null,
    val portion: Int? = null,
    val status: RecipeStatus? = null
)
