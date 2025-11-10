package com.sukakotlin.presentation.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement


@Serializable
data class ApiResponse(
    val success: Boolean,
    val message: String? = null,
    val data: JsonElement? = null
)
