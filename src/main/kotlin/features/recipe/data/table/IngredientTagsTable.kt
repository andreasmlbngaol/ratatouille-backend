package com.sukakotlin.features.recipe.data.table

import com.sukakotlin.data.database.table.LongBaseTable

object IngredientTagsTable: LongBaseTable("ingredient_tags") {
    val name = varchar("name", 64).uniqueIndex()
}