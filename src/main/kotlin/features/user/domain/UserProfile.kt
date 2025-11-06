package com.sukakotlin.features.user.domain

data class UserProfile(
    val user: User,
    val stats: UserStats,
    val isMe: Boolean,
    val isFollowing: Boolean // Untuk current user context
)
