package com.sukakotlin.features.user.domain.service

import com.sukakotlin.features.user.domain.model.profile.ImageData

interface ImageUploadPort {
    suspend fun uploadProfilePicture(userId: String, imageData: ImageData): String?
    suspend fun uploadCoverPicture(userId: String, imageData: ImageData): String?
}