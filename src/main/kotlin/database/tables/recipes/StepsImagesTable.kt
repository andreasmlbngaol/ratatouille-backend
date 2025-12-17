package com.sukakotlin.database.tables.recipes

import org.jetbrains.exposed.v1.core.ReferenceOption
import org.jetbrains.exposed.v1.core.dao.id.LongIdTable

object StepsImagesTable: LongIdTable("steps_images") {
    val stepId = long("step_id").references(StepsTable.id, onDelete = ReferenceOption.CASCADE)
    val imageId = long("image_id").references(ImagesTable.id, onDelete = ReferenceOption.CASCADE)
}