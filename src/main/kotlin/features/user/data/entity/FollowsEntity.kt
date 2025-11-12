package com.sukakotlin.features.user.data.entity

import com.sukakotlin.data.database.entity.BaseEntityClass
import com.sukakotlin.data.database.entity.LongBaseEntity
import com.sukakotlin.features.user.data.table.FollowsTable
import org.jetbrains.exposed.v1.core.dao.id.EntityID

class FollowsEntity(id: EntityID<Long>): LongBaseEntity(id) {
    var followerId by FollowsTable.followerId
    var followingId by FollowsTable.followingId

    companion object: BaseEntityClass<Long, FollowsEntity>(FollowsTable)
}