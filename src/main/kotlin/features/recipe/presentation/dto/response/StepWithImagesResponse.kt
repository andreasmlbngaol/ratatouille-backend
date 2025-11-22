package com.sukakotlin.features.recipe.presentation.dto.response

import com.sukakotlin.features.recipe.domain.model.step.StepWithImages
import kotlinx.serialization.Serializable

@Serializable
data class StepWithImagesDto(
    val id: Long,
    val recipeId: Long,
    val stepNumber: Int,
    val content: String,
    val images: List<ImageDto>
)

fun StepWithImages.toDto() = StepWithImagesDto(
    id = this.id,
    recipeId = this.recipeId,
    stepNumber = this.stepNumber,
    content = this.content,
    images = this.images.map { it.toDto() },
)

@Serializable
data class StepWithImagesResponse(
    val success: Boolean = true,
    val message: String? = null,
    val data: StepWithImagesDto
)

fun StepWithImages.toResponse() = StepWithImagesResponse(data = this.toDto())

@Serializable
data class ListStepWithImagesResponse(
    val success: Boolean = true,
    val message: String? = null,
    val data: List<StepWithImagesDto>
)

fun List<StepWithImages>.toResponse() = ListStepWithImagesResponse(data = this.map { it.toDto() })