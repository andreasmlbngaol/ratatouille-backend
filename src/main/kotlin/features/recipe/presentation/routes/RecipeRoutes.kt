package com.sukakotlin.features.recipe.presentation.routes

import com.sukakotlin.features.recipe.domain.use_case.GetOrCreateDraftRecipeUseCase
import com.sukakotlin.features.recipe.domain.use_case.GetRecipeDetailUseCase
import com.sukakotlin.features.recipe.domain.use_case.base.UpdateRecipeUseCase
import com.sukakotlin.features.recipe.domain.use_case.base.UploadRecipeImageUseCase
import com.sukakotlin.features.recipe.domain.use_case.ingredients.CreateIngredientTagUseCase
import com.sukakotlin.features.recipe.domain.use_case.ingredients.GetIngredientTagUseCase
import com.sukakotlin.features.recipe.domain.use_case.ingredients.AddIngredientUseCase
import com.sukakotlin.features.recipe.domain.use_case.steps.CreateEmptyStepUseCase
import com.sukakotlin.features.recipe.domain.use_case.steps.UpdateStepUseCase
import com.sukakotlin.features.recipe.domain.use_case.steps.UploadStepImageUseCase
import com.sukakotlin.features.recipe.presentation.dto.request.CreateStepRequest
import com.sukakotlin.features.recipe.presentation.dto.request.IngredientTagRequest
import com.sukakotlin.features.recipe.presentation.dto.request.AddIngredientRequest
import com.sukakotlin.features.recipe.presentation.dto.request.UpdateRecipeRequest
import com.sukakotlin.features.recipe.presentation.dto.request.UpdateStepRequest
import com.sukakotlin.features.recipe.presentation.dto.response.toResponse
import com.sukakotlin.features.user.presentation.routes.extractImageFromMultipart
import com.sukakotlin.presentation.util.failureResponse
import com.sukakotlin.presentation.util.respondFailure
import com.sukakotlin.presentation.util.userId
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.auth.authenticate
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.patch
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import org.koin.ktor.ext.inject

