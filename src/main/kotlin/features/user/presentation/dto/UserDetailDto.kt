package com.sukakotlin.features.user.presentation.dto

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

fun UserDetail.toDto() = UserDetailDto(
    user = user.toDto(),
    isMe = isMe,
    isFollowing = isFollowing,
    followerCount = followerCount,
    followingCount = followingCount
)