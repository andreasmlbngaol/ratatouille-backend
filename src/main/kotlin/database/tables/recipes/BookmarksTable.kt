package com.sukakotlin.database.tables.recipes

import com.sukakotlin.database.tables.users.UsersTable
import org.jetbrains.exposed.v1.core.ReferenceOption
import org.jetbrains.exposed.v1.core.dao.id.LongIdTable

object BookmarksTable: LongIdTable("bookmarks") {
    val userId = varchar("user_id", 64).references(UsersTable.id, onDelete = ReferenceOption.CASCADE)
    val recipeId = long("recipe_id").references(RecipesTable.id, onDelete = ReferenceOption.CASCADE)
    init {
        uniqueIndex(userId, recipeId)
    }
}