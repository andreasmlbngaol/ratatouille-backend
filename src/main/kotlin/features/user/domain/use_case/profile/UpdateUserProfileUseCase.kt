package com.sukakotlin.features.user.domain.use_case.profile

import com.sukakotlin.features.user.domain.model.auth.User
import com.sukakotlin.features.user.domain.repository.UsersRepository
import org.slf4j.LoggerFactory

class UpdateUserProfileUseCase(
    private val usersRepository: UsersRepository
) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    suspend operator fun invoke(
        id: String,
        name: String? = null,
        bio: String? = null,
    ): Result<User> {
        return try {
            if (name == null && bio == null) {
                return Result.failure(IllegalArgumentException("No fields to update"))
            }

            val user = usersRepository.findById(id)
                ?: return Result.failure(IllegalArgumentException("User not found"))

            val updatedUser = user.copy(
                name = name ?: user.name,
                bio = bio ?: user.bio
            )

            val updated = usersRepository.update(id, updatedUser)
                ?: return Result.failure(IllegalStateException("Failed to update user"))

            return Result.success(updated)
        } catch (e: Exception) {
            logger.error("Error updating user profile", e)
            Result.failure(e)
        }
    }
}