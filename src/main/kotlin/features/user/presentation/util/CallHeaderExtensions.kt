package com.sukakotlin.features.user.presentation.util

import io.ktor.server.auth.UserIdPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.routing.RoutingCall

val RoutingCall.idToken: String?
    get() {
        val authHeader = this.request.headers["Authorization"]
        return authHeader?.removePrefix("Bearer ")?.trim()
    }

val RoutingCall.userId: String?
    get() = this.principal<UserIdPrincipal>()?.name