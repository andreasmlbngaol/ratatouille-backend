package com.sukakotlin

import com.sukakotlin.config.configureCORS
import com.sukakotlin.config.configureEnvironment
import com.sukakotlin.config.configureExceptionHandling
import com.sukakotlin.config.configureFirebase
import com.sukakotlin.config.configureKoin
import com.sukakotlin.config.configureLogging
import com.sukakotlin.config.configureRouting
import com.sukakotlin.config.configureSecurity
import com.sukakotlin.config.configureSerialization
import com.sukakotlin.database.DatabaseFactory
import io.ktor.server.application.*
import io.ktor.server.netty.EngineMain

fun main(args: Array<String>) {
    EngineMain.main(args)
}

fun Application.module() {
    configureEnvironment()
    configureKoin()
    DatabaseFactory.init(environment.config)
    configureFirebase()
    configureSerialization()
    configureSecurity()
    configureExceptionHandling()
    configureLogging()
    configureCORS()
    configureRouting()
}