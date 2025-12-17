package com.sukakotlin.database.tables.recipes

import org.jetbrains.exposed.v1.core.ReferenceOption
import org.jetbrains.exposed.v1.core.dao.id.LongIdTable

object StepsTable: LongIdTable("steps") {
    val recipeId = long("recipe_id").references(RecipesTable.id, onDelete = ReferenceOption.CASCADE)
    val stepNumber = integer("step_number")
    val content = text("content")

    init {
        uniqueIndex(recipeId, stepNumber)
    }
}