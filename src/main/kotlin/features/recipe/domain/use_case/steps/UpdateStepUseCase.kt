package com.sukakotlin.features.recipe.domain.use_case.steps

import com.sukakotlin.features.recipe.domain.model.StepWithImages
import com.sukakotlin.features.recipe.domain.repository.RecipesRepository
import org.slf4j.LoggerFactory

class UpdateStepUseCase(
    private val recipesRepository: RecipesRepository
) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    suspend operator fun invoke(
        userId: String,
        recipeId: Long,
        stepId: Long,
        content: String
    ): Result<List<StepWithImages>> {
        return try {
            recipesRepository.findByIdAndAuthorId(recipeId, userId)
                ?: return Result.failure(IllegalArgumentException("Recipe with id $recipeId and author $userId not found"))

            val stepsWithImages = recipesRepository.updateStep(recipeId, stepId, content)
            Result.success(stepsWithImages)
        } catch (e: Exception) {
            logger.error("Failed to update step", e)
            return Result.failure(e)
        }
    }
}