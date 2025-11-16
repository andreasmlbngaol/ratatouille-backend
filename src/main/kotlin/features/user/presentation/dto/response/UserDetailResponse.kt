package com.sukakotlin.features.user.presentation.dto.response

import com.sukakotlin.features.user.domain.model.social.UserDetail
import kotlinx.serialization.Serializable

@Serializable
data class UserDetailDto(
    val user: UserDto,
    val isMe: Boolean,
    val isFollowing: Boolean,
    val followerCount: Long,
    val followingCount: Long
)

@Serializable
data class UserDetailResponse(
    val success: Boolean = true,
    val message: String? = null,
    val data: UserDetailDto
)

fun UserDetail.toDto() = UserDetailDto(
    user = user.toDto(),
    isMe = isMe,
    isFollowing = isFollowing,
    followerCount = followerCount,
    followingCount = followingCount
)

fun UserDetail.toResponse() = UserDetailResponse(data = this.toDto())