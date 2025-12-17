package com.sukakotlin.dto

import kotlinx.serialization.Serializable

@Serializable
data class AddIngredientRequest(
    val amount: Double? = null,
    val unit: String? = null,
    val alternative: String? = null,
    val tagId: Long
)