package com.sukakotlin.features.recipe.domain.model.ingredient

data class Ingredient(
    val id: Long,
    val recipeId: Long,
    val tagId: Long,
    val amount: Double?,
    val unit: String?,
    val alternative: String?
)