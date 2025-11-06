package com.sukakotlin.features.user.data

import com.sukakotlin.core.database.util.BaseEntityClass
import com.sukakotlin.core.database.util.StringBaseEntity
import org.jetbrains.exposed.v1.core.dao.id.EntityID

class UsersEntity(id: EntityID<String>): StringBaseEntity(id) {
    var name by UsersTable.name
    var email by UsersTable.email
    var profilePictureUrl by UsersTable.profilePictureUrl
    var coverPictureUrl by UsersTable.coverPictureUrl
    var bio by UsersTable.bio
    var isEmailVerified by UsersTable.isEmailVerified
    companion object: BaseEntityClass<String, UsersEntity>(UsersTable)
}