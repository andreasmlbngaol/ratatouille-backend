package com.sukakotlin.features.recipe.data.repository

import com.sukakotlin.data.database.util.insertWithTimestampsAndGetId
import com.sukakotlin.data.database.util.updateWithTimestamps
import com.sukakotlin.features.recipe.data.table.ImagesTable
import com.sukakotlin.features.recipe.data.table.IngredientTagsTable
import com.sukakotlin.features.recipe.data.table.IngredientsTable
import com.sukakotlin.features.recipe.data.table.RecipesImagesTable
import com.sukakotlin.features.recipe.data.table.RecipesTable
import com.sukakotlin.features.recipe.data.table.StepsImagesTable
import com.sukakotlin.features.recipe.data.table.StepsTable
import com.sukakotlin.features.recipe.domain.model.*
import com.sukakotlin.features.recipe.domain.model.ingredient.IngredientTag
import com.sukakotlin.features.recipe.domain.model.ingredient.IngredientWithTag
import com.sukakotlin.features.recipe.domain.model.recipe.Recipe
import com.sukakotlin.features.recipe.domain.model.recipe.RecipeDetail
import com.sukakotlin.features.recipe.domain.model.recipe.RecipeStatus
import com.sukakotlin.features.recipe.domain.model.recipe.RecipeWithImages
import com.sukakotlin.features.recipe.domain.model.step.StepWithImages
import com.sukakotlin.features.recipe.domain.repository.RecipesRepository
import com.sukakotlin.shared.util.uppercaseEachWord
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.innerJoin
import org.jetbrains.exposed.v1.jdbc.insertAndGetId
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.slf4j.LoggerFactory

object RecipesRepositoryImpl: RecipesRepository {
    private val logger = LoggerFactory.getLogger(this::class.java)
    private fun ResultRow.toRecipe() = Recipe(
        id = this[RecipesTable.id].value,
        authorId = this[RecipesTable.authorId],
        name = this[RecipesTable.name],
        description = this[RecipesTable.description],
        isPublic = this[RecipesTable.isPublic],
        estTimeInMinutes = this[RecipesTable.estTimeInMinutes],
        portion = this[RecipesTable.portion],
        status = this[RecipesTable.status],
        createdAt = this[RecipesTable.createdAt],
        updatedAt = this[RecipesTable.updatedAt],
    )

    override suspend fun save(recipe: Recipe): RecipeWithImages = transaction {
        val id = RecipesTable.insertWithTimestampsAndGetId {
            it[RecipesTable.authorId] = recipe.authorId
            it[RecipesTable.name] = recipe.name
            it[RecipesTable.description] = recipe.description
            it[RecipesTable.isPublic] = recipe.isPublic
            it[RecipesTable.estTimeInMinutes] = recipe.estTimeInMinutes
            it[RecipesTable.portion] = recipe.portion
            it[RecipesTable.status] = recipe.status
            it[RecipesTable.createdAt] = recipe.createdAt
            it[RecipesTable.updatedAt] = recipe.updatedAt
        }.value

        val savedRecipe = recipe.copy(id = id)

        RecipeWithImages(
            id = savedRecipe.id,
            authorId = savedRecipe.authorId,
            name = savedRecipe.name,
            description = savedRecipe.description,
            isPublic = savedRecipe.isPublic,
            estTimeInMinutes = savedRecipe.estTimeInMinutes,
            portion = savedRecipe.portion,
            status = savedRecipe.status,
            createdAt = savedRecipe.createdAt,
            updatedAt = savedRecipe.updatedAt,
            images = emptyList(),
        )
    }

    override suspend fun update(
        id: Long,
        recipe: Recipe
    ): RecipeWithImages? = transaction {
        val updatedRows = RecipesTable
            .updateWithTimestamps({ RecipesTable.id eq id }) {
                it[name] = recipe.name
                it[description] = recipe.description
                it[isPublic] = recipe.isPublic
                it[estTimeInMinutes] = recipe.estTimeInMinutes
                it[portion] = recipe.portion
                it[status] = recipe.status
            }
        if (updatedRows > 0) {
            RecipesTable
                .selectAll()
                .where { RecipesTable.id eq id }
                .map { recipeRow ->
                    val updatedRecipe = recipeRow.toRecipe()
                    val images = RecipesImagesTable
                        .innerJoin(ImagesTable, { RecipesImagesTable.imageId }, { ImagesTable.id })
                        .selectAll()
                        .where { RecipesImagesTable.recipeId eq id }
                        .map { it.toImage() }

                    RecipeWithImages(
                        id = updatedRecipe.id,
                        authorId = updatedRecipe.authorId,
                        name = updatedRecipe.name,
                        description = updatedRecipe.description,
                        isPublic = updatedRecipe.isPublic,
                        estTimeInMinutes = updatedRecipe.estTimeInMinutes,
                        portion = updatedRecipe.portion,
                        status = updatedRecipe.status,
                        createdAt = updatedRecipe.createdAt,
                        updatedAt = updatedRecipe.updatedAt,
                        images = images
                    )
                }
                .singleOrNull()
        } else {
            null
        }
    }

