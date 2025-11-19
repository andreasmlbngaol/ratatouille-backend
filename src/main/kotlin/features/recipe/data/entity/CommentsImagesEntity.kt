package com.sukakotlin.features.recipe.data.entity

import com.sukakotlin.data.database.entity.BaseEntityClass
import com.sukakotlin.data.database.entity.LongBaseEntity
import com.sukakotlin.features.recipe.data.table.CommentsImagesTable
import org.jetbrains.exposed.v1.core.dao.id.EntityID

class CommentsImagesEntity(id: EntityID<Long>): LongBaseEntity(id) {
    var commentId by CommentsImagesTable.commentId
    var imageId by CommentsImagesTable.imageId

    companion object: BaseEntityClass<Long, CommentsImagesEntity>(CommentsImagesTable)
}