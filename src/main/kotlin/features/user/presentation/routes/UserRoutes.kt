package com.sukakotlin.features.user.presentation.routes

import com.sukakotlin.features.user.domain.use_case.auth.GetOrCreateUserUseCase
import com.sukakotlin.features.user.domain.use_case.auth.RegisterUserUseCase
import com.sukakotlin.features.user.presentation.dto.RegisterRequest
import com.sukakotlin.features.user.presentation.util.idToken
import com.sukakotlin.features.user.presentation.util.toDto
import com.sukakotlin.presentation.util.failureResponse
import com.sukakotlin.presentation.util.respondResult
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.userRoutes() {
    val registerUser: RegisterUserUseCase by inject()
    val getOrCreateUser: GetOrCreateUserUseCase by inject()

    route("/users") {
        post("/register") {
            val request = call.receive<RegisterRequest>()

            val result = registerUser(
                uid = request.uid,
                idToken = request.idToken,
                name = request.name,
                profilePictureUrl = request.profilePictureUrl,
                email = request.email
            )

            call.respondResult(
                result = result,
                successStatusCode = HttpStatusCode.Created,
            ) { it.toDto() }
        }

        authenticate("firebase-auth") {
            get("/me") {
                val idToken = call.idToken
                    ?: return@get call.respond(
                        HttpStatusCode.Unauthorized,
                        failureResponse("Missing id token")
                    )

                val result = getOrCreateUser(idToken)
                call.respondResult(result) { it.toDto() }
            }
        }
    }
}