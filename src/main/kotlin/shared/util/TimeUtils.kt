package com.sukakotlin.shared.util

import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
val now = Clock.System.now().toLocalDateTime(TimeZone.of("Asia/Jakarta"))