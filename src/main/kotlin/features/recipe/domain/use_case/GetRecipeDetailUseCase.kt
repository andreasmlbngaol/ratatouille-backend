package com.sukakotlin.features.recipe.domain.use_case

import com.sukakotlin.features.recipe.domain.model.recipe.RecipeDetail
import com.sukakotlin.features.recipe.domain.repository.RecipesRepository
import org.slf4j.LoggerFactory

class GetRecipeDetailUseCase(
    private val recipesRepository: RecipesRepository
) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    suspend operator fun invoke(
        userId: String,
        recipeId: Long
    ): Result<RecipeDetail> {
        return try {
            val recipeDetail = recipesRepository.findRecipeDetail(recipeId)
                ?: return Result.failure(IllegalArgumentException("Recipe with id $recipeId not found"))

            if(!recipeDetail.recipe.isPublic && recipeDetail.recipe.authorId != userId) {
                return Result.failure(IllegalAccessException("Unauthorized access to private recipe"))
            }

            Result.success(recipeDetail)
        } catch (e: Exception) {
            logger.error("Failed to get recipe detail for recipeId=$recipeId", e)
            return Result.failure(e)
        }
    }
}