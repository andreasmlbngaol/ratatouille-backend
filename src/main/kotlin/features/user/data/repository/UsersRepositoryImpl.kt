package com.sukakotlin.features.user.data.repository

import com.sukakotlin.data.database.util.insertWithTimestampsAndGetId
import com.sukakotlin.data.database.util.updateWithTimestamps
import com.sukakotlin.features.user.data.table.UsersTable
import com.sukakotlin.features.user.domain.model.auth.User
import com.sukakotlin.features.user.domain.repository.UsersRepository
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

object UsersRepositoryImpl: UsersRepository {
    private fun ResultRow.toUser() = User(
        id = this[UsersTable.id].value,
        name = this[UsersTable.name],
        email = this[UsersTable.email],
        profilePictureUrl = this[UsersTable.profilePictureUrl],
        coverPictureUrl = this[UsersTable.coverPictureUrl],
        bio = this[UsersTable.bio],
        isEmailVerified = this[UsersTable.isEmailVerified],
        createdAt = this[UsersTable.createdAt]
    )

    override suspend fun findById(id: String): User? = transaction {
        UsersTable.selectAll()
            .where { UsersTable.id eq id }
            .singleOrNull()
            ?.toUser()
    }

    override suspend fun save(user: User): User = transaction {
        UsersTable.insertWithTimestampsAndGetId {
            it[id] = user.id
            it[name] = user.name
            it[email] = user.email
            it[profilePictureUrl] = user.profilePictureUrl
            it[coverPictureUrl] = user.coverPictureUrl
            it[bio] = user.bio
            it[isEmailVerified] = user.isEmailVerified
            it[createdAt] = user.createdAt
        }

        user
    }

    override suspend fun update(id: String, user: User): User? = transaction {
        val updated = UsersTable.updateWithTimestamps({ UsersTable.id eq id }) {
            it[name] = user.name
            it[email] = user.email
            it[profilePictureUrl] = user.profilePictureUrl
            it[coverPictureUrl] = user.coverPictureUrl
            it[bio] = user.bio
        }

        user.takeIf { updated > 0 }
    }

    override suspend fun updateEmailVerified(id: String, isVerified: Boolean): Boolean = transaction {
        UsersTable.updateWithTimestamps({ UsersTable.id eq id }) {
            it[UsersTable.isEmailVerified] = isVerified
        } > 0
    }
}