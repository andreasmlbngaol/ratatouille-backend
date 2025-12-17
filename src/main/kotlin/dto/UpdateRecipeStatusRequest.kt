package com.sukakotlin.dto

import com.sukakotlin.model.RecipeStatus
import kotlinx.serialization.Serializable

@Serializable
data class UpdateRecipeStatusRequest(
    val status: RecipeStatus
)