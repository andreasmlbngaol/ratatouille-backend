package com.sukakotlin.database.tables.recipes

import org.jetbrains.exposed.v1.core.ReferenceOption
import org.jetbrains.exposed.v1.core.dao.id.LongIdTable

object RecipesImagesTable : LongIdTable("recipes_images") {
    val recipeId = long("recipe_id").references(RecipesTable.id, onDelete = ReferenceOption.CASCADE)
    val imageId = long("image_id").references(ImagesTable.id, onDelete = ReferenceOption.CASCADE)
}