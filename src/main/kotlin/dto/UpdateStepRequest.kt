package com.sukakotlin.dto

import kotlinx.serialization.Serializable

@Serializable
data class UpdateStepRequest(
    val content: String
)