fun Route.recipeRoutes() {
    val getOrCreateDraftRecipe: GetOrCreateDraftRecipeUseCase by inject()
    val uploadRecipeImage: UploadRecipeImageUseCase by inject()
    val updateRecipeBaseInfo: UpdateRecipeUseCase by inject()
    val getIngredientTag: GetIngredientTagUseCase by inject()
    val createIngredientTag: CreateIngredientTagUseCase by inject()
    val addIngredient: AddIngredientUseCase by inject()
    val createEmptyStep: CreateEmptyStepUseCase by inject()
    val uploadStepImage: UploadStepImageUseCase by inject()
    val updateStep: UpdateStepUseCase by inject()
    val getRecipeDetail: GetRecipeDetailUseCase by inject()

    authenticate("firebase-auth") {
        route("/recipes") {
            get("/drafts") {
                val userId = call.userId!!
                val result = getOrCreateDraftRecipe(userId)
                result.fold(
                    onSuccess = { call.respond(it.toResponse()) },
                    onFailure = { call.respondFailure(it) }
                )
            }

            route("/{recipeId}") {
                get {
                    val userId = call.userId!!
                    val recipeId = call.recipeId
                        ?: return@get call.respond(
                            HttpStatusCode.BadRequest,
                            failureResponse("Invalid Recipe ID")
                        )

                    val result = getRecipeDetail(userId, recipeId)

                    result.fold(
                        onSuccess = { call.respond(it.toResponse("Recipe detail")) },
                        onFailure = { call.respondFailure(it) }
                    )
                }
                patch {
                    val userId = call.userId!!
                    val recipeId = call.recipeId
                        ?: return@patch call.respond(
                            HttpStatusCode.BadRequest,
                            failureResponse("Invalid Recipe ID")
                        )
                    val payload = call.receive<UpdateRecipeRequest>()

                    val result = updateRecipeBaseInfo(
                        userId = userId,
                        recipeId = recipeId,
                        name = payload.name,
                        description = payload.description,
                        isPublic = payload.isPublic,
                        estTimeInMinutes = payload.estTimeInMinutes,
                        portion = payload.portion,
                        status = payload.status
                    )

                    result.fold(
                        onSuccess = { call.respond(it.toResponse()) },
                        onFailure = { call.respondFailure(it) }
                    )
                }
                post("/pictures") {
                    val userId = call.userId!!
                    val recipeId = call.parameters["recipeId"]
                        ?.toLongOrNull()
                        ?: return@post call.respond(
                            HttpStatusCode.BadRequest,
                            failureResponse("Invalid Recipe ID")
                        )

                    val imageData = extractImageFromMultipart(call)
                        ?: return@post call.respond(
                            HttpStatusCode.BadRequest,
                            failureResponse("Missing image")
                        )

                    val result = uploadRecipeImage(userId, recipeId, imageData)

                    result.fold(
                        onSuccess = { call.respond(it.toResponse()) },
                        onFailure = { call.respondFailure(it) }
                    )
                }

                route("/ingredients") {
                    post {
                        val userId = call.userId!!
                        val recipeId = call.recipeId
                            ?: return@post call.respond(
                                HttpStatusCode.BadRequest,
                                failureResponse("Invalid Recipe ID")
                            )
                        val payload = call.receive<AddIngredientRequest>()

                        val result = addIngredient(
                            userId,
                            recipeId,
                            payload.tagId,
                            payload.amount,
                            payload.unit,
                            payload.alternative
                        )

                        result.fold(
                            onSuccess = { call.respond(it.toResponse()) },
                            onFailure = { call.respondFailure(it) }
                        )
                    }
                    get {
                        val userId = call.userId!!
                        val recipeId = call.recipeId
                            ?: return@get call.respond(
                                HttpStatusCode.BadRequest,
                                failureResponse("Invalid Recipe ID")
                            )
                        val result = getRecipeDetail(userId, recipeId)
                        result.fold(
                            onSuccess = { call.respond(it.ingredients.toResponse()) },
                            onFailure = { call.respondFailure(it) }
                        )
                    }
                }

                route("/steps") {
                    post {
                        val userId = call.userId!!
                        val recipeId = call.recipeId
                            ?: return@post call.respond(
                                HttpStatusCode.BadRequest,
                                failureResponse("Invalid Recipe ID")
                            )
                        val payload = call.receive<CreateStepRequest>()
                        val result = createEmptyStep(userId, recipeId, payload.stepNumber)

                        result.fold(
                            onSuccess = { call.respond(it.toResponse()) },
                            onFailure = { call.respondFailure(it) }
                        )
                    }

                    route("/{stepId}") {
                        patch {
                            val userId = call.userId!!
                            val recipeId = call.recipeId
                                ?: return@patch call.respond(
                                    HttpStatusCode.BadRequest,
                                    failureResponse("Invalid Recipe ID")
                                )
                            val stepId = call.parameters["stepId"]
                                ?.toLongOrNull()
                                ?: return@patch call.respond(
                                    HttpStatusCode.BadRequest,
                                    failureResponse("Invalid stepId")
                                )
                            val payload = call.receive<UpdateStepRequest>()
                            val result = updateStep(
                                userId,
                                recipeId,
                                stepId,
                                payload.content
                            )

                            result.fold(
                                onSuccess = { call.respond(it.toResponse()) },
                                onFailure = { call.respondFailure(it) }
                            )
                        }
                        post("/pictures") {
                            val userId = call.userId!!
                            val recipeId = call.recipeId
                                ?: return@post call.respond(
                                    HttpStatusCode.BadRequest,
                                    failureResponse("Invalid Recipe ID")
                                )
                            val stepId = call.parameters["stepId"]
                                ?.toLongOrNull()
                                ?: return@post call.respond(
                                    HttpStatusCode.BadRequest,
                                    failureResponse("Invalid stepId")
                                )

                            val imageData = extractImageFromMultipart(call)
                                ?: return@post call.respond(
                                    HttpStatusCode.BadRequest,
                                    failureResponse("Missing image")
                                )

                            val result = uploadStepImage(userId, recipeId, stepId, imageData)

                            result.fold(
                                onSuccess = { call.respond(it.toResponse()) },
                                onFailure = { call.respondFailure(it) }
                            )
                        }
                    }
                }
            }

            route("/ingredient-tags") {
                get {
                    val query = call.request.queryParameters["query"]
                        ?: return@get call.respond(
                            HttpStatusCode.BadRequest,
                            failureResponse("Missing query parameter")
                        )
                    val result = getIngredientTag(query)

                    result.fold(
                        onSuccess = { call.respond(it.toResponse()) },
                        onFailure = { call.respondFailure(it) }
                    )
                }
                post {
                    val payload = call.receive<IngredientTagRequest>()
                    val result = createIngredientTag(payload.name)

                    result.fold(
                        onSuccess = { call.respond(it.toResponse()) },
                        onFailure = { call.respondFailure(it) }
                    )
                }
            }
        }
    }
}

private val ApplicationCall.recipeId
    get() = this.parameters["recipeId"]
        ?.toLongOrNull()