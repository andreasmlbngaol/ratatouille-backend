package com.sukakotlin.data.database.migration

import com.sukakotlin.features.recipe.data.table.BookmarksTable
import com.sukakotlin.features.recipe.data.table.CommentsImagesTable
import com.sukakotlin.features.recipe.data.table.CommentsTable
import com.sukakotlin.features.recipe.data.table.ImagesTable
import com.sukakotlin.features.recipe.data.table.IngredientTagsTable
import com.sukakotlin.features.recipe.data.table.IngredientsTable
import com.sukakotlin.features.recipe.data.table.RatingsTable
import com.sukakotlin.features.recipe.data.table.RecipesImagesTable
import com.sukakotlin.features.recipe.data.table.RecipesTable
import com.sukakotlin.features.recipe.data.table.StepsImagesTable
import com.sukakotlin.features.recipe.data.table.StepsTable
import com.sukakotlin.features.user.data.table.FollowsTable
import com.sukakotlin.features.user.data.table.UsersTable
import org.jetbrains.exposed.v1.jdbc.SchemaUtils

val migrations = listOf(
    Migration(1) {
        SchemaUtils.create(
            UsersTable
        )
    },
    Migration(2) {
        SchemaUtils.create(
            FollowsTable
        )
    },
    Migration(3) {
        SchemaUtils.create(
            BookmarksTable,
            CommentsImagesTable,
            CommentsTable,
            ImagesTable,
            IngredientsTable,
            IngredientTagsTable,
            RatingsTable,
            RecipesImagesTable,
            RecipesTable,
            StepsImagesTable,
            StepsTable
        )
    }
)
