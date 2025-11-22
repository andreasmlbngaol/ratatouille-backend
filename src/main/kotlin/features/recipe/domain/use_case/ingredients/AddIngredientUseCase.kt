package com.sukakotlin.features.recipe.domain.use_case.ingredients

import com.sukakotlin.features.recipe.domain.model.ingredient.IngredientWithTag
import com.sukakotlin.features.recipe.domain.repository.RecipesRepository
import org.slf4j.LoggerFactory

class AddIngredientUseCase(
    private val recipesRepository: RecipesRepository
) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    suspend operator fun invoke(
        userId: String,
        recipeId: Long,
        tagId: Long,
        amount: Double?,
        unit: String?,
        alternative: String?
    ): Result<List<IngredientWithTag>> {
        return try {
            recipesRepository.existByIdAndAuthorId(recipeId, userId).let {
                if(!it) return Result.failure(IllegalArgumentException("Recipe with id $recipeId and author $userId not found"))
            }

            val updatedIngredients = recipesRepository.addIngredient(
                recipeId = recipeId,
                tagId = tagId,
                amount = amount,
                unit = unit?.uppercase()?.trim(),
                alternative = alternative?.trim()?.uppercase()
            )

            logger.info("Updated ingredients for recipe $recipeId: $updatedIngredients")
            Result.success(updatedIngredients)
        } catch (e: Exception) {
            logger.error("Failed to update ingredient", e)
            return Result.failure(IllegalArgumentException("Duplicate ingredient entry"))
        }
    }
}