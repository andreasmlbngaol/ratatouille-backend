package com.sukakotlin.database.tables.recipes

import com.sukakotlin.database.tables.users.UsersTable
import com.sukakotlin.model.RecipeStatus
import org.jetbrains.exposed.v1.core.ReferenceOption
import org.jetbrains.exposed.v1.core.dao.id.LongIdTable

object RecipesTable: LongIdTable("recipes") {
    val authorId = varchar("author_id", 64).references(UsersTable.id, onDelete = ReferenceOption.CASCADE)
    val name = varchar("name", 50)
    val description = varchar("description", 100).nullable()
    val isPublic = bool("is_public")
    val estTimeInMinutes = integer("est_time_in_minutes")
    val portion = integer("portion").default(1)
    val status = enumerationByName<RecipeStatus>("status", 32).default(RecipeStatus.DRAFT)
    val createdAt = long("created_at")
    val updatedAt = long("updated_at")
}