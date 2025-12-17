package com.sukakotlin.database.tables.recipes

import org.jetbrains.exposed.v1.core.ReferenceOption
import org.jetbrains.exposed.v1.core.dao.id.LongIdTable

object IngredientsTable: LongIdTable("ingredients") {
    val recipeId = long("recipe_id").references(RecipesTable.id, onDelete = ReferenceOption.CASCADE)
    val tagId = long("tag_id").references(IngredientTagsTable.id, onDelete = ReferenceOption.CASCADE)
    val alternative = varchar("alternative", 100).nullable()
    val amount = double("amount").nullable()
    val unit = varchar("unit", 100).nullable()

    init {
        uniqueIndex(recipeId, tagId)
    }
}