package com.sukakotlin.features.user.domain.model

data class UserStats(
    val userId: String,
    val followerCount: Int,
    val followingCount: Int,
    val recipeCount: Int,
    val totalLikes: Int
)
