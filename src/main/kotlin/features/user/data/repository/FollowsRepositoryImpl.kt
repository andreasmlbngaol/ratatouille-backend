package com.sukakotlin.features.user.data.repository

import com.sukakotlin.data.repository.BaseRepositoryImpl
import com.sukakotlin.features.user.data.entity.FollowsEntity
import com.sukakotlin.features.user.data.table.FollowsTable
import com.sukakotlin.features.user.data.utils.now
import com.sukakotlin.features.user.domain.model.social.Follow
import com.sukakotlin.features.user.domain.repository.FollowsRepository
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.exposed.v1.core.SortOrder
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.statements.UpsertSqlExpressionBuilder.eq
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import kotlin.time.Clock

object FollowsRepositoryImpl:
        BaseRepositoryImpl<Long, FollowsEntity, Follow>(FollowsEntity.Companion),
        FollowsRepository {

    private fun FollowsEntity.toFollow() = Follow(
        id = this.id.value,
        followerId = this.followerId,
        followingId = this.followingId,
        createdAt = this.createdAt
    )

    override fun FollowsEntity.toDomain(): Follow = this.toFollow()


    override suspend fun follow(followerId: String, followingId: String): Boolean = transaction {
        val existing = FollowsEntity.find {
            (FollowsTable.followerId eq followerId) and (FollowsTable.followingId eq followingId)
        }.firstOrNull()

        if(existing == null) {
            saveEntity {
                this.followerId = followerId
                this.followingId = followingId
                this.createdAt = now
            }
            true
        } else {
            false
        }
    }

    override suspend fun unfollow(followerId: String, followingId: String): Boolean = transaction {
        FollowsTable.deleteWhere {
            (FollowsTable.followerId eq followerId) and (FollowsTable.followingId eq followingId)
        } > 0
    }

    override suspend fun isFollowing(followerId: String, followingId: String): Boolean = transaction {
        !FollowsEntity.find {
            (FollowsTable.followerId eq followerId) and (FollowsTable.followingId eq followingId)
        }.empty()
    }

    override suspend fun getFollowerCount(userId: String): Long = transaction {
        FollowsEntity.find { FollowsTable.followingId eq userId }.count()
    }


    override suspend fun getFollowingCount(userId: String): Long = transaction {
        FollowsEntity.find { FollowsTable.followingId eq userId }.count()
    }


    override suspend fun getFollowers(
        userId: String,
        limit: Int,
        offset: Int
    ): List<Follow> = transaction {
        FollowsEntity.find { FollowsTable.followingId eq userId }
            .orderBy(FollowsTable.createdAt to SortOrder.DESC)
            .limit(limit)
            .offset(offset.toLong())
            .map { it.toDomain() }
    }

    override suspend fun getFollowings(
        userId: String,
        limit: Int,
        offset: Int
    ): List<Follow> = transaction {
        FollowsEntity.find { FollowsTable.followerId eq userId }
            .orderBy(FollowsTable.createdAt to SortOrder.DESC)
            .limit(limit)
            .offset(offset.toLong())
            .map { it.toDomain() }
    }
}