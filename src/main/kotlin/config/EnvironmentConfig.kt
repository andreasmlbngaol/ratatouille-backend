package com.sukakotlin.config

import io.github.cdimascio.dotenv.dotenv

fun configureEnvironment() {
    val dotenv = dotenv {
        ignoreIfMissing = true
    }

    dotenv.entries().forEach { entry ->
        if(System.getenv(entry.key) == null) {
            System.setProperty(entry.key, entry.value)
        }
    }
}