package com.sukakotlin.domain.repository

interface BaseRepository<ID, D> {
    suspend fun findById(id: ID): D?
    suspend fun findAll(): List<D>
    suspend fun delete(id: ID): Boolean
    suspend fun count(): Long
    suspend fun existsById(id: ID): Boolean
    suspend fun findPaged(limit: Int, offset: Int): List<D>
}