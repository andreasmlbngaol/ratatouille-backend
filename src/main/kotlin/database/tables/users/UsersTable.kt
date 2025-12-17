package com.sukakotlin.database.tables.users

import org.jetbrains.exposed.v1.core.Column
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.core.dao.id.IdTable

object UsersTable: IdTable<String>("users") {
    override val id: Column<EntityID<String>> =
        varchar("id", 255).entityId()

    override val primaryKey = PrimaryKey(id)

    val name = varchar("name", 100)
    val email = varchar("email", 100).uniqueIndex()
    val profilePictureUrl = varchar("profile_picture_url", 255).nullable()
    val coverPictureUrl = varchar("cover_picture_url", 255).nullable()
    val bio = varchar("bio", 255).nullable()
    val isEmailVerified = bool("is_email_verified")
    val createdAt = long("created_at")
    val updatedAt = long("updated_at")
}