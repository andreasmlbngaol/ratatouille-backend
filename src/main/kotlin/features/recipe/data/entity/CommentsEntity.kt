package com.sukakotlin.features.recipe.data.entity

import com.sukakotlin.data.database.entity.BaseEntityClass
import com.sukakotlin.data.database.entity.LongBaseEntity
import com.sukakotlin.features.recipe.data.table.CommentsTable
import org.jetbrains.exposed.v1.core.dao.id.EntityID

class CommentsEntity(id: EntityID<Long>): LongBaseEntity(id) {
    var recipeId by CommentsTable.recipeId
    var userId by CommentsTable.userId
    var content by CommentsTable.content

    companion object: BaseEntityClass<Long, CommentsEntity>(CommentsTable)
}