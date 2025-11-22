package com.sukakotlin.features.recipe.domain.use_case.steps

import com.sukakotlin.domain.model.ImageData
import com.sukakotlin.features.recipe.domain.model.Image
import com.sukakotlin.features.recipe.domain.model.step.StepWithImages
import com.sukakotlin.features.recipe.domain.repository.RecipesRepository
import com.sukakotlin.shared.util.now
import com.sukakotlin.features.user.domain.service.ImageUploadPort
import org.slf4j.LoggerFactory

class UploadStepImageUseCase(
    private val recipesRepository: RecipesRepository,
    private val imageUploadPort: ImageUploadPort
) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    suspend operator fun invoke(
        userId: String,
        recipeId: Long,
        stepId: Long,
        imageData: ImageData
    ): Result<List<StepWithImages>> {
        return try {
            validateImageSize(imageData)

            recipesRepository.existByIdAndAuthorId(recipeId, userId).let {
                if(!it) return Result.failure(IllegalArgumentException("Recipe with id $recipeId and author $userId not found"))
            }

            val pictureUrl = imageUploadPort.uploadStepImage(
                userId = userId,
                recipeId = recipeId,
                stepId = stepId,
                imageData = imageData
            ) ?: return Result.failure(IllegalStateException("Failed to upload image"))

            val uploadedImage = Image(
                id = 0L,
                url = pictureUrl,
                createdAt = now
            )

            val stepWithImages = recipesRepository.addStepImage(userId, recipeId, stepId, uploadedImage)
            Result.success(stepWithImages)
        } catch (e: Exception) {
            logger.error("Failed to upload image", e)
            return Result.failure(e)
        }
    }

    private fun validateImageSize(imageData: ImageData) {
        if (imageData.content.size > 5 * 1024 * 1024) {
            throw IllegalArgumentException("Image size must be less than 5MB")
        }
    }
}