package com.sukakotlin.presentation.util

import com.sukakotlin.config.apiJson
import com.sukakotlin.presentation.model.ApiResponse
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.serializer


@OptIn(InternalSerializationApi::class)
private fun Any?.toJsonElement(): JsonElement {
    return if (this == null) {
        JsonNull
    } else {
        try {
            @Suppress("UNCHECKED_CAST")
            val serializer = this::class.serializer() as kotlinx.serialization.KSerializer<Any>
            val jsonString = apiJson.encodeToString(serializer, this)
            apiJson.parseToJsonElement(jsonString)
        } catch (e: Exception) {
            JsonNull
        }
    }
}

fun <T> successResponse(
    message: String? = null,
    data: T? = null
): ApiResponse {
    return ApiResponse(
        success = true,
        message = message,
        data = data?.toJsonElement()
    )
}

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

typealias EmptyResponse = ApiResponse

fun emptySuccessResponse(message: String = "Success") = ApiResponse(
    success = true,
    message = message,
    data = null
)