package com.sukakotlin.features.user.domain.service

interface ImageCleanupPort {
    suspend fun deleteImage(imageUrl: String)
}