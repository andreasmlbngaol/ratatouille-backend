package com.sukakotlin.model

import kotlinx.serialization.Serializable

@Serializable
data class IngredientWithTag(
    val id: Long,
    val recipeId: Long,
    val amount: Double?,
    val unit: String?,
    val alternative: String?,
    val tag: IngredientTag
)
