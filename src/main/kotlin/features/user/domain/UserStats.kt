package com.sukakotlin.features.user.domain

data class UserStats(
    val userId: String,
    val followerCount: Int,
    val followingCount: Int,
    val recipeCount: Int,
    val totalLikes: Int
)
