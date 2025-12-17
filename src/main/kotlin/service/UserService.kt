package com.sukakotlin.service

import com.sukakotlin.model.ImageData
import com.sukakotlin.model.User
import com.sukakotlin.repository.UserRepository
import org.slf4j.LoggerFactory

class UserService(
    private val authService: AuthService,
    private val userRepository: UserRepository,
    private val storageService: StorageService
) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    fun getOrCreateUser(idToken: String): Result<User> {
        return try {
            val tokenVerification = authService.verifyIdToken(idToken)

            if (!tokenVerification.isValid)
                return Result.failure(IllegalArgumentException("Invalid Firebase ID token"))

            val uid = tokenVerification.uid
            val email = tokenVerification.email
                ?: return Result.failure(IllegalArgumentException("Email not found in token"))

            val existingUser = userRepository.findById(uid)
            if (existingUser != null) {
                if (existingUser.isEmailVerified != tokenVerification.isEmailVerified) {
                    userRepository.updateEmailVerified(uid, tokenVerification.isEmailVerified)
                    return Result.success(existingUser.copy(isEmailVerified = tokenVerification.isEmailVerified))
                }
                return Result.success(existingUser)
            }

            val newUser = User(
                id = uid,
                name = "",
                email = email,
                profilePictureUrl = null,
                coverPictureUrl = null,
                bio = null,
                isEmailVerified = tokenVerification.isEmailVerified,
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            )
            logger.info("Creating new user: $newUser")
            val success = userRepository.save(newUser)
            if (!success) {
                return Result.failure(IllegalStateException("Failed to create new user"))
            }
            return Result.success(newUser)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun updateProfile(userId: String, name: String, bio: String?): Result<User> {
        return try {
            val updated = userRepository.updateProfile(userId, name, bio)
            Result.success(updated)
        } catch (e: Exception) {
            logger.error("Error updating profile for user $userId", e)
            Result.failure(e)
        }
    }

    suspend fun updateProfilePicture(userId: String, imageData: ImageData): Result<User> {
        return try {
            // Delete old picture if exists
            val user = userRepository.findById(userId)
                ?: return Result.failure(Exception("User not found"))

            user.profilePictureUrl?.let { oldUrl ->
                try {
                    storageService.deleteImage(oldUrl)
                } catch (e: Exception) {
                    logger.warn("Failed to delete old profile picture: $oldUrl", e)
                }
            }

            val pictureUrl = storageService.uploadProfilePicture(userId, imageData)
            val updated = userRepository.updateProfilePicture(userId, pictureUrl)
            Result.success(updated)
        } catch (e: Exception) {
            logger.error("Error updating profile picture for user $userId", e)
            Result.failure(e)
        }
    }

    suspend fun updateCoverPicture(userId: String, imageData: ImageData): Result<User> {
        return try {
            // Delete old cover if exists
            val user = userRepository.findById(userId)
                ?: return Result.failure(Exception("User not found"))

            user.coverPictureUrl?.let { oldUrl ->
                try {
                    storageService.deleteImage(oldUrl)
                } catch (e: Exception) {
                    logger.warn("Failed to delete old cover picture: $oldUrl", e)
                }
            }

            val coverUrl = storageService.uploadCoverPicture(userId, imageData)
            val updated = userRepository.updateCoverPicture(userId, coverUrl)
            Result.success(updated)
        } catch (e: Exception) {
            logger.error("Error updating cover picture for user $userId", e)
            Result.failure(e)
        }
    }

    fun searchUsersByName(query: String, currentUserId: String): Result<List<User>> {
        return try {
            val users = userRepository.searchByNameExcluding(query, currentUserId)
            Result.success(users)
        } catch (e: Exception) {
            logger.error("Error searching users by name: $query", e)
            Result.failure(e)
        }
    }

    fun getUserDetail(userId: String, currentUserId: String): Result<User> {
        return try {
            val user = userRepository.findById(userId)
                ?: return Result.failure(Exception("User not found"))
            Result.success(user)
        } catch (e: Exception) {
            logger.error("Error getting user detail for user $userId", e)
            Result.failure(e)
        }
    }

    fun followUser(currentUserId: String, targetUserId: String): Result<User> {
        return try {
            // TODO: Implement follow logic
            val user = userRepository.findById(targetUserId)
                ?: return Result.failure(Exception("User not found"))
            Result.success(user)
        } catch (e: Exception) {
            logger.error("Error following user $targetUserId", e)
            Result.failure(e)
        }
    }

    fun unfollowUser(currentUserId: String, targetUserId: String): Result<User> {
        return try {
            // TODO: Implement unfollow logic
            val user = userRepository.findById(targetUserId)
                ?: return Result.failure(Exception("User not found"))
            Result.success(user)
        } catch (e: Exception) {
            logger.error("Error unfollowing user $targetUserId", e)
            Result.failure(e)
        }
    }
}