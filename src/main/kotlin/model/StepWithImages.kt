package com.sukakotlin.model

import kotlinx.serialization.Serializable

@Serializable
data class StepWithImages(
    val id: Long,
    val recipeId: Long,
    val stepNumber: Int,
    val content: String,
    val images: List<Image>
)