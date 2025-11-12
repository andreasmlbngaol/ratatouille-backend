package com.sukakotlin.features.user.domain.model.social

import kotlinx.datetime.LocalDateTime

data class Follow(
    val id: Long,
    val followerId: String,
    val followingId: String,
    val createdAt: LocalDateTime
)