package com.sukakotlin.features.user.domain.use_case.social

import com.sukakotlin.features.user.domain.model.social.UserDetail
import com.sukakotlin.features.user.domain.repository.FollowsRepository
import com.sukakotlin.features.user.domain.repository.UsersRepository

class GetUserDetailUseCase(
    private val usersRepository: UsersRepository,
    private val followsRepository: FollowsRepository
) {
    private val logger = org.slf4j.LoggerFactory.getLogger(this::class.java)

    suspend operator fun invoke(
        targetUserId: String,
        currentUserId: String
    ): Result<UserDetail> {
        return try {
            val user = usersRepository.findById(targetUserId)
                ?: return Result.failure(IllegalArgumentException("User not found"))

            val (isMe, isFollowing) = if (currentUserId != targetUserId) {
                Pair(
                    false,
                    followsRepository.isFollowing(currentUserId, targetUserId)
                )
            } else {
                Pair(true,false)
            }

            val followerCount = followsRepository.getFollowerCount(targetUserId)
            val followingCount = followsRepository.getFollowingCount(targetUserId)

            Result.success(
                UserDetail(
                    user = user,
                    isMe = isMe,
                    isFollowing = isFollowing,
                    followerCount = followerCount,
                    followingCount = followingCount
                )
            )

        } catch (e: Exception) {
            logger.error("Error getting user detail", e)
            Result.failure(e)
        }
    }
}