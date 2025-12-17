package com.sukakotlin.config

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.application.log
import io.ktor.server.plugins.BadRequestException
import io.ktor.server.plugins.ContentTransformationException
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.response.respond

fun Application.configureExceptionHandling() {
    install(StatusPages) {

        // 400 - Bad Request (JSON invalid, parameter salah, dll)
        exception<BadRequestException> { call, cause ->
            call.application.log.warn("Bad request: ${cause.message}")

            call.respond(
                HttpStatusCode.BadRequest,
                cause.message ?: "Bad request"
            )
        }

        // 400 - Content negotiation / serialization error
        exception<ContentTransformationException> { call, cause ->
            call.application.log.warn("Invalid request body", cause)

            call.respond(
                HttpStatusCode.BadRequest,
                "Invalid request body"
            )
        }

        exception<Throwable> { call, cause ->
            call.application.log.error("Unhandled exception:", cause)
            cause.printStackTrace()  // ✅ Print full stack trace

            call.respond(
                HttpStatusCode.InternalServerError,
                cause.message ?: "Something went wrong!"
            )
        }
    }
}