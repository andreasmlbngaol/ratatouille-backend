package com.sukakotlin.features.recipe.data.entity

import com.sukakotlin.data.database.entity.BaseEntityClass
import com.sukakotlin.data.database.entity.LongBaseEntity
import com.sukakotlin.features.recipe.data.table.RecipesTable
import org.jetbrains.exposed.v1.core.dao.id.EntityID

class RecipesEntity(id: EntityID<Long>): LongBaseEntity(id) {
    var authorId by RecipesTable.authorId
    var name by RecipesTable.name
    var description by RecipesTable.description
    var isPublic by RecipesTable.isPublic
    var estTimeInMinutes by RecipesTable.estTimeInMinutes
    var portion by RecipesTable.portion
    var status by RecipesTable.status

    companion object: BaseEntityClass<Long, RecipesEntity>(RecipesTable)
}