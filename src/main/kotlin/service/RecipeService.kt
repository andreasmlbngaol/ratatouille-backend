package com.sukakotlin.service

import com.sukakotlin.model.Image
import com.sukakotlin.model.IngredientTag
import com.sukakotlin.model.IngredientWithTag
import com.sukakotlin.model.Recipe
import com.sukakotlin.model.RecipeDetail
import com.sukakotlin.model.RecipeStatus
import com.sukakotlin.model.RecipeWithImages
import com.sukakotlin.model.StepWithImages
import com.sukakotlin.repository.IngredientTagsRepository
import com.sukakotlin.repository.RecipeRepository
import org.slf4j.LoggerFactory

class RecipeService(
    private val recipesRepository: RecipeRepository,
    private val ingredientTagsRepository: IngredientTagsRepository,
    private val storageService: StorageService
) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    fun getOrCreateDraft(userId: String): Result<RecipeWithImages> {
        return try {
            val existingDraft = recipesRepository.findDraftByAuthorId(userId)
            if (existingDraft != null) {
                return Result.success(existingDraft)
            }

            val newRecipe = Recipe(
                id = 0,
                authorId = userId,
                name = "",
                description = null,
                isPublic = false,
                estTimeInMinutes = 1,
                portion = 1,
                status = RecipeStatus.DRAFT,
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            )

            val savedRecipe = recipesRepository.save(newRecipe)
            logger.info("Created new draft recipe for user: $userId")
            Result.success(savedRecipe)
        } catch (e: Exception) {
            logger.error("Error creating draft recipe for user: $userId", e)
            Result.failure(e)
        }
    }

    fun getRecipeDetail(recipeId: Long, currentUserId: String): Result<RecipeDetail> {
        return try {
            val recipeDetail = recipesRepository.findRecipeDetail(recipeId)
                ?: return Result.failure(Exception("Recipe not found"))

            // Check access: public atau milik user
            val recipe = recipeDetail.recipe
            if (!recipe.isPublic && recipe.authorId != currentUserId) {
                return Result.failure(Exception("You don't have access to this recipe"))
            }

            Result.success(recipeDetail)
        } catch (e: Exception) {
            logger.error("Error getting recipe detail for recipe: $recipeId", e)
            Result.failure(e)
        }
    }

    fun updateRecipeStatus(
        userId: String,
        recipeId: Long,
        status: RecipeStatus
    ): Result<RecipeWithImages> = try {
        val recipe = recipesRepository.findByIdAndAuthorId(recipeId, userId)
            ?: return Result.failure(Exception("Recipe not found or you don't own it"))
        val updatedRecipe = recipe.copy(status = status)
        val result = recipesRepository.update(recipeId, updatedRecipe) ?: return Result.failure(Exception("Failed to update recipe"))
        Result.success(result)
    } catch (e: Exception) {
        logger.error("Error publishing recipe: $recipeId for user: $userId", e)
        Result.failure(e)
    }

    fun updateRecipe(
        userId: String,
        recipeId: Long,
        name: String,
        description: String?,
        isPublic: Boolean,
        estTimeInMinutes: Int,
        portion: Int,
        status: RecipeStatus
    ): Result<RecipeWithImages> {
        return try {
            // Verify ownership
            recipesRepository.findByIdAndAuthorId(recipeId, userId)
                ?: return Result.failure(Exception("Recipe not found or you don't own it"))

            val updatedRecipe = Recipe(
                id = recipeId,
                authorId = userId,
                name = name,
                description = description,
                isPublic = isPublic,
                estTimeInMinutes = estTimeInMinutes,
                portion = portion,
                status = status,
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            )

            val result = recipesRepository.update(recipeId, updatedRecipe)
                ?: return Result.failure(Exception("Failed to update recipe"))

            logger.info("Updated recipe: $recipeId by user: $userId")
            Result.success(result)
        } catch (e: Exception) {
            logger.error("Error updating recipe: $recipeId for user: $userId", e)
            Result.failure(e)
        }
    }

    fun searchRecipes(
        query: String,
        userId: String,
        minEstTimeInMinutes: Int? = null,
        maxEstTimeInMinutes: Int? = null
    ): Result<List<RecipeDetail>> {
        return try {
            val recipes = recipesRepository.searchFiltered(
                query = query,
                userId = userId,
                minEstTimeInMinutes = minEstTimeInMinutes,
                maxEstTimeInMinutes = maxEstTimeInMinutes
            )
            Result.success(recipes)
        } catch (e: Exception) {
            logger.error("Error searching recipes with query: $query", e)
            Result.failure(e)
        }
    }

    suspend fun uploadRecipeImage(userId: String, recipeId: Long, imageData: com.sukakotlin.model.ImageData): Result<RecipeWithImages> {
        return try {
            // Verify ownership
            recipesRepository.findByIdAndAuthorId(recipeId, userId)
                ?: return Result.failure(Exception("Recipe not found or you don't own it"))

            // Upload image
            val imageUrl = storageService.uploadRecipeImage(userId, recipeId, imageData)

            // Save to database
            val image = Image(
                id = 0,
                url = imageUrl
            )

            val result = recipesRepository.addRecipeImage(recipeId, image)
            logger.info("Uploaded recipe image for recipe: $recipeId")
            Result.success(result)
        } catch (e: Exception) {
            logger.error("Error uploading recipe image for recipe: $recipeId", e)
            Result.failure(e)
        }
    }

    suspend fun uploadStepImage(
        userId: String,
        recipeId: Long,
        stepId: Long,
        imageData: com.sukakotlin.model.ImageData
    ): Result<List<StepWithImages>> {
        return try {
            // Verify ownership
            recipesRepository.findByIdAndAuthorId(recipeId, userId)
                ?: return Result.failure(Exception("Recipe not found or you don't own it"))

            // Upload image
            val imageUrl = storageService.uploadStepImage(userId, recipeId, stepId, imageData)

            // Save to database
            val image = Image(
                id = 0,
                url = imageUrl,
            )

            val result = recipesRepository.addStepImage(recipeId, stepId, image)
            logger.info("Uploaded step image for recipe: $recipeId, step: $stepId")
            Result.success(result)
        } catch (e: Exception) {
            logger.error("Error uploading step image for recipe: $recipeId, step: $stepId", e)
            Result.failure(e)
        }
    }

    fun getOrCreateIngredientTag(name: String): Result<IngredientTag> {
        return try {
            // Search if exists
            val existing = ingredientTagsRepository.searchByName(name).firstOrNull()
            if (existing != null) {
                return Result.success(existing)
            }

            // Create new
            val newTag = IngredientTag(
                id = 0,
                name = name
            )

            val saved = ingredientTagsRepository.save(newTag)
            logger.info("Created new ingredient tag: $name")
            Result.success(saved)
        } catch (e: Exception) {
            logger.error("Error creating ingredient tag: $name", e)
            Result.failure(e)
        }
    }

    fun searchIngredientTags(query: String): Result<List<IngredientTag>> {
        return try {
            val tags = ingredientTagsRepository.searchByName(query)
            Result.success(tags)
        } catch (e: Exception) {
            logger.error("Error searching ingredient tags with query: $query", e)
            Result.failure(e)
        }
    }

    fun addIngredient(
        userId: String,
        recipeId: Long,
        tagId: Long,
        amount: Double?,
        unit: String?,
        alternative: String?
    ): Result<List<IngredientWithTag>> {
        return try {
            // Verify ownership
            recipesRepository.findByIdAndAuthorId(recipeId, userId)
                ?: return Result.failure(Exception("Recipe not found or you don't own it"))

            // Verify tag exists
            ingredientTagsRepository.existsById(tagId)
                .takeIf { it }
                ?: return Result.failure(Exception("Ingredient tag not found"))

            val result = recipesRepository.addIngredient(
                recipeId = recipeId,
                tagId = tagId,
                amount = amount,
                unit = unit,
                alternative = alternative
            )

            logger.info("Added ingredient to recipe: $recipeId, tag: $tagId")
            Result.success(result)
        } catch (e: Exception) {
            logger.error("Error adding ingredient to recipe: $recipeId", e)
            Result.failure(e)
        }
    }

    fun createStep(userId: String, recipeId: Long, stepNumber: Int): Result<List<StepWithImages>> {
        return try {
            // Verify ownership
            recipesRepository.findByIdAndAuthorId(recipeId, userId)
                ?: return Result.failure(Exception("Recipe not found or you don't own it"))

            val result = recipesRepository.addStep(
                recipeId = recipeId,
                stepNumber = stepNumber,
                content = ""
            )

            logger.info("Created new step for recipe: $recipeId, step number: $stepNumber")
            Result.success(result)
        } catch (e: Exception) {
            logger.error("Error creating step for recipe: $recipeId", e)
            Result.failure(e)
        }
    }

    fun updateStep(
        userId: String,
        recipeId: Long,
        stepId: Long,
        content: String
    ): Result<List<StepWithImages>> {
        return try {
            // Verify ownership
            recipesRepository.findByIdAndAuthorId(recipeId, userId)
                ?: return Result.failure(Exception("Recipe not found or you don't own it"))

            val result = recipesRepository.updateStep(
                recipeId = recipeId,
                stepId = stepId,
                content = content
            )

            logger.info("Updated step: $stepId for recipe: $recipeId")
            Result.success(result)
        } catch (e: Exception) {
            logger.error("Error updating step: $stepId for recipe: $recipeId", e)
            Result.failure(e)
        }
    }
}