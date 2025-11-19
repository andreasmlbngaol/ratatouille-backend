package com.sukakotlin.features.recipe.di

import com.sukakotlin.features.recipe.data.repository.IngredientTagsRepositoryImpl
import com.sukakotlin.features.recipe.data.repository.RecipesRepositoryImpl
import com.sukakotlin.features.recipe.domain.repository.IngredientTagsRepository
import com.sukakotlin.features.recipe.domain.repository.RecipesRepository
import com.sukakotlin.features.recipe.domain.use_case.GetOrCreateDraftRecipeUseCase
import com.sukakotlin.features.recipe.domain.use_case.base.UpdateRecipeUseCase
import com.sukakotlin.features.recipe.domain.use_case.base.UploadRecipeImageUseCase
import com.sukakotlin.features.recipe.domain.use_case.ingredients.CreateIngredientTagUseCase
import com.sukakotlin.features.recipe.domain.use_case.ingredients.GetIngredientTagUseCase
import com.sukakotlin.features.recipe.domain.use_case.ingredients.UpdateIngredientUseCase
import com.sukakotlin.features.recipe.domain.use_case.steps.CreateEmptyStepUseCase
import com.sukakotlin.features.recipe.domain.use_case.steps.UpdateStepUseCase
import com.sukakotlin.features.recipe.domain.use_case.steps.UploadStepImageUseCase
import org.koin.dsl.module

val recipeModule = module {
    single<RecipesRepository> { RecipesRepositoryImpl }
    single<IngredientTagsRepository> { IngredientTagsRepositoryImpl }

    factory { GetOrCreateDraftRecipeUseCase(get()) }
    factory { UpdateRecipeUseCase(get()) }
    factory { UploadRecipeImageUseCase(get(), get()) }
    factory { GetIngredientTagUseCase(get()) }
    factory { CreateIngredientTagUseCase(get()) }
    factory { UpdateIngredientUseCase(get()) }
    factory { CreateEmptyStepUseCase(get()) }
    factory { UploadStepImageUseCase(get(), get()) }
    factory { UpdateStepUseCase(get()) }
//    factory { PublishRecipeUseCase(get()) }
}