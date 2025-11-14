package com.sukakotlin.features.user.data.table

import com.sukakotlin.data.database.table.LongBaseTable
import org.jetbrains.exposed.v1.core.ReferenceOption

object FollowsTable: LongBaseTable("follows") {
    val followerId = varchar("follower_id", 64).references(UsersTable.id, onDelete = ReferenceOption.CASCADE)
    val followingId = varchar("following_id", 64).references(UsersTable.id, onDelete = ReferenceOption.CASCADE)

    init {
        uniqueIndex(followerId, followingId)
    }
}