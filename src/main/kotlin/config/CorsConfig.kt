package com.sukakotlin.config

import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.cors.routing.CORS

fun Application.configureCORS() {
    install(CORS) {
        anyHost()
        listOf(
            HttpMethod.Get,
            HttpMethod.Post,
            HttpMethod.Put,
            HttpMethod.Delete,
            HttpMethod.Patch,
        ).forEach { allowMethod(it) }

        listOf(
            HttpHeaders.ContentType,
            HttpHeaders.Authorization,
        ).forEach { allowHeader(it) }
    }
}