package com.sukakotlin.features.recipe.data.table

import com.sukakotlin.data.database.table.LongBaseTable
import com.sukakotlin.features.user.data.table.UsersTable
import org.jetbrains.exposed.v1.core.ReferenceOption

object BookmarksTable: LongBaseTable("bookmarks") {
    val userId = varchar("user_id", 64).references(UsersTable.id, onDelete = ReferenceOption.CASCADE)
    val recipeId = long("recipe_id").references(RecipesTable.id, onDelete = ReferenceOption.CASCADE)

    init {
        uniqueIndex(userId, recipeId)
    }
}