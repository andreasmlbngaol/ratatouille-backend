package com.sukakotlin.di

import com.sukakotlin.service.AuthService
import com.sukakotlin.service.RecipeService
import com.sukakotlin.service.StorageService
import com.sukakotlin.service.UserService
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val serviceModule = module {
    single { StorageService() }
    singleOf(::AuthService)

    singleOf(::UserService)
    singleOf(::RecipeService)
}