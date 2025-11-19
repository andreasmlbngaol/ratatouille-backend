package com.sukakotlin.features.recipe.presentation.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class CreateStepRequest(
    val stepNumber: Int
)
