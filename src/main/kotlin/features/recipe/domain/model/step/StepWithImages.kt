package com.sukakotlin.features.recipe.domain.model.step

import com.sukakotlin.features.recipe.domain.model.Image

data class StepWithImages(
    val id: Long,
    val recipeId: Long,
    val stepNumber: Int,
    val content: String,
    val images: List<Image>
)