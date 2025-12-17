package com.sukakotlin.repository

import com.sukakotlin.database.tables.recipes.BookmarksTable
import com.sukakotlin.database.tables.recipes.CommentsImagesTable
import com.sukakotlin.database.tables.recipes.CommentsTable
import com.sukakotlin.database.tables.recipes.ImagesTable
import com.sukakotlin.database.tables.recipes.IngredientTagsTable
import com.sukakotlin.database.tables.recipes.IngredientsTable
import com.sukakotlin.database.tables.recipes.RatingsTable
import com.sukakotlin.database.tables.recipes.RecipesImagesTable
import com.sukakotlin.database.tables.recipes.RecipesTable
import com.sukakotlin.database.tables.recipes.StepsImagesTable
import com.sukakotlin.database.tables.recipes.StepsTable
import com.sukakotlin.database.utils.ilikeContains
import com.sukakotlin.model.CommentWithImage
import com.sukakotlin.model.Image
import com.sukakotlin.model.IngredientTag
import com.sukakotlin.model.IngredientWithTag
import com.sukakotlin.model.Recipe
import com.sukakotlin.model.RecipeDetail
import com.sukakotlin.model.RecipeRating
import com.sukakotlin.model.RecipeStatus
import com.sukakotlin.model.RecipeWithImages
import com.sukakotlin.model.StepWithImages
import com.sukakotlin.utils.uppercaseEachWord
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.SortOrder
import org.jetbrains.exposed.v1.core.SqlExpressionBuilder.eq
import org.jetbrains.exposed.v1.core.SqlExpressionBuilder.inList
import org.jetbrains.exposed.v1.core.alias
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.avg
import org.jetbrains.exposed.v1.core.countDistinct
import org.jetbrains.exposed.v1.core.innerJoin
import org.jetbrains.exposed.v1.jdbc.andWhere
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.insertAndGetId
import org.jetbrains.exposed.v1.jdbc.select
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.jetbrains.exposed.v1.jdbc.update

class RecipeRepository {
//    private val logger = LoggerFactory.getLogger(this::class.java)

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

