package com.sukakotlin.data.database.migration

import org.jetbrains.exposed.v1.jdbc.JdbcTransaction

data class Migration(
    val version: Int,
    val run: JdbcTransaction.() -> Unit
)