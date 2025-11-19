package com.sukakotlin.features.recipe.data.table

import com.sukakotlin.data.database.table.LongBaseTable
import com.sukakotlin.features.recipe.domain.model.RecipeStatus
import com.sukakotlin.features.user.data.table.UsersTable
import org.jetbrains.exposed.v1.core.ReferenceOption

object RecipesTable: LongBaseTable("recipes") {
    val authorId = varchar("author_id", 64).references(UsersTable.id, onDelete = ReferenceOption.CASCADE)
    val name = varchar("name", 50)
    val description = varchar("description", 100).nullable()
    val isPublic = bool("is_public")
    val estTimeInMinutes = integer("est_time_in_minutes")
    val portion = integer("portion").default(1)
    val status = enumerationByName<RecipeStatus>("status", 32).default(RecipeStatus.DRAFT)
}
