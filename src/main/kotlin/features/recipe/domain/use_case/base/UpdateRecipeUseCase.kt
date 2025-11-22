package com.sukakotlin.features.recipe.domain.use_case.base

import com.sukakotlin.features.recipe.domain.model.recipe.RecipeStatus
import com.sukakotlin.features.recipe.domain.model.recipe.RecipeWithImages
import com.sukakotlin.features.recipe.domain.repository.RecipesRepository
import org.slf4j.LoggerFactory

class UpdateRecipeUseCase(
    private val recipesRepository: RecipesRepository
) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    suspend operator fun invoke(
        userId: String,
        recipeId: Long,
        name: String?,
        description: String?,
        isPublic: Boolean?,
        estTimeInMinutes: Int?,
        portion: Int?,
        status: RecipeStatus?
    ): Result<RecipeWithImages> {
        return try {
            if(name == null && description == null && isPublic == null && estTimeInMinutes == null && portion == null && status == null) {
                logger.error("No fields to update")
                return Result.failure(IllegalArgumentException("No fields to update"))
            }
            estTimeInMinutes?.let { time ->
                if (time <= 0) {
                    logger.error("Estimated time must be greater than 0")
                    return Result.failure(IllegalArgumentException("Estimated time must be greater than 0"))
                }
            }

            portion?.let { p ->
                if (p <= 0) {
                    logger.error("Portion must be greater than 0")
                    return Result.failure(IllegalArgumentException("Portion must be greater than 0"))
                }
            }

            val recipe = recipesRepository.findByIdAndAuthorId(recipeId, userId)
                ?: return Result.failure(IllegalArgumentException("Recipe with id $recipeId and author $userId not found"))

            val updatedRecipe = recipe.copy(
                id = recipeId,
                authorId = userId,
                name = name ?: recipe.name,
                description = description ?: recipe.description,
                isPublic = isPublic ?: recipe.isPublic,
                estTimeInMinutes = estTimeInMinutes ?: recipe.estTimeInMinutes,
                portion = portion ?: recipe.portion,
                status = status ?: recipe.status
            )

            val updated = recipesRepository.update(recipeId, updatedRecipe)
                ?: return Result.failure(IllegalStateException("Failed to update recipe"))

            logger.info("Updated recipe $updated")
            Result.success(updated)
        } catch (e: Exception) {
            return Result.failure(e)
        }
    }
}