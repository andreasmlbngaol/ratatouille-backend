package com.sukakotlin.features.recipe.domain.model

data class Rating(
    val id: Long,
    val recipeId: Long,
    val userId: String,
    val value: Double
)