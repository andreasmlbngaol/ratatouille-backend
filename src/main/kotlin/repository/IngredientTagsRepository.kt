package com.sukakotlin.repository

import com.sukakotlin.database.tables.recipes.IngredientTagsTable
import com.sukakotlin.database.utils.ilikeContains
import com.sukakotlin.model.IngredientTag
import com.sukakotlin.utils.uppercaseEachWord
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.SortOrder
import org.jetbrains.exposed.v1.jdbc.insertAndGetId
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.slf4j.LoggerFactory

class IngredientTagsRepository {
    private val logger = LoggerFactory.getLogger(this::class.java)

    private fun ResultRow.toIngredientTag() = IngredientTag(
        id = this[IngredientTagsTable.id].value,
        name = this[IngredientTagsTable.name].uppercaseEachWord()
    )

    fun save(tag: IngredientTag): IngredientTag = transaction {
        try {
            val id = IngredientTagsTable.insertAndGetId {
                it[name] = tag.name.uppercase()
            }

            IngredientTag(
                id = id.value,
                name = tag.name.uppercaseEachWord()
            )
        } catch (e: Exception) {
            logger.error("Failed to save ingredient tag: ${tag.name}", e)
            throw IllegalStateException("Tag already exists or failed to create", e)
        }
    }

    fun searchByName(query: String): List<IngredientTag> = transaction {
        IngredientTagsTable
            .selectAll()
            .where { IngredientTagsTable.name ilikeContains query }
            .orderBy(IngredientTagsTable.name to SortOrder.ASC)
            .limit(8)
            .map { it.toIngredientTag() }
    }

    fun searchByNameExact(name: String): IngredientTag? = transaction {
        IngredientTagsTable
            .selectAll()
            .where { IngredientTagsTable.name eq name }
            .singleOrNull()
            ?.toIngredientTag()
    }

    fun findById(id: Long): IngredientTag? = transaction {
        IngredientTagsTable
            .selectAll()
            .where { IngredientTagsTable.id eq id }
            .singleOrNull()
            ?.toIngredientTag()
    }

    fun existsById(id: Long): Boolean = transaction {
        IngredientTagsTable
            .selectAll()
            .where { IngredientTagsTable.id eq id }
            .count() > 0
    }
}