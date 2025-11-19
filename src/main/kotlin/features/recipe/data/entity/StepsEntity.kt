package com.sukakotlin.features.recipe.data.entity

import com.sukakotlin.data.database.entity.BaseEntityClass
import com.sukakotlin.data.database.entity.LongBaseEntity
import com.sukakotlin.features.recipe.data.table.StepsTable
import org.jetbrains.exposed.v1.core.dao.id.EntityID

class StepsEntity(id: EntityID<Long>): LongBaseEntity(id) {
    var recipeId by StepsTable.recipeId
    var stepNumber by StepsTable.stepNumber
    var content by StepsTable.content

    companion object: BaseEntityClass<Long, StepsEntity>(StepsTable)
}