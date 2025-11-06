package com.sukakotlin.features.user.data

import com.sukakotlin.core.database.data.BaseRepositoryImpl
import com.sukakotlin.core.database.util.updateWithTimestamps
import com.sukakotlin.features.user.domain.User
import com.sukakotlin.features.user.domain.UsersRepository
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

object UsersRepositoryImpl:
    BaseRepositoryImpl<String, UsersEntity, User>(UsersEntity),
    UsersRepository {
    private fun UsersEntity.toUser() = User(
        id = this.id.value,
        name = this.name,
        email = this.email,
        profilePictureUrl = this.profilePictureUrl,
        coverPictureUrl = this.coverPictureUrl,
        bio = this.bio,
        isEmailVerified = this.isEmailVerified,
        createdAt = this.createdAt
    )

    override fun UsersEntity.toDomain(): User = this.toUser()

    override suspend fun save(user: User): User = saveEntity(user.id) {
        name = user.name
        email = user.email
        profilePictureUrl = user.profilePictureUrl
        coverPictureUrl = user.coverPictureUrl
        bio = user.bio
        isEmailVerified = user.isEmailVerified
        createdAt = user.createdAt
    }

    override suspend fun update(id: String, user: User): User? = updateEntity(id) {
        name = user.name
        email = user.email
        profilePictureUrl = user.profilePictureUrl
        coverPictureUrl = user.coverPictureUrl
        bio = user.bio
    }

    override suspend fun findByEmail(email: String): User? = transaction {
        UsersEntity.find { UsersTable.email eq email }
            .singleOrNull()
            ?.toDomain()
    }

    override suspend fun existsByEmail(email: String): Boolean = exists {
        UsersTable.email eq email
    }

    override suspend fun updateName(id: String, name: String): Boolean = transaction {
        UsersTable.updateWithTimestamps({ UsersTable.id eq id}) {
            it[UsersTable.name] = name
        } > 0
    }

    override suspend fun updateEmailVerified(id: String, isVerified: Boolean): Boolean = transaction {
        UsersTable.updateWithTimestamps({ UsersTable.id eq id }) {
            it[UsersTable.isEmailVerified] = isVerified
        } > 0
    }
}