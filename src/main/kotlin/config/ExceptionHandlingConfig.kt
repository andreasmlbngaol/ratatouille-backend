package com.sukakotlin.config

import com.sukakotlin.presentation.util.internalFailureResponse
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.application.log
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.response.respond

fun Application.configureExceptionHandling() {
    install(StatusPages) {
        exception<Throwable> { call, cause ->

            call.application.log.error("Unhandled exception:", cause)
            cause.printStackTrace()  // ✅ Print full stack trace

            call.respond(
                HttpStatusCode.InternalServerError,
                internalFailureResponse(cause as? Exception ?: Exception(cause))
            )
        }
    }
}