package com.sukakotlin.features.recipe.presentation.dto.response

import com.sukakotlin.features.recipe.domain.model.Image
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class ImageDto(
    val id: Long,
    val url: String,
    val createdAt: LocalDateTime
)

@Serializable
data class ImageResponse(
    val success: Boolean = true,
    val message: String? = null,
    val data: ImageDto
)

fun Image.toDto() = ImageDto(
    id = this.id,
    url = this.url,
    createdAt = this.createdAt
)

fun Image.toResponse() = ImageResponse(data = this.toDto())