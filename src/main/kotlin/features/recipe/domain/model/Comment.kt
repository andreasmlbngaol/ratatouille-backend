package com.sukakotlin.features.recipe.domain.model

import com.sukakotlin.features.user.domain.model.auth.User

data class Comment(
    val id: Long,
    val recipeId: Long,
    val userId: String,
    val content: String
)

data class CommentWithUser(
    val comment: Comment,
    val user: User
)
