package com.sukakotlin.features.user.domain.repository

import com.sukakotlin.domain.repository.BaseRepository
import com.sukakotlin.features.user.domain.model.auth.User

interface UsersRepository: BaseRepository<String, User> {
    suspend fun save(user: User): User
    suspend fun update(id: String, user: User): User?
    suspend fun findByEmail(email: String): User?
    suspend fun existsByEmail(email: String): Boolean
    suspend fun updateName(id: String, name: String): Boolean
    suspend fun updateEmailVerified(id: String, isVerified: Boolean): Boolean
//    suspend fun findUserDetail(id: String): UserDetail?
}