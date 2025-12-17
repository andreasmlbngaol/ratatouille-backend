package com.sukakotlin.database.migration

import com.sukakotlin.database.tables.recipes.BookmarksTable
import com.sukakotlin.database.tables.users.FollowsTable
import com.sukakotlin.database.tables.users.UsersTable
import com.sukakotlin.database.tables.recipes.CommentsImagesTable
import com.sukakotlin.database.tables.recipes.CommentsTable
import com.sukakotlin.database.tables.recipes.ImagesTable
import com.sukakotlin.database.tables.recipes.IngredientTagsTable
import com.sukakotlin.database.tables.recipes.IngredientsTable
import com.sukakotlin.database.tables.recipes.RatingsTable
import com.sukakotlin.database.tables.recipes.RecipesImagesTable
import com.sukakotlin.database.tables.recipes.RecipesTable
import com.sukakotlin.database.tables.recipes.StepsImagesTable
import com.sukakotlin.database.tables.recipes.StepsTable

import org.jetbrains.exposed.v1.jdbc.SchemaUtils

val migrations = listOf(
    Migration(1) {
        SchemaUtils.create(
            UsersTable,
            FollowsTable,
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