    override suspend fun findDraftByAuthorId(authorId: String): RecipeWithImages? = transaction {
        RecipesTable
            .selectAll()
            .where {
                (RecipesTable.authorId eq authorId) and
                        (RecipesTable.isPublic eq false) and
                        (RecipesTable.status eq RecipeStatus.DRAFT)
            }
            .map { recipeRow ->
                val updatedRecipe = recipeRow.toRecipe()
                val recipeId = recipeRow[RecipesTable.id].value  // ← Ambil dari recipeRow

                val images = RecipesImagesTable
                    .innerJoin(ImagesTable, { RecipesImagesTable.imageId }, { ImagesTable.id })
                    .selectAll()
                    .where { RecipesImagesTable.recipeId eq recipeId }
                    .map { it.toImage() }

                RecipeWithImages(
                    id = updatedRecipe.id,
                    authorId = updatedRecipe.authorId,
                    name = updatedRecipe.name,
                    description = updatedRecipe.description,
                    isPublic = updatedRecipe.isPublic,
                    estTimeInMinutes = updatedRecipe.estTimeInMinutes,
                    portion = updatedRecipe.portion,
                    status = updatedRecipe.status,
                    createdAt = updatedRecipe.createdAt,
                    updatedAt = updatedRecipe.updatedAt,
                    images = images,
                )
            }
            .singleOrNull()
    }

    override suspend fun findByIdAndAuthorId(
        id: Long,
        authorId: String
    ): Recipe? = transaction {
        RecipesTable
            .selectAll()
            .where {
                (RecipesTable.id eq id) and (RecipesTable.authorId eq authorId)
            }
            .singleOrNull()
            ?.toRecipe()
    }

    override suspend fun existByIdAndAuthorId(id: Long, authorId: String): Boolean = transaction {
        RecipesTable
            .selectAll()
            .where {
                (RecipesTable.id eq id) and (RecipesTable.authorId eq authorId)
            }
            .count() > 0
    }

    private fun ResultRow.toImage(): Image {
        logger.info("toImage: $this")
        return Image(
            id = this[ImagesTable.id].value,
            url = this[ImagesTable.url],
            createdAt = this[ImagesTable.createdAt]
        )
    }

    override suspend fun addRecipeImage(
        recipeId: Long,
        image: Image
    ): RecipeWithImages = transaction {
        logger.info("recipeId: $recipeId")
        val imageId = ImagesTable.insertWithTimestampsAndGetId {
            it[ImagesTable.url] = image.url
        }.value

        logger.info("imageId: $imageId")

        RecipesImagesTable.insertWithTimestampsAndGetId {
            it[RecipesImagesTable.recipeId] = recipeId
            it[RecipesImagesTable.imageId] = imageId
        }

        logger.info("inserted")
        val recipe = RecipesTable
            .selectAll()
            .where { RecipesTable.id eq recipeId }
            .singleOrNull()
            ?.toRecipe()

        requireNotNull(recipe)

        val image = RecipesImagesTable
            .innerJoin(ImagesTable, { RecipesImagesTable.imageId }, { ImagesTable.id })
            .selectAll()
            .where { RecipesImagesTable.recipeId eq recipeId }
            .map { it.toImage() }

        RecipeWithImages(
            id = recipe.id,
            authorId = recipe.authorId,
            name = recipe.name,
            description = recipe.description,
            isPublic = recipe.isPublic,
            estTimeInMinutes = recipe.estTimeInMinutes,
            portion = recipe.portion,
            status = recipe.status,
            createdAt = recipe.createdAt,
            updatedAt = recipe.updatedAt,
            images = image,
        )
    }

    private fun ResultRow.toIngredientWithTag() = IngredientWithTag(
        id = this[IngredientsTable.id].value,
        recipeId = this[IngredientsTable.recipeId],
        amount = this[IngredientsTable.amount],
        unit = this[IngredientsTable.unit],
        alternative = this[IngredientsTable.alternative],
        tag = IngredientTag(
            id = this[IngredientTagsTable.id].value,
            name = this[IngredientTagsTable.name].uppercaseEachWord()
        )
    )

