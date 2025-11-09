package com.sukakotlin.presentation.util

import com.sukakotlin.presentation.model.ApiResponse

fun <T> successResponse(
    message: String? = null,
    data: T? = null
) = ApiResponse(
    success = true,
    message = message,
    data = data
)

fun failureResponse(
    message: String
) = ApiResponse(
    success = false,
    message = message,
    data = null
)

fun internalFailureResponse(
    exception: Exception
) = failureResponse("Internal Server Error${exception.message?.let { ": $it" } ?: ""}")

typealias EmptyResponse = ApiResponse<Unit>

fun emptySuccessResponse(message: String = "Success") = ApiResponse<Unit>(
    success = true,
    message = message,
    data = null
)