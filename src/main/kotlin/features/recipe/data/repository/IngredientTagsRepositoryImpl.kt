package com.sukakotlin.features.recipe.data.repository

import com.sukakotlin.data.repository.BaseRepositoryImpl
import com.sukakotlin.features.recipe.data.entity.IngredientTagsEntity
import com.sukakotlin.features.recipe.domain.model.IngredientTag
import com.sukakotlin.features.recipe.domain.repository.IngredientTagsRepository

object IngredientTagsRepositoryImpl:
    BaseRepositoryImpl<Long, IngredientTagsEntity, IngredientTag>(IngredientTagsEntity),
    IngredientTagsRepository {
    override fun IngredientTagsEntity.toDomain(): IngredientTag {
        TODO("Not yet implemented")
    }

    override suspend fun save(tag: IngredientTag): IngredientTag {
        TODO("Not yet implemented")
    }

    override suspend fun findByNameILike(name: String): IngredientTag? {
        TODO("Not yet implemented")
    }

    override suspend fun searchByName(query: String): List<IngredientTag> {
        TODO("Not yet implemented")
    }

    override suspend fun findByNameOrCreate(name: String): IngredientTag {
        TODO("Not yet implemented")
    }

}