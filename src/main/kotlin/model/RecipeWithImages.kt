package com.sukakotlin.model

import kotlinx.serialization.Serializable


@Serializable
data class RecipeWithImages(
    val id: Long,
    val authorId: String,
    val name: String,
    val description: String?,
    val isPublic: Boolean,
    val estTimeInMinutes: Int,
    val portion: Int,
    val status: RecipeStatus,
    val createdAt: Long,
    val updatedAt: Long,
    val images: List<Image>
)
