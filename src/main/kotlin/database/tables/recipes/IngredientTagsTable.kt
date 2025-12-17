package com.sukakotlin.database.tables.recipes

import org.jetbrains.exposed.v1.core.dao.id.LongIdTable

object IngredientTagsTable: LongIdTable("ingredient_tags") {
    val name = varchar("name", 64).uniqueIndex()
}