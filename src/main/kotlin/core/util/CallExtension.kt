package com.sukakotlin.core.util

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.response.respond

suspend inline fun <reified T : Any> ApplicationCall.respondJson(
    status: HttpStatusCode,
    value: T,
    key: String? = null,
) {
    val newKey = key ?: "message"
    this.respond(
        status,
        mapOf(newKey to value)
    )
}