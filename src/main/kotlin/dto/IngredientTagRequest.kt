package com.sukakotlin.dto

import kotlinx.serialization.Serializable

@Serializable
data class IngredientTagRequest(
    val name: String
)