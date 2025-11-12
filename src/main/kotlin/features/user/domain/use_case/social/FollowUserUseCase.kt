package com.sukakotlin.features.user.domain.use_case.social

import com.sukakotlin.features.user.domain.model.social.UserDetail
import com.sukakotlin.features.user.domain.repository.FollowsRepository
import com.sukakotlin.features.user.domain.repository.UsersRepository
import java.lang.Exception

class FollowUserUseCase(
    private val usersRepository: UsersRepository,
    private val followsRepository: FollowsRepository
) {
    suspend fun follow(
        currentUserId: String,
        targetUserId: String
    ): Result<UserDetail> {
        return try {
            validateUsers(currentUserId, targetUserId)

            followsRepository.follow(currentUserId, targetUserId)

            Result.success(
                buildUserFollowInfo(currentUserId, targetUserId)
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun unfollow(
        currentUserId: String,
        targetUserId: String
    ): Result<UserDetail> {
        return try {
            validateUsers(currentUserId, targetUserId)

            followsRepository.unfollow(currentUserId, targetUserId)

            Result.success(
                buildUserFollowInfo(currentUserId, targetUserId)
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun validateUsers(vararg userIds: String) {
        if (userIds.size != 2) {
            throw IllegalArgumentException("Exactly 2 user ids must be provided")
        }

        val (currentUserId, targetUserId) = userIds

        if (currentUserId == targetUserId) {
            throw IllegalArgumentException("Cannot follow/unfollow yourself")
        }

        usersRepository.findById(currentUserId)
            ?: throw IllegalArgumentException("Current user not found")

        usersRepository.findById(targetUserId)
            ?: throw IllegalArgumentException("Target user not found")
    }

    private suspend fun buildUserFollowInfo(
        currentUserId: String,
        targetUserId: String
    ): UserDetail {
        val targetUser = usersRepository.findById(targetUserId)!!
        val isFollowing = followsRepository.isFollowing(currentUserId, targetUserId)
        val followerCount = followsRepository.getFollowerCount(targetUserId)
        val followingCount = followsRepository.getFollowingCount(targetUserId)

        return UserDetail(
            user = targetUser,
            isMe = false,
            isFollowing = isFollowing,
            followerCount = followerCount,
            followingCount = followingCount
        )
    }
}