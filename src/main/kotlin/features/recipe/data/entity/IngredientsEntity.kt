package com.sukakotlin.features.recipe.data.entity

import com.sukakotlin.data.database.entity.BaseEntityClass
import com.sukakotlin.data.database.entity.LongBaseEntity
import com.sukakotlin.features.recipe.data.table.IngredientsTable
import org.jetbrains.exposed.v1.core.dao.id.EntityID

class IngredientsEntity(id: EntityID<Long>): LongBaseEntity(id) {
    var recipeId by IngredientsTable.recipeId
    var tagId by IngredientsTable.tagId
    var alternative by IngredientsTable.alternative
    var amount by IngredientsTable.amount
    var unit by IngredientsTable.unit

    companion object: BaseEntityClass<Long, IngredientsEntity>(IngredientsTable)
}