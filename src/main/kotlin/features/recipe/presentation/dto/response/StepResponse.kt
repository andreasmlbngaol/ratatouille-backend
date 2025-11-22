package com.sukakotlin.features.recipe.presentation.dto.response

import com.sukakotlin.features.recipe.domain.model.step.Step
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

