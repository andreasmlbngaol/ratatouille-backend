package com.sukakotlin.features.recipe.domain.model.recipe

import com.sukakotlin.features.recipe.domain.model.ingredient.IngredientWithTag
import com.sukakotlin.features.recipe.domain.model.step.StepWithImages

data class RecipeDetail(
    val recipe: RecipeWithImages,
    val ingredients: List<IngredientWithTag>,
    val steps: List<StepWithImages>
)