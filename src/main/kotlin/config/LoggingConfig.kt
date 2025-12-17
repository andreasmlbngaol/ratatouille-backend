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
            val ip = call.request.headers["X-Forwarded-For"] ?: call.request.origin.remoteHost
            val method = call.request.httpMethod.value
            val path = call.request.uri
            val status = call.response.status()
            val authFull = call.request.headers["Authorization"]
//            val auth = if(status?.value in 200..300) "${authFull?.removePrefix("Bearer ")?.substring(0, 20)}..." else authFull
            val auth = authFull

//            "[$ip] $method $path -> $status\n\tAuthorization: $auth"
            auth ?: "No Auth"
        }
    }
}