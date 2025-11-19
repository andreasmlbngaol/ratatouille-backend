package com.sukakotlin.features.recipe.domain.repository

import com.sukakotlin.domain.repository.BaseRepository
import com.sukakotlin.features.recipe.domain.model.IngredientTag

interface IngredientTagsRepository : BaseRepository<Long, IngredientTag> {
    suspend fun save(tag: IngredientTag): IngredientTag

    suspend fun findByNameILike(name: String): IngredientTag?
    suspend fun searchByName(query: String): List<IngredientTag>
    suspend fun findByNameOrCreate(name: String): IngredientTag
}
