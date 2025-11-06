package com.sukakotlin.core.database

import com.sukakotlin.features.user.data.UsersTable
import org.jetbrains.exposed.v1.jdbc.SchemaUtils

val migrations = listOf(
    Migration(1) {
        SchemaUtils.create(
            UsersTable
        )
    },
//    Migration(2) {
//        SchemaUtils.create(
//            Bookmarks,
//            Comments,
//            CommentsImages,
//            Images,
//            Ingredients,
//            Ratings,
//            Reactions,
//            Recipes,
//            RecipesImages,
//            Steps
//        )
//    },
//    Migration(3) {
//        SchemaUtils.create(
//            StepsImages,
//            Follows
//        )
//    },
//    Migration(4) {
//        SchemaUtils.create(
//            IngredientTags
//        )
//    }
)
