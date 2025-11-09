package com.sukakotlin.data.database.entity

import com.sukakotlin.data.database.table.BaseTable
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.exposed.v1.dao.EntityClass
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

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