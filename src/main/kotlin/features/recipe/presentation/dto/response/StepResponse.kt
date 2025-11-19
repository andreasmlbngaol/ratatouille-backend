package com.sukakotlin.features.recipe.presentation.dto.response

import com.sukakotlin.features.recipe.domain.model.Step
import com.sukakotlin.features.recipe.domain.model.StepWithImages
import kotlinx.serialization.Serializable

@Serializable
data class StepDto(
    val id: Long,
    val recipeId: Long,
    val stepNumber: Int,
    val content: String
)

@Serializable
data class StepResponse(
    val success: Boolean = true,
    val message: String? = null,
    val data: StepDto
)

fun Step.toDto() = StepDto(
    id = this.id,
    recipeId = this.recipeId,
    stepNumber = this.stepNumber,
    content = this.content
)

fun Step.toResponse() = StepResponse(data = this.toDto())

@Serializable
data class StepWithImagesDto(
    val step: StepDto,
    val images: List<ImageDto>
)

fun StepWithImages.toDto() = StepWithImagesDto(
    step = this.step.toDto(),
    images = this.images.map { it.toDto() }
)

@Serializable
data class StepWithImagesResponse(
    val success: Boolean = true,
    val message: String? = null,
    val data: StepWithImagesDto
)

fun StepWithImages.toResponse() = StepWithImagesResponse(data = this.toDto())