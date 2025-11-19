package com.sukakotlin.features.recipe.data.table

import com.sukakotlin.data.database.table.LongBaseTable
import org.jetbrains.exposed.v1.core.ReferenceOption

object StepsTable: LongBaseTable("steps") {
    val recipeId = long("recipe_id").references(RecipesTable.id, onDelete = ReferenceOption.CASCADE)
    val stepNumber = integer("step_number")
    val content = text("content")

    init {
        uniqueIndex(recipeId, stepNumber)
    }
}