package com.sukakotlin.features.recipe.domain.use_case.ingredients

import com.sukakotlin.features.recipe.domain.model.IngredientTag
import com.sukakotlin.features.recipe.domain.repository.IngredientTagsRepository

class GetIngredientTagUseCase(
    private val tagsRepository: IngredientTagsRepository,
) {
    suspend operator fun invoke(name: String): Result<List<IngredientTag>> =
        Result.success(tagsRepository.searchByName(name))
}