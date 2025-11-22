package com.sukakotlin.features.recipe.domain.model.step

data class Step(
    val id: Long,
    val recipeId: Long,
    val stepNumber: Int,
    val content: String
)