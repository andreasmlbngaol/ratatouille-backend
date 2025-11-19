package com.sukakotlin.features.recipe.data.table

import com.sukakotlin.data.database.table.LongBaseTable

object ImagesTable: LongBaseTable("images") {
    val url = varchar("url", 255)
}