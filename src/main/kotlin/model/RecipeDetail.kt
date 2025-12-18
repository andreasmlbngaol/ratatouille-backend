package com.sukakotlin.model

import kotlinx.serialization.Serializable

@Serializable
data class RecipeDetail(
    val author: User,
    val recipe: RecipeWithImages,
    val ingredients: List<IngredientWithTag>,
    val steps: List<StepWithImages>,
    val comments: List<CommentWithImage>,
    val rating: RecipeRating,
    val isFavorited: Boolean?,
    val favoriteCount: Long
)