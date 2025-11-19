package com.sukakotlin.di

import com.sukakotlin.features.recipe.di.recipeModule
import userModule

val koinModules = listOf(
    userModule,
    recipeModule
)