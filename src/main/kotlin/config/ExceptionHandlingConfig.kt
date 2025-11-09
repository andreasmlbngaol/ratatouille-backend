package com.sukakotlin.config

import com.sukakotlin.presentation.util.internalFailureResponse
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.response.respond

fun Application.configureExceptionHandling() {
    install(StatusPages) {
        exception<Throwable> { call, cause ->
            call.respond(
                HttpStatusCode.InternalServerError,
                internalFailureResponse(cause as? Exception ?: Exception(cause))
            )
        }
    }
}