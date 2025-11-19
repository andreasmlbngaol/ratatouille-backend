package com.sukakotlin.features.recipe.domain.use_case.steps

import com.sukakotlin.features.recipe.domain.model.StepWithImages
import com.sukakotlin.features.recipe.domain.repository.RecipesRepository
import org.slf4j.LoggerFactory

class CreateEmptyStepUseCase(
    private val recipesRepository: RecipesRepository
) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    suspend operator fun invoke(
        userId: String,
        recipeId: Long,
        stepNumber: Int
    ): Result<List<StepWithImages>> {
        return try {
            val steps = recipesRepository.addStep(
                recipeId = recipeId,
                stepNumber = stepNumber,
                content = ""
            )
            Result.success(steps)
        } catch (e: Exception) {
            logger.error("Failed to create empty step", e)
            return Result.failure(e)
        }
    }
}