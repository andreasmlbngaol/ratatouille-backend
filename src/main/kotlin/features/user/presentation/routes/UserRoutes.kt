package com.sukakotlin.features.user.presentation.routes

import com.sukakotlin.domain.model.ImageData
import com.sukakotlin.features.user.domain.use_case.auth.GetOrCreateUserUseCase
import com.sukakotlin.features.user.domain.use_case.profile.UpdateUserPictureUseCase
import com.sukakotlin.features.user.domain.use_case.profile.UpdateUserProfileUseCase
import com.sukakotlin.features.user.domain.use_case.social.FollowUserUseCase
import com.sukakotlin.features.user.domain.use_case.social.GetUserDetailUseCase
import com.sukakotlin.features.user.presentation.dto.request.UpdateProfileRequest
import com.sukakotlin.features.user.presentation.dto.response.toResponse
import com.sukakotlin.presentation.util.idToken
import com.sukakotlin.presentation.util.userId
import com.sukakotlin.presentation.util.failureResponse
import com.sukakotlin.presentation.util.respondFailure
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.userRoutes() {
    val getOrCreateUser: GetOrCreateUserUseCase by inject()
    val updateProfileUseCase: UpdateUserProfileUseCase by inject()
    val updatePictureUseCase: UpdateUserPictureUseCase by inject()
    val followUserUseCase: FollowUserUseCase by inject()
    val getUserDetailUseCase: GetUserDetailUseCase by inject()

    authenticate("firebase-auth") {
        route("/users") {
            route("/me") {
                get {
                    val idToken = call.idToken!!

                    val result = getOrCreateUser(idToken)

                    result.fold(
                        onSuccess = { call.respond(it.toResponse()) },
                        onFailure = { call.respondFailure(it) }
                    )
                }

                patch {
                    val idToken = call.userId!!
                    val payload = call.receive<UpdateProfileRequest>()

                    val result = updateProfileUseCase(idToken, payload.name,payload.bio)

                    result.fold(
                        onSuccess = { call.respond(it.toResponse()) },
                        onFailure = { call.respondFailure(it) }
                    )
                }

                post("/profile-picture") {
                    val userId = call.userId!!
                    val imageData = extractImageFromMultipart(call)
                        ?: return@post call.respond(
                            HttpStatusCode.BadRequest,
                            failureResponse("Missing image")
                        )

                    val result = updatePictureUseCase.updateProfilePicture(userId, imageData)

                    result.fold(
                        onSuccess = { call.respond(it.toResponse()) },
                        onFailure = { call.respondFailure(it) }
                    )
                }

                post("/cover-picture") {
                    val userId = call.userId!!
                    val imageData = extractImageFromMultipart(call)
                        ?: return@post call.respond(
                            HttpStatusCode.BadRequest,
                            failureResponse("Missing image")
                        )

                    val result = updatePictureUseCase.updateCoverPicture(userId, imageData)

                    result.fold(
                        onSuccess = { call.respond(it.toResponse()) },
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
                            failureResponse("Missing userId")
                        )

                    val result = getUserDetailUseCase(targetUserId, currentUserId)

                    result.fold(
                        onSuccess = { call.respond(it.toResponse()) },
                        onFailure = { call.respondFailure(it) }
                    )
                }

                post {
                    val currentUserId = call.userId!!
                    val targetUserId = call.parameters["userId"]
                        ?: return@post call.respond(
                            HttpStatusCode.BadRequest,
                            failureResponse("Missing userId")
                        )

                    val result = followUserUseCase.follow(currentUserId, targetUserId)
                    result.fold(
                        onSuccess = { call.respond(it.toResponse()) },
                        onFailure = { call.respondFailure(it) }
                    )
                }

                delete {
                    val currentUserId = call.userId!!
                    val targetUserId = call.parameters["userId"]
                        ?: return@delete call.respond(
                            HttpStatusCode.BadRequest,
                            failureResponse("Missing userId")
                        )

                    val result = followUserUseCase.unfollow(currentUserId, targetUserId)
                    result.fold(
                        onSuccess = { call.respond(it.toResponse()) },
                        onFailure = { call.respondFailure(it) }
                    )
                }
            }
        }
    }
}

@Suppress("DEPRECATION")
suspend fun extractImageFromMultipart(call: ApplicationCall): ImageData? {
    val multipartData = call.receiveMultipart()
    var imageData: ImageData? = null

    multipartData.forEachPart { part ->
        println("Part name: ${part.name}, type: ${part::class.simpleName}")
        if (part is PartData.FileItem && part.name == "image") {
            imageData = ImageData(
                content = part.streamProvider().readAllBytes(),
                mimeType = part.contentType?.toString() ?: "image/jpeg",
                fileName = part.originalFileName ?: "image.jpeg"
            )
        }
        part.dispose()
    }

    return imageData
}
