package com.sukakotlin.data.database.migration

import org.jetbrains.exposed.v1.core.Table

object SchemaVersion: Table("schema_version") {
    val version = integer("version").uniqueIndex()
}