package com.sukakotlin.features.recipe.domain.model

data class RecipeDetail(
    val recipe: Recipe,
    val images: List<Image>,
    val ingredients: List<IngredientWithTag>,
    val steps: List<StepWithImages>
)

data class IngredientWithTag(
    val ingredient: Ingredient,
    val tag: IngredientTag
)

data class StepWithImages(
    val step: Step,
    val images: List<Image>
)