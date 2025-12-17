package com.sukakotlin.repository

import com.sukakotlin.database.tables.recipes.ImagesTable
import com.sukakotlin.database.tables.recipes.IngredientTagsTable
import com.sukakotlin.database.tables.recipes.IngredientsTable
import com.sukakotlin.database.tables.recipes.RecipesImagesTable
import com.sukakotlin.database.tables.recipes.RecipesTable
import com.sukakotlin.database.tables.recipes.StepsImagesTable
import com.sukakotlin.database.tables.recipes.StepsTable
import com.sukakotlin.database.utils.ilikeContains
import com.sukakotlin.model.Image
import com.sukakotlin.model.IngredientTag
import com.sukakotlin.model.IngredientWithTag
import com.sukakotlin.model.Recipe
import com.sukakotlin.model.RecipeDetail
import com.sukakotlin.model.RecipeStatus
import com.sukakotlin.model.RecipeWithImages
import com.sukakotlin.model.StepWithImages
import com.sukakotlin.utils.uppercaseEachWord
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.SortOrder
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.innerJoin
import org.jetbrains.exposed.v1.jdbc.andWhere
import org.jetbrains.exposed.v1.jdbc.insertAndGetId
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.jetbrains.exposed.v1.jdbc.update
import org.slf4j.LoggerFactory

class RecipeRepository {
    private val logger = LoggerFactory.getLogger(this::class.java)

    // ============ MAPPING FUNCTIONS ============

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

