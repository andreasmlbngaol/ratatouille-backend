package com.sukakotlin.features.recipe.data.entity

import com.sukakotlin.data.database.entity.BaseEntityClass
import com.sukakotlin.data.database.entity.LongBaseEntity
import com.sukakotlin.features.recipe.data.table.RecipesImagesTable
import org.jetbrains.exposed.v1.core.dao.id.EntityID

class RecipesImagesEntity(id: EntityID<Long>): LongBaseEntity(id) {
    var recipeId by RecipesImagesTable.recipeId
    var imageId by RecipesImagesTable.imageId

    companion object: BaseEntityClass<Long, RecipesImagesEntity>(RecipesImagesTable)
}