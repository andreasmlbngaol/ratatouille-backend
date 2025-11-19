package com.sukakotlin.features.recipe.domain.repository

import com.sukakotlin.features.recipe.domain.model.*

interface RecipesRepository {
    suspend fun save(recipe: Recipe): Recipe
    suspend fun update(id: Long, recipe: Recipe): Recipe?

    // Draft operations
    suspend fun findDraftByAuthorId(authorId: String): Recipe?
    suspend fun findByIdAndAuthorId(id: Long, authorId: String): Recipe?

    suspend fun addRecipeImage(recipeId: Long, image: Image): List<Image>

    suspend fun addIngredient(
        recipeId: Long,
        tagId: Long,
        amount: Double?,
        unit: String?,
        alternative: String?
    ): List<IngredientWithTag>

    // Steps
    suspend fun addStep(
        recipeId: Long,
        stepNumber: Int,
        content: String
    ): List<StepWithImages>
    suspend fun updateStep(
        recipeId: Long,
        id: Long,
        content: String,
    ): List<StepWithImages>

    // Step images
    suspend fun addStepImage(userId: String, recipeId: Long, stepId: Long, image: Image): List<Image>

    // Ratings
//    suspend fun saveRating(rating: Rating): Rating
//    suspend fun findRatingByRecipeIdAndUserId(recipeId: Long, userId: String): Rating?
//    suspend fun findRatingsByRecipeId(recipeId: Long): List<Rating>
//    suspend fun deleteRating(recipeId: Long, userId: String): Boolean
//
//    // Comments
//    suspend fun saveComment(comment: Comment): Comment
//    suspend fun updateComment(id: Long, comment: Comment): Comment?
//    suspend fun findCommentsByRecipeId(recipeId: Long): List<CommentWithUser>
//    suspend fun findCommentsByRecipeIdPaged(recipeId: Long, limit: Int, offset: Int): List<CommentWithUser>
//    suspend fun findCommentByIdAndUserId(id: Long, userId: String): Comment?
//    suspend fun deleteComment(id: Long, userId: String): Boolean
//    suspend fun findCommentImages(commentId: Long): List<Image>
//    suspend fun addCommentImage(commentId: Long, image: Image): Image
//    suspend fun deleteCommentImage(commentId: Long, imageId: Long, userId: String): Boolean
//
//    // Bookmarks
//    suspend fun saveBookmark(bookmark: Bookmark): Bookmark
//    suspend fun findBookmarkByUserIdAndRecipeId(userId: String, recipeId: Long): Bookmark?
//    suspend fun findBookmarksByUserId(userId: String): List<Bookmark>
//    suspend fun findBookmarksByUserIdPaged(userId: String, limit: Int, offset: Int): List<Bookmark>
//    suspend fun existsBookmarkByUserIdAndRecipeId(userId: String, recipeId: Long): Boolean
//    suspend fun deleteBookmark(userId: String, recipeId: Long): Boolean

    // Detail queries
    suspend fun findRecipeDetail(id: Long): RecipeDetail?
}
