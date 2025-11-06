package com.sukakotlin.core.database.util

import org.jetbrains.exposed.v1.core.Column
import org.jetbrains.exposed.v1.core.Op
import org.jetbrains.exposed.v1.core.SqlExpressionBuilder
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.core.dao.id.IdTable
import org.jetbrains.exposed.v1.core.statements.BatchInsertStatement
import org.jetbrains.exposed.v1.core.statements.InsertStatement
import org.jetbrains.exposed.v1.core.statements.UpdateStatement
import org.jetbrains.exposed.v1.datetime.CurrentDateTime
import org.jetbrains.exposed.v1.datetime.datetime
import org.jetbrains.exposed.v1.jdbc.batchInsert
import org.jetbrains.exposed.v1.jdbc.insertAndGetId
import org.jetbrains.exposed.v1.jdbc.update

abstract class BaseTable<Key: Any>(name: String): IdTable<Key>(name) {
    val createdAt = datetime("created_at").defaultExpression(CurrentDateTime)
    val updatedAt = datetime("updated_at").defaultExpression(CurrentDateTime)
}

open class LongBaseTable(
    name: String,
    columnName: String = "id"
): BaseTable<Long>(name) {
    final override val id: Column<EntityID<Long>> = long(columnName).autoIncrement().entityId()
    final override val primaryKey = PrimaryKey(id)
}

open class StringBaseTable(
    name: String,
    columnName: String = "id"
): BaseTable<String>(name) {
    final override val id: Column<EntityID<String>> = varchar(columnName, 64).entityId()
    final override val primaryKey = PrimaryKey(id)
}

fun <Key: Any, T: BaseTable<Key>> T.insertWithTimestamps(
    body: T.(InsertStatement<EntityID<Key>>) -> Unit
) = insertAndGetId {
    body(it)
    it[createdAt] = CurrentDateTime
    it[updatedAt] = CurrentDateTime
}

fun <Key: Any, T: BaseTable<Key>> T.updateWithTimestamps(
    where: SqlExpressionBuilder.() -> Op<Boolean>,
    limit: Int? = null,
    body: T.(UpdateStatement) -> Unit
): Int = update(where, limit) {
    body(it)
    it[updatedAt] = CurrentDateTime
}

fun <Key: Any, T: BaseTable<Key>, E> T.batchInsertWithTimestamps(
    data: Iterable<E>,
    ignore: Boolean = false,
    shouldReturnGeneratedValues: Boolean = true,
    body: BatchInsertStatement.(E) -> Unit
) = batchInsert(
    data,
    ignore,
    shouldReturnGeneratedValues,
) {
    this[createdAt] = CurrentDateTime
    this[updatedAt] = CurrentDateTime
    body(it)
}