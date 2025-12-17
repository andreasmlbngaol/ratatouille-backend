package com.sukakotlin.model

import kotlinx.serialization.Serializable

@Serializable
data class IngredientTag(
    val id: Long,
    val name: String
)
