package com.sukakotlin.features.recipe.data.repository

import com.sukakotlin.data.repository.BaseRepositoryImpl
import com.sukakotlin.features.recipe.data.entity.RecipesEntity
import com.sukakotlin.features.recipe.data.table.RecipesTable
import com.sukakotlin.features.recipe.domain.model.Image
import com.sukakotlin.features.recipe.domain.model.IngredientWithTag
import com.sukakotlin.features.recipe.domain.model.Recipe
import com.sukakotlin.features.recipe.domain.model.RecipeDetail
import com.sukakotlin.features.recipe.domain.model.RecipeStatus
import com.sukakotlin.features.recipe.domain.model.StepWithImages
import com.sukakotlin.features.recipe.domain.repository.RecipesRepository
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

object RecipesRepositoryImpl:
    BaseRepositoryImpl<Long, RecipesEntity, Recipe>(RecipesEntity.Companion),
    RecipesRepository {
    private fun RecipesEntity.toRecipe() = Recipe(
        id = this.id.value,
        authorId = this.authorId,
        name = this.name,
        description = this.description,
        isPublic = this.isPublic,
        estTimeInMinutes = this.estTimeInMinutes,
        portion = this.portion,
        status = this.status,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt,
    )

    override fun RecipesEntity.toDomain(): Recipe = this.toRecipe()

    override suspend fun save(recipe: Recipe): Recipe = saveEntity {
        name = recipe.name
        description = recipe.description
        isPublic = recipe.isPublic
        estTimeInMinutes = recipe.estTimeInMinutes
        portion = recipe.portion
        status = recipe.status
        authorId = recipe.authorId
    }

    override suspend fun update(
        id: Long,
        recipe: Recipe
    ): Recipe? = updateEntity(id) {
        name = recipe.name
        description = recipe.description
        isPublic = recipe.isPublic
        estTimeInMinutes = recipe.estTimeInMinutes
        portion = recipe.portion
        status = recipe.status
        authorId = recipe.authorId
    }

    override suspend fun findDraftByAuthorId(authorId: String): Recipe? = transaction {
        RecipesEntity.find {
            (RecipesTable.authorId eq authorId) and
                    (RecipesTable.isPublic eq false) and
                    (RecipesTable.status eq RecipeStatus.DRAFT)
        }
            .singleOrNull()
            ?.toDomain()
    }

    override suspend fun findByIdAndAuthorId(
        id: Long,
        authorId: String
    ): Recipe? = transaction {
        RecipesEntity.find {
            (RecipesTable.id eq id) and (RecipesTable.authorId eq authorId)
        }
            .singleOrNull()
            ?.toDomain()
    }

    override suspend fun addRecipeImage(
        userId: String,
        recipeId: Long,
        image: Image
    ): List<Image> {
        TODO("Not yet implemented")
    }

    override suspend fun addIngredient(
        recipeId: Long,
        tagId: Long,
        amount: Double?,
        unit: String?,
        alternative: String?
    ): List<IngredientWithTag> {
        TODO("Not yet implemented")
    }

    override suspend fun addStep(
        recipeId: Long,
        stepNumber: Int,
        content: String
    ): List<StepWithImages> {
        TODO("Not yet implemented")
    }

    override suspend fun updateStep(
        id: Long,
        content: String
    ): List<StepWithImages> {
        TODO("Not yet implemented")
    }

    override suspend fun addStepImage(
        userId: String,
        recipeId: Long,
        stepId: Long,
        image: Image
    ): List<Image> {
        TODO("Not yet implemented")
    }

    override suspend fun findRecipeDetail(id: Long): RecipeDetail? {
        TODO("Not yet implemented")
    }
}