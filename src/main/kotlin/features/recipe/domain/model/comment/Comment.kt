package com.sukakotlin.features.recipe.domain.model.comment


data class Comment(
    val id: Long,
    val recipeId: Long,
    val userId: String,
    val content: String
)