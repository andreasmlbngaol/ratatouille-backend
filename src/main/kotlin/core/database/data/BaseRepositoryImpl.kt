@file:Suppress("unused")

package com.sukakotlin.core.database.data

import com.sukakotlin.core.database.util.BaseEntity
import com.sukakotlin.core.database.util.BaseEntityClass
import org.jetbrains.exposed.v1.core.Op
import org.jetbrains.exposed.v1.core.SqlExpressionBuilder
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

abstract class BaseRepositoryImpl<
        ID: Comparable<ID>,
        E: BaseEntity<ID>,
        D: Any
        >(protected val entityClass: BaseEntityClass<ID, E>) {
    protected abstract fun E.toDomain(): D

    open suspend fun findAll(): List<D> = transaction {
        entityClass.all().map { it.toDomain() }
    }

    open suspend fun findById(id: ID): D? = transaction {
        entityClass.findById(id)?.toDomain()
    }

    open suspend fun delete(id: ID) = transaction {
        entityClass.findById(id)?.delete() != null
    }

    open suspend fun count(): Long = transaction {
        entityClass.count()
    }

    open suspend fun existsById(id: ID) = transaction {
        entityClass.findById(id) != null
    }

    open suspend fun findPaged(limit: Int, offset: Int = 0) = transaction {
        entityClass.all()
            .limit(limit)
            .offset(offset.toLong())
            .map { it.toDomain() }
    }

    protected fun saveEntity(block: E.() -> Unit) = transaction {
        entityClass.new(block).toDomain()
    }

    protected fun saveEntity(id: ID, block: E.() -> Unit) = transaction {
        val test = entityClass.new(id, block)
        println(test)
        test.toDomain()
    }

    protected fun updateEntity(id: ID, block: E.() -> Unit) = transaction {
        entityClass.findById(id)
            ?.apply(block)
            ?.toDomain()
    }

    protected fun exists(where: SqlExpressionBuilder.() -> Op<Boolean>) = transaction {
        !entityClass
            .find(where)
            .empty()
    }
}