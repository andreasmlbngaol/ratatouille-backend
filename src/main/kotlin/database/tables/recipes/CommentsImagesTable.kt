package com.sukakotlin.database.tables.recipes

import org.jetbrains.exposed.v1.core.ReferenceOption
import org.jetbrains.exposed.v1.core.dao.id.LongIdTable

object CommentsImagesTable: LongIdTable("comments_images") {
    val commentId = long("comment_id").references(CommentsTable.id, onDelete = ReferenceOption.CASCADE)
    val imageId = long("image_id").references(ImagesTable.id, onDelete = ReferenceOption.CASCADE)
}