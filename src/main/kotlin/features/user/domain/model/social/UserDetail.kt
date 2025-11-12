package com.sukakotlin.features.user.domain.model.social

import com.sukakotlin.features.user.domain.model.auth.User

data class UserDetail(
    val user: User,
    val isMe: Boolean,
    val isFollowing: Boolean,
    val followerCount: Long,
    val followingCount: Long
)
