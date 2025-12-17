package com.sukakotlin.database.tables.users

import org.jetbrains.exposed.v1.core.ReferenceOption
import org.jetbrains.exposed.v1.core.dao.id.LongIdTable

object FollowsTable: LongIdTable("follows") {
    val followerId = varchar("follower_id", 64).references(UsersTable.id, onDelete = ReferenceOption.CASCADE)
    val followingId = varchar("following_id", 64).references(UsersTable.id, onDelete = ReferenceOption.CASCADE)

    init {
        uniqueIndex(followerId, followingId)
    }
}