package com.sukakotlin.routes

import com.sukakotlin.dto.AddIngredientRequest
import com.sukakotlin.dto.CreateStepRequest
import com.sukakotlin.dto.IngredientTagRequest
import com.sukakotlin.dto.UpdateRecipeRequest
import com.sukakotlin.dto.UpdateRecipeStatusRequest
import com.sukakotlin.dto.UpdateStepRequest
import com.sukakotlin.service.RecipeService
import com.sukakotlin.utils.extractImageFromMultipart
import com.sukakotlin.utils.respondFailure
import com.sukakotlin.utils.userId
import io.ktor.http.HttpStatusCode
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
    val recipeService by inject<RecipeService>()

    authenticate("firebase-auth") {
        route("/recipes") {
            // GET /recipes?query=...&minEstTime=...&maxEstTime=...
            get {
                val userId = call.userId!!
                val query = call.request.queryParameters["query"]
                    ?.takeIf { it.length >= 3 }
                    ?: return@get call.respond(
                        HttpStatusCode.BadRequest,
                        "Query parameter 'query' must be at least 3 characters long."
                    )

                val minEstTime = call.request.queryParameters["minEstTime"]?.toIntOrNull()
                val maxEstTime = call.request.queryParameters["maxEstTime"]?.toIntOrNull()

                // Validation
                if (minEstTime != null && minEstTime < 0) {
                    return@get call.respond(
                        HttpStatusCode.BadRequest,
                        "Estimated time must be non-negative"
                    )
                }

                if (maxEstTime != null && maxEstTime < 0) {
                    return@get call.respond(
                        HttpStatusCode.BadRequest,
                        "Estimated time must be non-negative"
                    )
                }

                if (minEstTime != null && maxEstTime != null && maxEstTime < minEstTime) {
                    return@get call.respond(
                        HttpStatusCode.BadRequest,
                        "Max estimated time must be greater than or equal to min estimated time"
                    )
                }

                recipeService.searchRecipes(query, userId, minEstTime, maxEstTime).fold(
                    onSuccess = { call.respond(it) },
                    onFailure = { call.respondFailure(it) }
                )
            }

            get("/drafts") {
                val userId = call.userId!!
                recipeService.getOrCreateDraft(userId).fold(
                    onSuccess = { call.respond(it) },
                    onFailure = { call.respondFailure(it) }
                )
            }

            get("/ingredient-tags") {
                val query = call.request.queryParameters["query"]
                    ?: return@get call.respond(
                        HttpStatusCode.BadRequest,
                        "Missing 'query' parameter"
                    )

                recipeService.searchIngredientTags(query).fold(
                    onSuccess = { call.respond(it) },
                    onFailure = { call.respondFailure(it) }
                )
            }

            post("/ingredient-tags") {
                val payload = call.receive<IngredientTagRequest>()

                recipeService.getOrCreateIngredientTag(payload.name.trim()).fold(
                    onSuccess = { call.respond(it) },
                    onFailure = { call.respondFailure(it) }
                )
            }

            route("/{recipeId}") {
                post("/status") {
                    val userId = call.userId!!
                    val recipeId = call.parameters["recipeId"]?.toLongOrNull()
                        ?: return@post call.respond(
                            HttpStatusCode.BadRequest,
                            "Invalid Recipe ID"
                        )

                    println("Test")

                    val payload = call.receive<UpdateRecipeStatusRequest>()

                    println("Test 2")

                    recipeService.updateRecipeStatus(
                        userId = userId,
                        recipeId = recipeId,
                        status = payload.status
                    ).fold(
                        onSuccess = { call.respond(it) },
                        onFailure = { call.respondFailure(it) }
                    )
                }
                // GET /recipes/{recipeId}
                get {
                    val userId = call.userId!!
                    val recipeId = call.parameters["recipeId"]?.toLongOrNull()
                        ?: return@get call.respond(
                            HttpStatusCode.BadRequest,
                            "Invalid Recipe ID"
                        )

                    recipeService.getRecipeDetail(recipeId, userId).fold(
                        onSuccess = { call.respond(it) },
                        onFailure = { call.respondFailure(it) }
                    )
                }

                patch {
                    val userId = call.userId!!
                    val recipeId = call.parameters["recipeId"]?.toLongOrNull()
                        ?: return@patch call.respond(
                            HttpStatusCode.BadRequest,
                            "Invalid Recipe ID"
                        )

                    val payload = call.receive<UpdateRecipeRequest>()

                    recipeService.updateRecipe(
                        userId = userId,
                        recipeId = recipeId,
                        name = payload.name,
                        description = payload.description,
                        isPublic = payload.isPublic,
                        estTimeInMinutes = payload.estTimeInMinutes,
                        portion = payload.portion,
                        status = payload.status
                    ).fold(
                        onSuccess = { call.respond(it) },
                        onFailure = { call.respondFailure(it) }
                    )
                }

                post("/pictures") {
                    val userId = call.userId!!
                    val recipeId = call.parameters["recipeId"]?.toLongOrNull()
                        ?: return@post call.respond(
                            HttpStatusCode.BadRequest,
                            "Invalid Recipe ID"
                        )

                    val imageData = extractImageFromMultipart(call)
                        ?: return@post call.respond(
                            HttpStatusCode.BadRequest,
                            "Missing image"
                        )

                    recipeService.uploadRecipeImage(userId, recipeId, imageData).fold(
                        onSuccess = { call.respond(it) },
                        onFailure = { call.respondFailure(it) }
                    )
                }

                route("/ingredients") {
                    // POST /recipes/{recipeId}/ingredients
                    post {
                        val userId = call.userId!!
                        val recipeId = call.parameters["recipeId"]?.toLongOrNull()
                            ?: return@post call.respond(
                                HttpStatusCode.BadRequest,
                                "Invalid Recipe ID"
                            )

                        val payload = call.receive<AddIngredientRequest>()

                        recipeService.addIngredient(
                            userId = userId,
                            recipeId = recipeId,
                            tagId = payload.tagId,
                            amount = payload.amount,
                            unit = payload.unit,
                            alternative = payload.alternative
                        ).fold(
                            onSuccess = { call.respond(it) },
                            onFailure = { call.respondFailure(it) }
                        )
                    }

                    get {
                        val userId = call.userId!!
                        val recipeId = call.parameters["recipeId"]?.toLongOrNull()
                            ?: return@get call.respond(
                                HttpStatusCode.BadRequest,
                                "Invalid Recipe ID"
                            )

                        recipeService.getRecipeDetail(recipeId, userId).fold(
                            onSuccess = { call.respond(it.ingredients) },
                            onFailure = { call.respondFailure(it) }
                        )
                    }
                }

                route("/steps") {
                    // POST /recipes/{recipeId}/steps
                    post {
                        val userId = call.userId!!
                        val recipeId = call.parameters["recipeId"]?.toLongOrNull()
                            ?: return@post call.respond(
                                HttpStatusCode.BadRequest,
                                "Invalid Recipe ID"
                            )

                        val payload = call.receive<CreateStepRequest>()

                        recipeService.createStep(userId, recipeId, payload.stepNumber).fold(
                            onSuccess = { call.respond(it) },
                            onFailure = { call.respondFailure(it) }
                        )
                    }

                    get {
                        val userId = call.userId!!
                        val recipeId = call.parameters["recipeId"]?.toLongOrNull()
                            ?: return@get call.respond(
                                HttpStatusCode.BadRequest,
                                "Invalid Recipe ID"
                            )

                        recipeService.getRecipeDetail(recipeId, userId).fold(
                            onSuccess = { call.respond(it.steps) },
                            onFailure = { call.respondFailure(it) }
                        )
                    }

                    route("/{stepId}") {
                        // PATCH /recipes/{recipeId}/steps/{stepId}
                        patch {
                            val userId = call.userId!!
                            val recipeId = call.parameters["recipeId"]?.toLongOrNull()
                                ?: return@patch call.respond(
                                    HttpStatusCode.BadRequest,
                                    "Invalid Recipe ID"
                                )

                            val stepId = call.parameters["stepId"]?.toLongOrNull()
                                ?: return@patch call.respond(
                                    HttpStatusCode.BadRequest,
                                    "Invalid Step ID"
                                )

                            val payload = call.receive<UpdateStepRequest>()

                            recipeService.updateStep(userId, recipeId, stepId, payload.content).fold(
                                onSuccess = { call.respond(it) },
                                onFailure = { call.respondFailure(it) }
                            )
                        }

                        post("/pictures") {
                            val userId = call.userId!!
                            val recipeId = call.parameters["recipeId"]?.toLongOrNull()
                                ?: return@post call.respond(
                                    HttpStatusCode.BadRequest,
                                    "Invalid Recipe ID"
                                )

                            val stepId = call.parameters["stepId"]?.toLongOrNull()
                                ?: return@post call.respond(
                                    HttpStatusCode.BadRequest,
                                    "Invalid Step ID"
                                )

                            val imageData = extractImageFromMultipart(call)
                                ?: return@post call.respond(
                                    HttpStatusCode.BadRequest,
                                    "Missing image"
                                )

                            recipeService.uploadStepImage(userId, recipeId, stepId, imageData).fold(
                                onSuccess = { call.respond(it) },
                                onFailure = { call.respondFailure(it) }
                            )
                        }
                    }
                }
            }
        }
    }
}