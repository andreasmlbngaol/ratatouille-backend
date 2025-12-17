package com.sukakotlin.dto

import kotlinx.serialization.Serializable

@Serializable
data class CreateStepRequest(
    val stepNumber: Int
)