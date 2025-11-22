package com.sukakotlin.features.recipe.domain.model.ingredient

data class IngredientWithTag(
    val id: Long,
    val recipeId: Long,
    val amount: Double?,
    val unit: String?,
    val alternative: String?,
    val tag: IngredientTag
)
