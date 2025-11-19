package com.sukakotlin.data.database.util

import com.sukakotlin.data.database.table.BaseTable
import org.jetbrains.exposed.v1.core.Op
import org.jetbrains.exposed.v1.core.SqlExpressionBuilder
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.core.statements.BatchInsertStatement
import org.jetbrains.exposed.v1.core.statements.InsertStatement
import org.jetbrains.exposed.v1.core.statements.UpdateStatement
import org.jetbrains.exposed.v1.datetime.CurrentDateTime
import org.jetbrains.exposed.v1.jdbc.batchInsert
import org.jetbrains.exposed.v1.jdbc.insertAndGetId
import org.jetbrains.exposed.v1.jdbc.update

fun <Key: Any, T: BaseTable<Key>> T.insertWithTimestampsAndGetId(
    body: T.(InsertStatement<EntityID<Key>>) -> Unit
) = insertAndGetId {
    it[createdAt] = CurrentDateTime
    it[updatedAt] = CurrentDateTime
    body(it)
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