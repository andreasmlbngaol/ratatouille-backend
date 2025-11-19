package com.sukakotlin.features.recipe.data.entity

import com.sukakotlin.data.database.entity.BaseEntityClass
import com.sukakotlin.data.database.entity.LongBaseEntity
import com.sukakotlin.features.recipe.data.table.IngredientTagsTable
import org.jetbrains.exposed.v1.core.dao.id.EntityID

class IngredientTagsEntity(id: EntityID<Long>): LongBaseEntity(id) {
    var name by IngredientTagsTable.name

    companion object: BaseEntityClass<Long, IngredientTagsEntity>(IngredientTagsTable)
}