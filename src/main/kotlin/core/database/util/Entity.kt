package com.sukakotlin.core.database.util

import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.dao.Entity
import org.jetbrains.exposed.v1.dao.EntityBatchUpdate
import org.jetbrains.exposed.v1.dao.EntityClass
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
abstract class BaseEntity<Key: Comparable<Key>>(id: EntityID<Key>): Entity<Key>(id) {
    val baseTable: BaseTable<Key> by lazy {
        (klass.table as? BaseTable<Key>) ?: error("Table must extend BaseTable")
    }

    var createdAt by baseTable.createdAt
    var updatedAt by baseTable.updatedAt

    override fun flush(batch: EntityBatchUpdate?): Boolean {
        updatedAt = Clock.System.now().toLocalDateTime(TimeZone.of("Asia/Jakarta"))
        return super.flush(batch)
    }
}

open class StringBaseEntity(id: EntityID<String>): BaseEntity<String>(id)
open class LongBaseEntity(id: EntityID<Long>): BaseEntity<Long>(id)
open class IntBaseEntity(id: EntityID<Int>): BaseEntity<Int>(id)

@OptIn(ExperimentalTime::class)
open class BaseEntityClass<
        ID: Comparable<ID>,
        E: BaseEntity<ID>
        >(table: BaseTable<ID>): EntityClass<ID, E>(table) {
    override fun new(init: E.() -> Unit): E {
        return super.new {
            val now = Clock.System.now().toLocalDateTime(TimeZone.of("Asia/Jakarta"))
            createdAt = now
            updatedAt = now
            init()
        }
    }

    override fun new(id: ID?, init: E.() -> Unit): E {
        return super.new(id) {
            val now = Clock.System.now().toLocalDateTime(TimeZone.of("Asia/Jakarta"))
            createdAt = now
            updatedAt = now
            init()
        }
    }
}