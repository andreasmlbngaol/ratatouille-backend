package com.sukakotlin.features.recipe.data.entity

import com.sukakotlin.data.database.entity.BaseEntityClass
import com.sukakotlin.data.database.entity.LongBaseEntity
import com.sukakotlin.features.recipe.data.table.RatingsTable
import org.jetbrains.exposed.v1.core.dao.id.EntityID

class RatingsEntity(id: EntityID<Long>): LongBaseEntity(id) {
    var recipeId by RatingsTable.recipeId
    var userId by RatingsTable.userId
    var value by RatingsTable.value

    companion object: BaseEntityClass<Long, RatingsEntity>(RatingsTable)
}