package com.sukakotlin.features.recipe.data.table

import com.sukakotlin.data.database.table.LongBaseTable
import org.jetbrains.exposed.v1.core.ReferenceOption

object IngredientsTable: LongBaseTable("ingredients") {
    val recipeId = long("recipe_id").references(RecipesTable.id, onDelete = ReferenceOption.CASCADE)
    val tagId = long("tag_id").references(IngredientTagsTable.id, onDelete = ReferenceOption.CASCADE)
    val alternative = varchar("alternative", 100).nullable()
    val amount = double("amount").nullable()
    val unit = varchar("unit", 100).nullable()

    init {
        uniqueIndex(recipeId, tagId)
    }
}