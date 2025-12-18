package com.sukakotlin.model

import kotlinx.serialization.Serializable

@Serializable
data class CommentWithImage(
    val id: Long,
    val recipeId: Long,
    val author: User,
    val content: String,
    val createdAt: Long,
    val image: Image?
)