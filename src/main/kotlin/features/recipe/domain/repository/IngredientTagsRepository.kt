package com.sukakotlin.features.recipe.domain.repository

import com.sukakotlin.features.recipe.domain.model.ingredient.IngredientTag

interface IngredientTagsRepository {
    suspend fun save(tag: IngredientTag): IngredientTag
    suspend fun searchByName(query: String): List<IngredientTag>
}