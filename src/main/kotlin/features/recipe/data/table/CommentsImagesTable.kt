package com.sukakotlin.features.recipe.data.table

import com.sukakotlin.data.database.table.LongBaseTable
import org.jetbrains.exposed.v1.core.ReferenceOption

object CommentsImagesTable: LongBaseTable("comments_images") {
    val commentId = long("comment_id").references(CommentsTable.id, onDelete = ReferenceOption.CASCADE)
    val imageId = long("image_id").references(ImagesTable.id, onDelete = ReferenceOption.CASCADE)
}