package com.sukakotlin.presentation.util

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.response.respond

suspend inline fun <reified T: Any> ApplicationCall.respondResult(
    result: Result<T>,
    successStatusCode: HttpStatusCode = HttpStatusCode.OK,
    crossinline transform: (T) -> Any = { it }
) {
    result.fold(
        onSuccess = { data ->
            respond(successStatusCode, successResponse(data = transform(data)))
        },
        onFailure = { error ->
            when(error) {
                is IllegalArgumentException -> respond(
                    HttpStatusCode.BadRequest,
                    failureResponse(error.message ?: "Bad Request")
                )
                is NoSuchElementException -> respond(
                    HttpStatusCode.NotFound,
                    failureResponse(error.message ?: "Resource Not Found")
                )
                is IllegalStateException -> respond(
                    HttpStatusCode.Conflict,
                    failureResponse(error.message ?: "Conflict")
                )
                else -> throw error
            }

        }
    )
}