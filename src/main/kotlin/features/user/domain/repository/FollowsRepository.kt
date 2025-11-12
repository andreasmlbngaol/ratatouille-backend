package com.sukakotlin.features.user.domain.repository

import com.sukakotlin.domain.repository.BaseRepository
import com.sukakotlin.features.user.domain.model.social.Follow

interface FollowsRepository: BaseRepository<Long, Follow> {
    suspend fun follow(followerId: String, followingId: String): Boolean
    suspend fun unfollow(followerId: String, followingId: String): Boolean
    suspend fun isFollowing(followerId: String, followingId: String): Boolean
    suspend fun getFollowerCount(userId: String): Long
    suspend fun getFollowingCount(userId: String): Long
    suspend fun getFollowers(userId: String, limit: Int, offset: Int): List<Follow>
    suspend fun getFollowings(userId: String, limit: Int, offset: Int): List<Follow>
}