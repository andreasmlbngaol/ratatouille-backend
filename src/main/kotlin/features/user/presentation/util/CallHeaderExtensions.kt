package com.sukakotlin.features.user.presentation.util

import io.ktor.server.routing.RoutingCall

val RoutingCall.idToken: String?
    get() {
        val authHeader = this.request.headers["Authorization"]
        return authHeader?.removePrefix("Bearer ")?.trim()
    }