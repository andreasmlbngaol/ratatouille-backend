package com.sukakotlin

import com.sukakotlin.config.*
import com.sukakotlin.data.database.DatabaseFactory
import io.ktor.server.application.*
import io.ktor.server.netty.*

fun main(args: Array<String>) {
    EngineMain.main(args)
}

fun Application.module() {
    configureEnvironment()
    DatabaseFactory.init(environment.config)
    configureFirebase()
    configureKoin()
    configureSerialization()
    configureSecurity()
    configureExceptionHandling()
    configureLogging()
    configureRouting()
}