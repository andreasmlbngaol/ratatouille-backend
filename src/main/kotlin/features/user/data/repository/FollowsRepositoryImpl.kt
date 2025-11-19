package com.sukakotlin.features.user.data.repository

import com.sukakotlin.data.database.util.insertWithTimestampsAndGetId
import com.sukakotlin.features.user.data.table.FollowsTable
import com.sukakotlin.shared.util.now
import com.sukakotlin.features.user.domain.model.social.Follow
import com.sukakotlin.features.user.domain.repository.FollowsRepository
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.SortOrder
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.statements.UpsertSqlExpressionBuilder.eq
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.select
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

object FollowsRepositoryImpl: FollowsRepository {

    private fun ResultRow.toFollow() = Follow(
        id = this[FollowsTable.id].value,
        followerId = this[FollowsTable.followerId],
        followingId = this[FollowsTable.followingId],
        createdAt = this[FollowsTable.createdAt]
    )

    private fun existsByFollowerIdAndFollowingId(followerId: String, followingId: String): Boolean = transaction {
        FollowsTable
            .select(FollowsTable.id)
            .where {
                (FollowsTable.followerId eq followerId) and (FollowsTable.followingId eq followingId)
            }
            .any()
    }

    override suspend fun follow(followerId: String, followingId: String): Boolean = transaction {
        existsByFollowerIdAndFollowingId(followerId, followingId)

        if(!existsByFollowerIdAndFollowingId(followerId, followingId)) {
            FollowsTable.insertWithTimestampsAndGetId {
                it[this.followerId] = followerId
                it[this.followingId] = followingId
                it[this.createdAt] = now
            }.value > 0
        } else {
            false
        }
    }

    override suspend fun unfollow(followerId: String, followingId: String): Boolean = transaction {
        FollowsTable.deleteWhere {
            (FollowsTable.followerId eq followerId) and (FollowsTable.followingId eq followingId)
        } > 0
    }

    override suspend fun isFollowing(followerId: String, followingId: String): Boolean =
        existsByFollowerIdAndFollowingId(followerId, followingId)

    override suspend fun getFollowerCount(userId: String): Long = transaction {
        FollowsTable
            .select(FollowsTable.id)
            .where { FollowsTable.followingId eq userId }
            .count()
    }


    override suspend fun getFollowingCount(userId: String): Long = transaction {
        FollowsTable
            .select(FollowsTable.id)
            .where { FollowsTable.followerId eq userId }
            .count()
    }


    override suspend fun getFollowers(
        userId: String,
        limit: Int,
        offset: Int
    ): List<Follow> = transaction {
        FollowsTable
            .selectAll()
            .where { FollowsTable.followingId eq userId }
            .orderBy(FollowsTable.createdAt to SortOrder.DESC)
            .limit(limit)
            .offset(offset.toLong())
            .map { it.toFollow() }
    }

    override suspend fun getFollowings(
        userId: String,
        limit: Int,
        offset: Int
    ): List<Follow> = transaction {
        FollowsTable
            .selectAll()
            .where { FollowsTable.followerId eq userId }
            .orderBy(FollowsTable.createdAt to SortOrder.DESC)
            .limit(limit)
            .offset(offset.toLong())
            .map { it.toFollow() }
    }
}