    private fun ResultRow.toImage(): Image {
        return Image(
            id = this[ImagesTable.id].value,
            url = this[ImagesTable.url],
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

    private fun ResultRow.toStepWithImages(): StepWithImages {
        val stepId = this[StepsTable.id].value

        val images = StepsImagesTable
            .innerJoin(ImagesTable, { StepsImagesTable.imageId }, { ImagesTable.id })
            .selectAll()
            .where { StepsImagesTable.stepId eq stepId }
            .map { it.toImage() }

        return StepWithImages(
            id = stepId,
            recipeId = this[StepsTable.recipeId],
            stepNumber = this[StepsTable.stepNumber],
            content = this[StepsTable.content],
            images = images
        )
    }

    // ============ RECIPE OPERATIONS ============

    fun save(recipe: Recipe): RecipeWithImages = transaction {
        val id = RecipesTable.insertAndGetId {
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

        RecipeWithImages(
            id = id,
            authorId = recipe.authorId,
            name = recipe.name,
            description = recipe.description,
            isPublic = recipe.isPublic,
            estTimeInMinutes = recipe.estTimeInMinutes,
            portion = recipe.portion,
            status = recipe.status,
            createdAt = recipe.createdAt,
            updatedAt = recipe.updatedAt,
            images = emptyList(),
        )
    }

    fun update(id: Long, recipe: Recipe): RecipeWithImages? = transaction {
        val updatedRows = RecipesTable
            .update ({ RecipesTable.id eq id }) {
                it[name] = recipe.name
                it[description] = recipe.description
                it[isPublic] = recipe.isPublic
                it[estTimeInMinutes] = recipe.estTimeInMinutes
                it[portion] = recipe.portion
                it[status] = recipe.status
            }

        if (updatedRows > 0) {
            findRecipeWithImages(id)
        } else {
            null
        }
    }

    fun findByIdAndAuthorId(id: Long, authorId: String): Recipe? = transaction {
        RecipesTable
            .selectAll()
            .where {
                (RecipesTable.id eq id) and (RecipesTable.authorId eq authorId)
            }
            .singleOrNull()
            ?.toRecipe()
    }

    fun findDraftByAuthorId(authorId: String): RecipeWithImages? = transaction {
        RecipesTable
            .selectAll()
            .where {
                (RecipesTable.authorId eq authorId) and
                        (RecipesTable.status eq RecipeStatus.DRAFT)
            }
            .singleOrNull()
            ?.let { recipeRow ->
                val recipe = recipeRow.toRecipe()
                val images = getRecipeImages(recipe.id)

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
                    images = images,
                )
            }
    }

    fun existsByIdAndAuthorId(id: Long, authorId: String): Boolean = transaction {
        RecipesTable
            .selectAll()
            .where {
                (RecipesTable.id eq id) and (RecipesTable.authorId eq authorId)
            }
            .count() > 0
    }

    fun findRecipeDetail(id: Long): RecipeDetail? = transaction {
        val recipe = RecipesTable
            .selectAll()
            .where { RecipesTable.id eq id }
            .singleOrNull()
            ?.toRecipe() ?: return@transaction null

        val images = getRecipeImages(id)
        val ingredients = getRecipeIngredients(id)
        val steps = getRecipeSteps(id)

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

    fun searchFiltered(
        query: String,
        userId: String,
        minEstTimeInMinutes: Int? = null,
        maxEstTimeInMinutes: Int? = null
    ): List<RecipeDetail> = transaction {
        var baseQuery = RecipesTable
            .selectAll()
            .where {
                (RecipesTable.name ilikeContains query) and
                        (RecipesTable.status eq RecipeStatus.PUBLISHED)
            }

        if (minEstTimeInMinutes != null) {
            baseQuery = baseQuery.andWhere { RecipesTable.estTimeInMinutes greaterEq minEstTimeInMinutes }
        }

        if (maxEstTimeInMinutes != null) {
            baseQuery = baseQuery.andWhere { RecipesTable.estTimeInMinutes lessEq maxEstTimeInMinutes }
        }

        baseQuery
            .orderBy(RecipesTable.createdAt to SortOrder.DESC)
            .mapNotNull { recipeRow ->
                val recipe = recipeRow.toRecipe()

                // Filter: public atau milik user
                if (!recipe.isPublic && recipe.authorId != userId) {
                    return@mapNotNull null
                }

                val images = getRecipeImages(recipe.id)
                val ingredients = getRecipeIngredients(recipe.id)
                val steps = getRecipeSteps(recipe.id)

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

    fun addRecipeImage(recipeId: Long, image: Image): RecipeWithImages = transaction {
        val imageId = ImagesTable.insertAndGetId {
            it[ImagesTable.url] = image.url
        }.value

        RecipesImagesTable.insertAndGetId {
            it[RecipesImagesTable.recipeId] = recipeId
            it[RecipesImagesTable.imageId] = imageId
        }

        findRecipeWithImages(recipeId)!!
    }

    fun addStepImage(recipeId: Long, stepId: Long, image: Image): List<StepWithImages> = transaction {
        val imageId = ImagesTable.insertAndGetId {
            it[ImagesTable.url] = image.url
        }.value

        StepsImagesTable.insertAndGetId {
            it[StepsImagesTable.stepId] = stepId
            it[StepsImagesTable.imageId] = imageId
        }

        getRecipeSteps(recipeId)
    }

    private fun getRecipeImages(recipeId: Long): List<Image> {
        return (RecipesImagesTable innerJoin ImagesTable)
            .selectAll()
            .where { RecipesImagesTable.recipeId eq recipeId }
            .map { it.toImage() }
    }

    private fun findRecipeWithImages(recipeId: Long): RecipeWithImages? {
        val recipe = RecipesTable
            .selectAll()
            .where { RecipesTable.id eq recipeId }
            .singleOrNull()
            ?.toRecipe() ?: return null

        val images = getRecipeImages(recipeId)

        return RecipeWithImages(
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
        )
    }

    fun addIngredient(
        recipeId: Long,
        tagId: Long,
        amount: Double?,
        unit: String?,
        alternative: String?
    ): List<IngredientWithTag> = transaction {
        IngredientsTable.insertAndGetId {
            it[IngredientsTable.recipeId] = recipeId
            it[IngredientsTable.tagId] = tagId
            it[IngredientsTable.amount] = amount
            it[IngredientsTable.unit] = unit
            it[IngredientsTable.alternative] = alternative
        }

        getRecipeIngredients(recipeId)
    }

    private fun getRecipeIngredients(recipeId: Long): List<IngredientWithTag> {
        return (IngredientsTable innerJoin IngredientTagsTable)
            .selectAll()
            .where { IngredientsTable.recipeId eq recipeId }
            .map { it.toIngredientWithTag() }
    }

    fun addStep(
        recipeId: Long,
        stepNumber: Int,
        content: String = ""
    ): List<StepWithImages> = transaction {
        StepsTable.insertAndGetId {
            it[StepsTable.recipeId] = recipeId
            it[StepsTable.stepNumber] = stepNumber
            it[StepsTable.content] = content
        }

        getRecipeSteps(recipeId)
    }

    fun updateStep(recipeId: Long, stepId: Long, content: String): List<StepWithImages> = transaction {
        StepsTable.update({ StepsTable.id eq stepId }) {
            it[StepsTable.content] = content
        }

        getRecipeSteps(recipeId)
    }

    private fun getRecipeSteps(recipeId: Long): List<StepWithImages> {
        return StepsTable
            .selectAll()
            .where { StepsTable.recipeId eq recipeId }
            .orderBy(StepsTable.stepNumber)
            .map { it.toStepWithImages() }
    }
}