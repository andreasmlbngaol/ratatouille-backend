package com.sukakotlin.data.database.table

import org.jetbrains.exposed.v1.core.Column
import org.jetbrains.exposed.v1.core.dao.id.EntityID

open class StringBaseTable(
    name: String,
    columnName: String = "id"
): BaseTable<String>(name) {
    final override val id: Column<EntityID<String>> = varchar(columnName, 64).entityId()
    final override val primaryKey = PrimaryKey(id)
}
