package com.sukakotlin.features.recipe.domain.use_case.base

import com.sukakotlin.domain.model.ImageData
import com.sukakotlin.features.recipe.domain.model.Image
import com.sukakotlin.features.recipe.domain.repository.RecipesRepository
import com.sukakotlin.features.user.data.utils.now
import com.sukakotlin.features.user.domain.service.ImageUploadPort
import org.slf4j.LoggerFactory

class UploadRecipeImageUseCase(
    private val recipesRepository: RecipesRepository,
    private val imageUploadPort: ImageUploadPort
) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    suspend operator fun invoke(
        userId: String,
        recipeId: Long,
        imageData: ImageData
    ): Result<List<Image>> {
        return try {
            validateImageSize(imageData)

            recipesRepository.findByIdAndAuthorId(recipeId, userId)
                ?: return Result.failure(IllegalArgumentException("Recipe with id $recipeId and author $userId not found"))

            val pictureUrl = imageUploadPort.uploadRecipeImage(userId, recipeId, imageData)
                ?: return Result.failure(IllegalStateException("Failed to upload image"))

            val uploadedImage = Image(
                id = 0L,
                url = pictureUrl,
                createdAt = now
            )

            val images = recipesRepository.addRecipeImage(userId, recipeId, uploadedImage)
            Result.success(images)

        } catch (e: Exception) {
            logger.error("Failed to upload recipe image", e)
            return Result.failure(e)
        }
    }

    private fun validateImageSize(imageData: ImageData) {
        if (imageData.content.size > 5 * 1024 * 1024) {
            throw IllegalArgumentException("Image size must be less than 5MB")
        }
    }
}