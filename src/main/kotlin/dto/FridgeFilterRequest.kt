package com.sukakotlin.dto

import kotlinx.serialization.Serializable

@Serializable
data class FridgeFilterRequest(
    val includedIngredients: List<Long> = emptyList(),
    val excludedIngredients: List<Long> = emptyList(),
    val minRating: Double? = null,
    val minEstTime: Int? = null,
    val maxEstTime: Int? = null
)