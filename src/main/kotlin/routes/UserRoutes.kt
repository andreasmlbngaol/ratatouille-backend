package com.sukakotlin.routes

import com.sukakotlin.dto.UpdateProfileRequest
import com.sukakotlin.service.UserService
import com.sukakotlin.utils.extractImageFromMultipart
import com.sukakotlin.utils.idToken
import com.sukakotlin.utils.respondFailure
import com.sukakotlin.utils.userId
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.userRoutes() {
    val userService by inject<UserService>()

    authenticate("firebase-auth") {
        route("/users") {
            get {
                val userId = call.userId!!
                val query = call.request.queryParameters["query"]
                    ?.takeIf { it.length >= 3 }
                    ?: return@get call.respond(
                        HttpStatusCode.BadRequest,
                        "Query parameter 'query' must be at least 3 characters long."
                    )

                userService.searchUsersByName(query, userId).fold(
                    onSuccess = { call.respond(it) },
                    onFailure = { call.respondFailure(it) }
                )
            }

            route("/me") {
                get {
                    val idToken = call.idToken!!
                    val result = userService.getOrCreateUser(idToken)
                    result.fold(
                        onSuccess = { call.respond(it) },
                        onFailure = { call.respondFailure(it) }
                    )
                }

                patch {
                    val userId = call.userId!!
                    val payload = call.receive<UpdateProfileRequest>()
                    userService.updateProfile(userId, payload.name, payload.bio).fold(
                        onSuccess = { call.respond(it) },
                        onFailure = { call.respondFailure(it) }
                    )
                }

                post("/profile-picture") {
                    val userId = call.userId!!
                    val imageData = extractImageFromMultipart(call)
                        ?: return@post call.respond(
                            HttpStatusCode.BadRequest,
                            "Missing image"
                        )

                    userService.updateProfilePicture(userId, imageData).fold(
                        onSuccess = { call.respond(it) },
                        onFailure = { call.respondFailure(it) }
                    )
                }

                post("/cover-picture") {
                    val userId = call.userId!!
                    val imageData = extractImageFromMultipart(call)
                        ?: return@post call.respond(
                            HttpStatusCode.BadRequest,
                            "Missing image"
                        )

                    userService.updateCoverPicture(userId, imageData).fold(
                        onSuccess = { call.respond(it) },
                        onFailure = { call.respondFailure(it) }
                    )
                }
            }

            route("/{userId}") {
                get {
                    val currentUserId = call.userId!!
                    val targetUserId = call.parameters["userId"]
                        ?: return@get call.respond(
                            HttpStatusCode.BadRequest,
                            "Missing userId"
                        )

                    userService.getUserDetail(targetUserId, currentUserId).fold(
                        onSuccess = { call.respond(it) },
                        onFailure = { call.respondFailure(it) }
                    )
                }

                post {
                    val currentUserId = call.userId!!
                    val targetUserId = call.parameters["userId"]
                        ?: return@post call.respond(
                            HttpStatusCode.BadRequest,
                            "Missing userId"
                        )

                    userService.followUser(currentUserId, targetUserId).fold(
                        onSuccess = { call.respond(it) },
                        onFailure = { call.respondFailure(it) }
                    )
                }

                delete {
                    val currentUserId = call.userId!!
                    val targetUserId = call.parameters["userId"]
                        ?: return@delete call.respond(
                            HttpStatusCode.BadRequest,
                            "Missing userId"
                        )

                    userService.unfollowUser(currentUserId, targetUserId).fold(
                        onSuccess = { call.respond(it) },
                        onFailure = { call.respondFailure(it) }
                    )
                }
            }
        }
    }
}

