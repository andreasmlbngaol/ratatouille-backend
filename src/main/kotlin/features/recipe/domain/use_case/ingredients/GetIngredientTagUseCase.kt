package com.sukakotlin.features.recipe.domain.use_case.ingredients

import com.sukakotlin.features.recipe.domain.model.IngredientTag
import com.sukakotlin.features.recipe.domain.repository.IngredientTagsRepository

class GetIngredientTagUseCase(
    private val tagsRepository: IngredientTagsRepository,
) {
    suspend operator fun invoke(name: String): Result<List<IngredientTag>> {
        if (name.length < 3) return Result.failure(IllegalArgumentException("Name must be at least 3 characters long"))
        return Result.success(tagsRepository.searchByName(name.uppercase()))
    }
}