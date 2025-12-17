package com.sukakotlin.repository

import com.sukakotlin.database.tables.users.UsersTable
import com.sukakotlin.database.utils.ilikeContains
import com.sukakotlin.model.User
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.jdbc.andWhere
import org.jetbrains.exposed.v1.jdbc.insertAndGetId
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.jetbrains.exposed.v1.jdbc.update

class UserRepository {
    private fun ResultRow.toUser() = User(
        id = this[UsersTable.id].value,
        name = this[UsersTable.name],
        email = this[UsersTable.email],
        profilePictureUrl = this[UsersTable.profilePictureUrl],
        coverPictureUrl = this[UsersTable.coverPictureUrl],
        bio = this[UsersTable.bio],
        isEmailVerified = this[UsersTable.isEmailVerified],
        createdAt = this[UsersTable.createdAt],
        updatedAt = this[UsersTable.updatedAt]
    )

    fun findById(id: String): User? = transaction {
        UsersTable.selectAll()
            .where { UsersTable.id eq id }
            .singleOrNull()
            ?.toUser()
    }

    fun save(user: User) = transaction {
        UsersTable.insertAndGetId {
            it[id] = user.id
            it[name] = user.name
            it[email] = user.email
            it[profilePictureUrl] = user.profilePictureUrl
            it[coverPictureUrl] = user.coverPictureUrl
            it[bio] = user.bio
            it[isEmailVerified] = user.isEmailVerified
            it[createdAt] = user.createdAt
            it[updatedAt] = user.updatedAt
        }
        true
    }

    fun updateProfile(id: String, name: String, bio: String?): User = transaction {
        UsersTable.update({ UsersTable.id eq id }) {
            it[UsersTable.name] = name
            it[UsersTable.bio] = bio
            it[UsersTable.updatedAt] = System.currentTimeMillis()
        }
        findById(id)!!
    }

    fun updateProfilePicture(id: String, pictureUrl: String): User = transaction {
        UsersTable.update({ UsersTable.id eq id }) {
            it[UsersTable.profilePictureUrl] = pictureUrl
            it[UsersTable.updatedAt] = System.currentTimeMillis()
        }
        findById(id)!!
    }

    fun updateCoverPicture(id: String, coverUrl: String): User = transaction {
        UsersTable.update({ UsersTable.id eq id }) {
            it[UsersTable.coverPictureUrl] = coverUrl
            it[UsersTable.updatedAt] = System.currentTimeMillis()
        }
        findById(id)!!
    }

    fun updateEmailVerified(id: String, isEmailVerified: Boolean): Unit = transaction {
        UsersTable.update({ UsersTable.id eq id }) {
            it[UsersTable.isEmailVerified] = isEmailVerified
            it[UsersTable.updatedAt] = System.currentTimeMillis()
        }
    }

    fun searchByName(query: String): List<User> = transaction {
        UsersTable
            .selectAll()
            .where { UsersTable.name ilikeContains query }
            .andWhere { UsersTable.isEmailVerified eq true }
            .map { it.toUser() }
    }

    fun searchByNameExcluding(query: String, excludeUserId: String): List<User> = transaction {
        UsersTable
            .selectAll()
            .where { UsersTable.name ilikeContains query }
            .andWhere { UsersTable.id neq excludeUserId }
            .andWhere { UsersTable.isEmailVerified eq true }
            .map { it.toUser() }
    }
}