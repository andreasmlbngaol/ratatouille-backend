package com.sukakotlin.model

import kotlinx.serialization.Serializable

@Serializable
data class UserDetail(
    val user: User,
    val recipes: List<RecipeDetail>,
    val isMe: Boolean,
    val isFollower: Boolean?,
    val isFollowing: Boolean?,
    val followersCount: Long,
    val followingCount: Long
)