package com.sukakotlin.features.recipe.domain.model

import kotlinx.datetime.LocalDateTime

data class Image(
    val id: Long,
    val url: String,
    val createdAt: LocalDateTime
)
