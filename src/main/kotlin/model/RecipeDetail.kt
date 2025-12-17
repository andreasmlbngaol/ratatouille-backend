package com.sukakotlin.model

import kotlinx.serialization.Serializable

@Serializable
data class RecipeDetail(
    val recipe: RecipeWithImages,
    val ingredients: List<IngredientWithTag>,
    val steps: List<StepWithImages>
)