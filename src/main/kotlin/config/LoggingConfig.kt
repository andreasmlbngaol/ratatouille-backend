package com.sukakotlin.config

import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.plugins.calllogging.*
import io.ktor.server.request.*
import org.slf4j.event.Level

fun Application.configureLogging() {
    install(CallLogging) {
        level = Level.INFO
        filter { call -> call.request.path().startsWith("/api") }
        format { call ->
            val method = call.request.httpMethod.value
            val path = call.request.uri
            val status = call.response.status()
            val auth = call.request.headers["Authorization"]
            val ip = call.request.headers["X-Forwarded-For"] ?: call.request.origin.remoteHost

            "[$ip] $method $path -> $status\n\tAuthorization: $auth"
        }
    }
}