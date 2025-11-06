package com.sukakotlin.core.util

fun String.uppercaseEachWord() = this
    .lowercase()
    .split(' ')
    .joinToString(" ") {
        it.replaceFirstChar { char -> char.uppercaseChar() }
    }