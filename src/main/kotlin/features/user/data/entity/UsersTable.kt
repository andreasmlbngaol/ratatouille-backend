package com.sukakotlin.features.user.data.entity

import com.sukakotlin.data.database.table.StringBaseTable


object UsersTable: StringBaseTable("users") {
    val name = varchar("name", 100)
    val email = varchar("email", 100).uniqueIndex()
    val profilePictureUrl = varchar("profile_picture_url", 255).nullable()
    val coverPictureUrl = varchar("cover_picture_url", 255).nullable()
    val bio = varchar("bio", 255).nullable()
    val isEmailVerified = bool("is_email_verified")
}