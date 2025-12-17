package com.sukakotlin.database.tables.recipes

import org.jetbrains.exposed.v1.core.dao.id.LongIdTable

object ImagesTable: LongIdTable("images") {
    val url = varchar("url", 255)
}