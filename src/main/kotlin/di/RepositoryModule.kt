package com.sukakotlin.di

import com.sukakotlin.repository.IngredientTagsRepository
import com.sukakotlin.repository.RecipeRepository
import com.sukakotlin.repository.UserRepository
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val repositoryModule = module {
    singleOf(::UserRepository)
    singleOf(::RecipeRepository)
    singleOf(::IngredientTagsRepository)
}