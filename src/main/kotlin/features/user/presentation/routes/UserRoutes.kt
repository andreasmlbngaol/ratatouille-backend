package com.sukakotlin.features.user.presentation.routes

import com.sukakotlin.features.user.domain.model.profile.ImageData
import com.sukakotlin.features.user.domain.use_case.auth.GetOrCreateUserUseCase
import com.sukakotlin.features.user.domain.use_case.profile.UpdateUserPictureUseCase
import com.sukakotlin.features.user.domain.use_case.profile.UpdateUserProfileUseCase
import com.sukakotlin.features.user.domain.use_case.social.FollowUserUseCase
import com.sukakotlin.features.user.domain.use_case.social.GetUserDetailUseCase
import com.sukakotlin.features.user.presentation.dto.UpdateProfileRequest
import com.sukakotlin.features.user.presentation.dto.toDto
import com.sukakotlin.features.user.presentation.util.idToken
import com.sukakotlin.features.user.presentation.util.userId
import com.sukakotlin.presentation.util.failureResponse
import com.sukakotlin.presentation.util.respondResult
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.ApplicationCall
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

                    call.respondResult(result) { it.toDto() }
                }

                patch {
                    val idToken = call.userId!!
                    val payload = call.receive<UpdateProfileRequest>()

                    val result = updateProfileUseCase(idToken, payload.name,payload.bio)

                    call.respondResult(result) { it.toDto() }
                }

                post("/profile-picture") {
                    val idToken = call.userId!!
                    val imageData = extractImageFromMultipart(call)
                        ?: return@post call.respond(
                            HttpStatusCode.BadRequest,
                            failureResponse("Missing image")
                        )

                    val result = updatePictureUseCase.updateProfilePicture(idToken, imageData)

                    call.respondResult(result) { it.toDto() }
                }

                post("/cover-picture") {
                    val idToken = call.userId!!
                    val imageData = extractImageFromMultipart(call)
                        ?: return@post call.respond(
                            HttpStatusCode.BadRequest,
                            failureResponse("Missing image")
                        )

                    val result = updatePictureUseCase.updateCoverPicture(idToken, imageData)

                    call.respondResult(result) { it.toDto() }
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

                    call.respondResult(result) { it.toDto() }
                }

                post {
                    val currentUserId = call.userId!!
                    val targetUserId = call.parameters["userId"]
                        ?: return@post call.respond(
                            HttpStatusCode.BadRequest,
                            failureResponse("Missing userId")
                        )

                    val result = followUserUseCase.follow(currentUserId, targetUserId)
                    call.respondResult(result) { it.toDto() }
                }

                delete {
                    val currentUserId = call.userId!!
                    val targetUserId = call.parameters["userId"]
                        ?: return@delete call.respond(
                            HttpStatusCode.BadRequest,
                            failureResponse("Missing userId")
                        )

                    val result = followUserUseCase.unfollow(currentUserId, targetUserId)
                    call.respondResult(result) { it.toDto() }
                }
            }
        }
    }
}

private suspend fun extractImageFromMultipart(call: ApplicationCall): ImageData? {
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
