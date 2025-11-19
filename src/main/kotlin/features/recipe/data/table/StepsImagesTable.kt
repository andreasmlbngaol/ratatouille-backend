package com.sukakotlin.features.recipe.data.table

import com.sukakotlin.data.database.table.LongBaseTable
import org.jetbrains.exposed.v1.core.ReferenceOption

object StepsImagesTable: LongBaseTable("steps_images") {
    val stepId = long("step_id").references(StepsTable.id, onDelete = ReferenceOption.CASCADE)
    val imageId = long("image_id").references(ImagesTable.id, onDelete = ReferenceOption.CASCADE)
}