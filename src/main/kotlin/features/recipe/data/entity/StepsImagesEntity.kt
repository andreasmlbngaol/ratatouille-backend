package com.sukakotlin.features.recipe.data.entity

import com.sukakotlin.data.database.entity.BaseEntityClass
import com.sukakotlin.data.database.entity.LongBaseEntity
import com.sukakotlin.features.recipe.data.table.StepsImagesTable
import org.jetbrains.exposed.v1.core.dao.id.EntityID

class StepsImagesEntity(id: EntityID<Long>): LongBaseEntity(id) {
    var stepId by StepsImagesTable.stepId
    var imageId by StepsImagesTable.imageId

    companion object: BaseEntityClass<Long, StepsImagesEntity>(StepsImagesTable)
}