package com.sukakotlin.features.recipe.data.entity

import com.sukakotlin.data.database.entity.BaseEntityClass
import com.sukakotlin.data.database.entity.LongBaseEntity
import com.sukakotlin.features.recipe.data.table.ImagesTable
import org.jetbrains.exposed.v1.core.dao.id.EntityID

class ImagesEntity(id: EntityID<Long>): LongBaseEntity(id) {
    var url by ImagesTable.url

    companion object: BaseEntityClass<Long, ImagesEntity>(ImagesTable)
}