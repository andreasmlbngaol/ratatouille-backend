package com.sukakotlin.presentation.model

import kotlinx.serialization.Serializable

@Serializable
data class FailureResponse(
    val success: Boolean = false,
    val message: String? = null,
    val data: Nothing? = null
)