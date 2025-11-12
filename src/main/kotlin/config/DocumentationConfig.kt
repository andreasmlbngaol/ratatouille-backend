package com.sukakotlin.config

import io.ktor.server.plugins.openapi.openAPI
import io.ktor.server.plugins.swagger.swaggerUI
import io.ktor.server.routing.Routing
import io.swagger.codegen.v3.generators.html.StaticHtmlCodegen

fun Routing.configureDocumentation() {
    openAPI(path="openapi", swaggerFile = "openapi/documentation.yaml") {
        codegen = StaticHtmlCodegen()
    }
    swaggerUI(path = "swagger", swaggerFile = "openapi/documentation.yaml")
}