    private fun ResultRow.toCommentWithImage(): CommentWithImage {
        val commentId = this[CommentsTable.id].value

        val image = (CommentsImagesTable innerJoin ImagesTable)
            .selectAll()
            .where { CommentsImagesTable.commentId eq commentId }
            .singleOrNull()
            ?.toImage()

        return CommentWithImage(
            id = commentId,
            recipeId = this[CommentsTable.recipeId],
            authorId = this[CommentsTable.userId],
            content = this[CommentsTable.content],
            createdAt = this[CommentsTable.createdAt],
            image = image
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
            .update({ RecipesTable.id eq id }) {
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

    fun findRecipeDetail(id: Long, userId: String): RecipeDetail? = transaction {
        val recipe = RecipesTable
            .selectAll()
            .where { RecipesTable.id eq id }
            .singleOrNull()
            ?.toRecipe() ?: return@transaction null

        val images = getRecipeImages(id)
        val ingredients = getRecipeIngredients(id)
        val steps = getRecipeSteps(id)
        val comments = getCommentWithImage(id)
        val rating = getRecipeRating(id)

        val isFavorited = if (userId == recipe.authorId) null else isFavorited(id, userId)
        val favoriteCount = getFavorite(id)

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
            steps = steps,
            comments = comments,
            rating = rating,
            isFavorited = isFavorited,
            favoriteCount = favoriteCount
        )
    }

    fun findAllDetailByAuthorId(authorId: String) = transaction {
        RecipesTable
            .select(RecipesTable.id)
            .where { RecipesTable.authorId eq authorId }
            .map { it[RecipesTable.id].value }
            .mapNotNull { findRecipeDetail(it, authorId) }
    }

    fun searchFiltered(
        query: String,
        userId: String,
        minRating: Double? = null,
        minEstTime: Int? = null,
        maxEstTime: Int? = null
    ): List<RecipeDetail> = transaction {
        // Base query dengan filter text dan status
        var baseQuery = RecipesTable
            .selectAll()
            .where {
                (RecipesTable.name ilikeContains query) and
                        (RecipesTable.status eq RecipeStatus.PUBLISHED) and
                        (RecipesTable.isPublic eq true)
            }

        // Filter berdasarkan estimated time
        if (minEstTime != null) {
            baseQuery = baseQuery.andWhere { RecipesTable.estTimeInMinutes greaterEq minEstTime }
        }

        if (maxEstTime != null) {
            baseQuery = baseQuery.andWhere { RecipesTable.estTimeInMinutes lessEq maxEstTime }
        }

        // Jika ada minRating filter, ambil recipe IDs yang match
        var recipeIds: List<Long>? = null
        if (minRating != null) {
            // Query ratings dan grouping di memory
            recipeIds = RatingsTable
                .selectAll()
                .map { it[RatingsTable.recipeId] }
                .groupingBy { it }
                .eachCount()
                .mapNotNull { (recipeId, _) ->
                    val ratings = RatingsTable
                        .selectAll()
                        .where { RatingsTable.recipeId eq recipeId }
                        .map { it[RatingsTable.value] }

                    val average = if (ratings.isNotEmpty()) ratings.average() else 0.0
                    if (average >= minRating) recipeId else null
                }

            // Filter base query dengan recipe IDs
            if (recipeIds.isNotEmpty()) {
                baseQuery = baseQuery.andWhere { RecipesTable.id inList recipeIds }
            } else {
                return@transaction emptyList()
            }
        }

        baseQuery
            .orderBy(RecipesTable.createdAt to SortOrder.DESC)
            .mapNotNull { recipeRow ->
                val recipe = recipeRow.toRecipe()

                val images = getRecipeImages(recipe.id)
                val ingredients = getRecipeIngredients(recipe.id)
                val steps = getRecipeSteps(recipe.id)
                val comments = getCommentWithImage(recipe.id)
                val rating = getRecipeRating(recipe.id)
                val isFavorited = isFavorited(recipe.id, userId)
                val favoriteCount = getFavorite(recipe.id)

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
                    steps = steps,
                    comments = comments,
                    rating = rating,
                    isFavorited = isFavorited,
                    favoriteCount = favoriteCount
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

    // Comments
    fun addComment(recipeId: Long, userId: String, content: String, imageUrl: String?): CommentWithImage = transaction {
        val now = System.currentTimeMillis()

        val commentId = CommentsTable.insertAndGetId {
            it[CommentsTable.recipeId] = recipeId
            it[CommentsTable.userId] = userId
            it[CommentsTable.content] = content
            it[CommentsTable.createdAt] = now
        }.value

        var image: Image? = null
        if (imageUrl != null) {
            val imageId = ImagesTable.insertAndGetId {
                it[ImagesTable.url] = imageUrl
            }.value

            CommentsImagesTable.insertAndGetId {
                it[CommentsImagesTable.commentId] = commentId
                it[CommentsImagesTable.imageId] = imageId
            }

            image = Image(id = imageId, url = imageUrl)
        }

        CommentWithImage(
            id = commentId,
            recipeId = recipeId,
            authorId = userId,
            content = content,
            createdAt = now,
            image = image
        )
    }

    fun deleteComment(commentId: Long): Boolean = transaction {
        CommentsTable.deleteWhere { CommentsTable.id eq commentId } > 0
    }

    fun getCommentWithImage(recipeId: Long): List<CommentWithImage> = transaction {
        CommentsTable
            .selectAll()
            .where { CommentsTable.recipeId eq recipeId }
            .map { it.toCommentWithImage() }
    }

    // Rating
    fun addOrUpdateRating(recipeId: Long, userId: String, value: Double): Unit = transaction {
        RatingsTable.deleteWhere {
            (RatingsTable.recipeId eq recipeId) and (RatingsTable.userId eq userId)
        }

        RatingsTable.insert {
            it[RatingsTable.recipeId] = recipeId
            it[RatingsTable.userId] = userId
            it[RatingsTable.value] = value
        }
    }

    fun deleteRating(recipeId: Long, userId: String): Unit = transaction {
        RatingsTable.deleteWhere {
            (RatingsTable.recipeId eq recipeId) and (RatingsTable.userId eq userId)
        }
    }

    private fun getRecipeRating(recipeId: Long): RecipeRating = transaction {
        val ratings = RatingsTable
            .selectAll()
            .where { RatingsTable.recipeId eq recipeId }
            .map { it[RatingsTable.value] }

        val average = if (ratings.isNotEmpty()) ratings.average() else 0.0
        val count = ratings.size

        RecipeRating(
            average = average,
            count = count
        )
    }

    fun isFavorited(recipeId: Long, userId: String): Boolean = transaction {
        BookmarksTable
            .select(BookmarksTable.id)
            .where { BookmarksTable.recipeId eq recipeId }
            .andWhere { BookmarksTable.userId eq userId }
            .count() > 0
    }

    fun getFavorite(recipeId: Long) = transaction {
        BookmarksTable
            .select(BookmarksTable.id)
            .where { BookmarksTable.recipeId eq recipeId }
            .count()
    }

    fun addToBookmark(recipeId: Long, userId: String): Boolean = transaction {
        BookmarksTable.insertAndGetId {
            it[BookmarksTable.userId] = userId
            it[BookmarksTable.recipeId] = recipeId
        }.value > 0
    }

    fun deleteFromBookmark(recipeId: Long, userId: String): Unit = transaction {
        BookmarksTable.deleteWhere { (BookmarksTable.userId eq userId) and (BookmarksTable.recipeId eq recipeId) }
    }

    fun getUserFavorites(userId: String) = transaction {
        val favIds = BookmarksTable
            .select(BookmarksTable.recipeId)
            .where { BookmarksTable.userId eq userId }
            .orderBy(BookmarksTable.id to SortOrder.DESC)
            .map { it[BookmarksTable.recipeId] }

        val details = favIds.mapNotNull { recipeId ->
            findRecipeDetail(recipeId, userId)
        }

        val validIds = details.map { it.recipe.id }
        val invalidIds = favIds - validIds.toSet()
        if (invalidIds.isNotEmpty()) {
            BookmarksTable.deleteWhere {
                (BookmarksTable.userId eq userId) and (BookmarksTable.recipeId inList invalidIds)
            }
        }

        details
    }

    fun fridgeFilter(
        userId: String,
        includedIngredientTags: List<Long>,
        excludedIngredientTags: List<Long>,
        minRating: Double? = null,
        minEstTime: Int? = null,
        maxEstTime: Int? = null
    ): List<RecipeDetail> = transaction {

        // ===== BASE QUERY =====
        var baseQuery = RecipesTable
            .select(RecipesTable.id)
            .where {
                (RecipesTable.status eq RecipeStatus.PUBLISHED) and
                        (RecipesTable.isPublic eq true)
            }

        // ===== EST TIME FILTER =====
        if (minEstTime != null) {
            baseQuery = baseQuery.andWhere {
                RecipesTable.estTimeInMinutes greaterEq minEstTime
            }
        }

        if (maxEstTime != null) {
            baseQuery = baseQuery.andWhere {
                RecipesTable.estTimeInMinutes lessEq maxEstTime
            }
        }

        // ===== INCLUDED TAGS (HARUS ADA SEMUA) =====
        if (includedIngredientTags.isNotEmpty()) {
            val tagCount = IngredientsTable.tagId.countDistinct().alias("tag_count")

            val includedRecipeIds = IngredientsTable
                .selectAll()
                .where {
                    IngredientsTable.tagId inList includedIngredientTags
                }
                .groupBy(IngredientsTable.recipeId)
                .mapNotNull {
                    val count = it[tagCount]
                    val recipeId = it[IngredientsTable.recipeId]

                    if (count == includedIngredientTags.size.toLong()) recipeId else null
                }

            if (includedRecipeIds.isEmpty()) return@transaction emptyList()

            baseQuery = baseQuery.andWhere {
                RecipesTable.id inList includedRecipeIds
            }
        }

        // ===== EXCLUDED TAGS (TIDAK BOLEH ADA) =====
        if (excludedIngredientTags.isNotEmpty()) {
            val excludedRecipeIds = IngredientsTable
                .selectAll()
                .where {
                    IngredientsTable.tagId inList excludedIngredientTags
                }
                .map { it[IngredientsTable.recipeId] }
                .distinct()

            if (excludedRecipeIds.isNotEmpty()) {
                baseQuery = baseQuery.andWhere {
                    RecipesTable.id notInList excludedRecipeIds
                }
            }
        }

        // ===== MIN RATING =====
        if (minRating != null) {
            val avgRating = RatingsTable.value.avg().alias("avg_rating")
            val minRatingBd = minRating.toBigDecimal()

            val ratedRecipeIds = RatingsTable
                .selectAll()
                .groupBy(RatingsTable.recipeId)
                .mapNotNull { row ->
                    val avg = row[avgRating]          // BigDecimal?
                    val recipeId = row[RatingsTable.recipeId]

                    if (avg != null && avg >= minRatingBd) recipeId else null
                }

            if (ratedRecipeIds.isEmpty()) return@transaction emptyList()

            baseQuery = baseQuery.andWhere {
                RecipesTable.id inList ratedRecipeIds
            }
        }

        // ===== FINAL RESULT =====
        baseQuery
            .orderBy(RecipesTable.createdAt to SortOrder.DESC)
            .map { it[RecipesTable.id].value }
            .mapNotNull { recipeId ->
                findRecipeDetail(recipeId, userId)
            }
    }
}