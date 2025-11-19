package com.sukakotlin.features.user.domain.repository

import com.sukakotlin.features.user.domain.model.auth.User

interface UsersRepository {
    suspend fun findById(id: String): User?
    suspend fun save(user: User): User
    suspend fun update(id: String, user: User): User?
    suspend fun updateEmailVerified(id: String, isVerified: Boolean): Boolean

//    suspend fun findUserDetail(id: String): UserDetail?
}