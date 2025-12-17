package com.sukakotlin.config

import com.sukakotlin.routes.recipeRoutes
import com.sukakotlin.routes.userRoutes
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.*
import io.ktor.server.http.content.staticFiles
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.io.File

fun Application.configureRouting() {
    routing {
        staticFiles("/uploads", File("/var/www/uploads"))


        get("/") { call.respondRedirect("/api") }
        route("/api") {
            get {
                call.respond(
                    HttpStatusCode.OK,
                    "Hello from Ratatouille Backend, this API is being used in Ratatouille Mobile App in https://github.com/andreasmlbngaol/ratatouille!"
                )
            }
            userRoutes()
            recipeRoutes()
        }
    }
}