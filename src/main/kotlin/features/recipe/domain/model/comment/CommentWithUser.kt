package com.sukakotlin.features.recipe.domain.model.comment

import com.sukakotlin.features.user.domain.model.auth.User

data class CommentWithUser(
    val comment: Comment,
    val user: User
)
