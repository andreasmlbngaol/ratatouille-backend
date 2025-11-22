package com.sukakotlin.features.recipe.data.repository

import com.sukakotlin.data.database.util.ilikeContains
import com.sukakotlin.data.database.util.insertWithTimestampsAndGetId
import com.sukakotlin.features.recipe.data.table.IngredientTagsTable
import com.sukakotlin.features.recipe.domain.model.ingredient.IngredientTag
import com.sukakotlin.features.recipe.domain.repository.IngredientTagsRepository
import com.sukakotlin.shared.util.uppercaseEachWord
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.SortOrder
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

object IngredientTagsRepositoryImpl: IngredientTagsRepository {
    private fun ResultRow.toIngredientTag() = IngredientTag(
        id = this[IngredientTagsTable.id].value,
        name = this[IngredientTagsTable.name].uppercaseEachWord()
    )

    override suspend fun save(tag: IngredientTag): IngredientTag = transaction {
        try {
            val id = IngredientTagsTable.insertWithTimestampsAndGetId {
                it[this.name] = tag.name
            }

            IngredientTag(
                id = id.value,
                name = tag.name.uppercaseEachWord()
            )
        } catch (e: Exception) {
            throw IllegalStateException("Tag already exists", e)
        }
    }

    override suspend fun searchByName(query: String): List<IngredientTag> = transaction {
        IngredientTagsTable
            .selectAll()
            .where { IngredientTagsTable.name ilikeContains query }
            .orderBy(IngredientTagsTable.name to SortOrder.ASC)
            .limit(8)
            .map { it.toIngredientTag() }
    }
}