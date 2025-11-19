package com.sukakotlin.features.recipe.domain.use_case

import com.sukakotlin.features.recipe.domain.model.Recipe
import com.sukakotlin.features.recipe.domain.model.RecipeStatus
import com.sukakotlin.features.recipe.domain.repository.RecipesRepository
import com.sukakotlin.shared.util.now
import org.slf4j.LoggerFactory

class GetOrCreateDraftRecipeUseCase(
    private val recipesRepository: RecipesRepository
) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    suspend operator fun invoke(userId: String): Result<Recipe> {
        return try {
            val existingDraft = recipesRepository.findDraftByAuthorId(userId)
            logger.info("Found existing draft recipe $existingDraft")
            if (existingDraft != null) {
                return Result.success(existingDraft)
            }

            logger.info("Creating new draft recipe for user $userId")
            val newRecipe = Recipe(
                id = 0L,
                authorId = userId,
                name = "",
                description = null,
                isPublic = false,
                estTimeInMinutes = 10,
                portion = 1,
                status = RecipeStatus.DRAFT,
                createdAt = now,
                updatedAt = now,
            )
            val savedDraft = recipesRepository.save(newRecipe)

            logger.info("Created new draft recipe $savedDraft")
            Result.success(savedDraft)
        } catch (e: Exception) {
            logger.error("Failed to get or create draft recipe", e)
            return Result.failure(e)
        }
    }
}