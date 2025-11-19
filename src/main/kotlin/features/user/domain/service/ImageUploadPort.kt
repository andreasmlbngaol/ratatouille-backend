package com.sukakotlin.features.user.domain.service

import com.sukakotlin.domain.model.ImageData

interface ImageUploadPort {
    suspend fun uploadProfilePicture(userId: String, imageData: ImageData): String?
    suspend fun uploadCoverPicture(userId: String, imageData: ImageData): String?
    suspend fun uploadRecipeImage(userId: String, recipeId: Long, imageData: ImageData): String?
    suspend fun uploadStepImage(userId: String, recipeId: Long, stepId: Long, imageData: ImageData): String?

}