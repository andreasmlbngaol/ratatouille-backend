package com.sukakotlin.user.routes

import com.sukakotlin.config.configureRouting
import com.sukakotlin.config.configureSerialization
import com.sukakotlin.features.user.domain.use_case.auth.RegisterUserUseCase
import com.sukakotlin.features.user.presentation.dto.RegisterRequest
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.testing.*
import io.mockk.*
import kotlin.test.*

class UserRoutesTest {

    private lateinit var useCase: RegisterUserUseCase

    @BeforeTest
    fun setup() {
        useCase = mockk()
    }

    private fun Application.testModule() {
        routing {
            // Dummy DI (inject manual)
            val register = useCase
            route("/users") {
                post("/register") {
                    val req = call.receive<RegisterRequest>()
                    val result = register(
                        req.uid,
                        req.idToken,
                        req.name,
                        req.profilePictureUrl,
                        req.email
                    )

                    result.fold(
                        onSuccess = {
                            call.respond(HttpStatusCode.Created, "OK")
                        },
                        onFailure = {
                            call.respond(HttpStatusCode.BadRequest, it.message!!)
                        }
                    )
                }
            }
        }
    }

    @Test
    fun `register returns 400 when request body is invalid`() = testApplication {
        // Install routing + serialization module yang sama seperti di Application asli
        application {
            configureSerialization()
            configureRouting() // pastikan ini route yg kamu test
        }

        val invalidBody = """{
            "firebase_uid": "",
            "id_token": "",
            "name": "",
            "email": ""
        }""".trimIndent()

        val response = client.post("/api/users/register") {
            contentType(ContentType.Application.Json)
            setBody(invalidBody)
        }

        assertEquals(HttpStatusCode.BadRequest, response.status)
    }

    @Test
    fun `register returns 200 OK when valid request`() = testApplication {
        application {
            configureSerialization()
            configureRouting()
        }

        val validBody = """{
            "firebase_uid": "UID123",
            "id_token": "valid-token-xyz",
            "name": "John Doe",
            "profile_picture_url": null,
            "email": "john@example.com"
        }""".trimIndent()

        val response = client.post("/api/auth/register") {
            contentType(ContentType.Application.Json)
            setBody(validBody)
        }

        assertEquals(HttpStatusCode.OK, response.status)
    }
}
