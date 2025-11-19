package com.sukakotlin.features.recipe.data.entity

import com.sukakotlin.data.database.entity.BaseEntityClass
import com.sukakotlin.data.database.entity.LongBaseEntity
import com.sukakotlin.features.recipe.data.table.BookmarksTable
import org.jetbrains.exposed.v1.core.dao.id.EntityID

class BookmarksEntity(id: EntityID<Long>): LongBaseEntity(id) {
    var userId by BookmarksTable.userId
    var recipeId by BookmarksTable.recipeId

    companion object: BaseEntityClass<Long, BookmarksEntity>(BookmarksTable)
}