    override suspend fun addIngredient(
        recipeId: Long,
        tagId: Long,
        amount: Double?,
        unit: String?,
        alternative: String?
    ): List<IngredientWithTag> = transaction {
        IngredientsTable.insertWithTimestampsAndGetId {
            it[IngredientsTable.recipeId] = recipeId
            it[IngredientsTable.tagId] = tagId
            it[IngredientsTable.amount] = amount
            it[IngredientsTable.unit] = unit
            it[IngredientsTable.alternative] = alternative
        }

        IngredientsTable
            .innerJoin(IngredientTagsTable, { IngredientsTable.tagId }, { IngredientTagsTable.id })
            .selectAll()
            .where { IngredientsTable.recipeId eq recipeId }
            .map { it.toIngredientWithTag() }
    }

    private fun ResultRow.toStepWithImages(): StepWithImages {
        val stepId = this[StepsTable.id].value

        val images = StepsImagesTable
            .innerJoin(ImagesTable, { StepsImagesTable.imageId }, { ImagesTable.id })
            .selectAll()
            .where { StepsImagesTable.stepId eq stepId }
            .map { it.toImage() }

        return StepWithImages(
            images = images,
            id = this[StepsTable.id].value,
            recipeId = this[StepsTable.recipeId],
            stepNumber = this[StepsTable.stepNumber],
            content = this[StepsTable.content]
        )
    }

    override suspend fun addStep(
        recipeId: Long,
        stepNumber: Int,
        content: String
    ): List<StepWithImages> = transaction {
        StepsTable.insertAndGetId {
            it[StepsTable.recipeId] = recipeId
            it[StepsTable.stepNumber] = stepNumber
            it[StepsTable.content] = content
        }

        StepsTable
            .selectAll()
            .where { StepsTable.recipeId eq recipeId }
            .orderBy(StepsTable.stepNumber)
            .map { it.toStepWithImages() }
    }

    override suspend fun updateStep(
        recipeId: Long,
        id: Long,
        content: String
    ): List<StepWithImages> = transaction {
        StepsTable.updateWithTimestamps({ StepsTable.id eq id }) {
            it[StepsTable.content] = content
        }

        StepsTable
            .selectAll()
            .where { StepsTable.recipeId eq recipeId }
            .orderBy(StepsTable.stepNumber)
            .map { it.toStepWithImages() }
    }

    override suspend fun addStepImage(
        userId: String,
        recipeId: Long,
        stepId: Long,
        image: Image
    ): List<StepWithImages> = transaction {
        val imageId = ImagesTable.insertWithTimestampsAndGetId {
            it[ImagesTable.url] = image.url
        }.value

        StepsImagesTable.insertWithTimestampsAndGetId {
            it[StepsImagesTable.stepId] = stepId
            it[StepsImagesTable.imageId] = imageId
        }

        StepsTable
            .selectAll()
            .where { StepsTable.recipeId eq recipeId }
            .orderBy(StepsTable.stepNumber)
            .map { it.toStepWithImages() }
    }

    override suspend fun findRecipeDetail(id: Long): RecipeDetail? = transaction {
        val recipe = RecipesTable
            .selectAll()
            .where { RecipesTable.id eq id }
            .singleOrNull()
            ?.toRecipe() ?: return@transaction null

        val images = (RecipesImagesTable innerJoin ImagesTable)
            .selectAll()
            .where { RecipesImagesTable.recipeId eq id }
            .map { it.toImage() }

        val ingredients = (IngredientsTable innerJoin IngredientTagsTable)
            .selectAll()
            .where { IngredientsTable.recipeId eq id }
            .map { it.toIngredientWithTag() }

        val steps = StepsTable
            .selectAll()
            .where { StepsTable.recipeId eq id }
            .orderBy(StepsTable.stepNumber)
            .map { it.toStepWithImages() }

        RecipeDetail(
            recipe = RecipeWithImages(
                id = recipe.id,
                authorId = recipe.authorId,
                name = recipe.name,
                description = recipe.description,
                isPublic = recipe.isPublic,
                estTimeInMinutes = recipe.estTimeInMinutes,
                portion = recipe.portion,
                status = recipe.status,
                createdAt = recipe.createdAt,
                updatedAt = recipe.updatedAt,
                images = images
            ),
            ingredients = ingredients,
            steps = steps
        )
    }
}