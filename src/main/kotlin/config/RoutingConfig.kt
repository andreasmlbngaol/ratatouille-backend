package com.sukakotlin.config

import com.sukakotlin.features.user.presentation.routes.userRoutes
import com.sukakotlin.presentation.util.emptySuccessResponse
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.io.File

fun Application.configureRouting() {
    routing {
        configureDocumentation()
        staticFiles("/uploads", File("/var/www/uploads"))

        get("/") { call.respondRedirect("/api") }
        route("/api") {
            get {
                call.respond(
                    HttpStatusCode.OK,
                    emptySuccessResponse(
                        "Hello from Ratatouille Backend, this API is being used in Ratatouille Mobile App in https://github.com/andreasmlbngaol/ratatouille!"
                    )
                )
            }
            userRoutes()
        }
    }
}