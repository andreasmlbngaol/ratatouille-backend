package com.sukakotlin.model

import kotlinx.serialization.Serializable

@Serializable
data class RecipeRating(
    val average: Double,
    val count: Int
)