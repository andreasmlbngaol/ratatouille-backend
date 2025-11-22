package com.sukakotlin.features.recipe.domain.use_case.ingredients

import com.sukakotlin.features.recipe.domain.model.ingredient.IngredientTag
import com.sukakotlin.features.recipe.domain.repository.IngredientTagsRepository

class CreateIngredientTagUseCase(
    private val tagsRepository: IngredientTagsRepository
) {
    suspend operator fun invoke(name: String): Result<IngredientTag> {
        return try {
            val newTag = IngredientTag(
                id = 0L,
                name = name.uppercase().trim()
            )

            val savedTag = tagsRepository.save(newTag)
            Result.success(savedTag)
        } catch (e: Exception) {
            return Result.failure(e)
        }
    }
}