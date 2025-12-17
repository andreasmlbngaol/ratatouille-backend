package com.sukakotlin.dto

import kotlinx.serialization.Serializable

@Serializable
data class RateRecipeRequest(
    val value: Double
)