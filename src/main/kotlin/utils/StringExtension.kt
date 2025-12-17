package com.sukakotlin.utils

fun String.uppercaseEachWord() = this
    .lowercase()
    .split(' ')
    .joinToString(" ") {
        it.replaceFirstChar { char -> char.uppercaseChar() }
    }