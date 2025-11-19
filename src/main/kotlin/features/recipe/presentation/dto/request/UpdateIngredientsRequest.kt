package com.sukakotlin.features.recipe.presentation.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class UpdateIngredientsRequest(
    val tagId: Long,
    val amount: Double?,
    val unit: String?,
    val alternative: String?
)
