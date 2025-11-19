package com.sukakotlin.features.user.domain.use_case.profile

import com.sukakotlin.domain.model.ImageData
import com.sukakotlin.features.user.domain.model.auth.User
import com.sukakotlin.features.user.domain.repository.UsersRepository
import com.sukakotlin.features.user.domain.service.ImageCleanupPort
import com.sukakotlin.features.user.domain.service.ImageUploadPort
import org.slf4j.LoggerFactory

class UpdateUserPictureUseCase(
    private val usersRepository: UsersRepository,
    private val imageUploadPort: ImageUploadPort,
    private val imageCleanupPort: ImageCleanupPort
) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    suspend fun updateProfilePicture(
        id: String,
        imageData: ImageData
    ): Result<User> = updatePicture(id, imageData, PictureType.PROFILE)

    suspend fun updateCoverPicture(
        id: String,
        imageData: ImageData
    ): Result<User> = updatePicture(id, imageData, PictureType.COVER)

    private suspend fun updatePicture(
        id: String,
        imageData: ImageData,
        type: PictureType
    ): Result<User> {
        return try {
            validateImageSize(imageData)

            val user = usersRepository.findById(id)
                ?: return Result.failure(IllegalArgumentException("User not found"))

            val oldPictureUrl = when (type) {
                PictureType.PROFILE -> user.profilePictureUrl
                PictureType.COVER -> user.coverPictureUrl
            }

            val pictureUrl = when (type) {
                PictureType.PROFILE -> imageUploadPort.uploadProfilePicture(id, imageData)
                PictureType.COVER -> imageUploadPort.uploadCoverPicture(id, imageData)
            }

            val updatedUser = user.copy(
                profilePictureUrl = if (type == PictureType.PROFILE) pictureUrl else user.profilePictureUrl,
                coverPictureUrl = if (type == PictureType.COVER) pictureUrl else user.coverPictureUrl
            )

            val saved = usersRepository.update(id, updatedUser)
                ?: return Result.failure(IllegalStateException("Failed to update user"))

            if(!oldPictureUrl.isNullOrBlank()) {
                try {
                    imageCleanupPort.deleteImage(oldPictureUrl)
                } catch (e: Exception) {
                    logger.warn("Failed to delete old image: $oldPictureUrl", e)
                }
            }

            logger.info("${type.name} picture updated for user: $id")
            return Result.success(saved)
        } catch (e: Exception) {
            logger.error("Failed to update ${type.name} picture", e)
            Result.failure(e)
        }
    }

    private fun validateImageSize(imageData: ImageData) {
        if (imageData.content.size > 5 * 1024 * 1024) {
            throw IllegalArgumentException("Image size must be less than 5MB")
        }
    }

    enum class PictureType {
        PROFILE, COVER
    }

}