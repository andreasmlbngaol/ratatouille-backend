package com.sukakotlin.features.recipe.data.table

import com.sukakotlin.data.database.table.LongBaseTable
import org.jetbrains.exposed.v1.core.ReferenceOption

object RecipesImagesTable: LongBaseTable("recipes_images") {
    val recipeId = long("recipe_id").references(RecipesTable.id, onDelete = ReferenceOption.CASCADE)
    val imageId = long("image_id").references(ImagesTable.id, onDelete = ReferenceOption.CASCADE)
}