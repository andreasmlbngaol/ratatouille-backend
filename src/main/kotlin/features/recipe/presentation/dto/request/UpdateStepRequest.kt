package com.sukakotlin.features.recipe.presentation.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class UpdateStepRequest(
    val content: String
)
