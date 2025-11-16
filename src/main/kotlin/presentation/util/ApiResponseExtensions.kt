package com.sukakotlin.presentation.util

import com.sukakotlin.presentation.model.FailureResponse
import kotlinx.serialization.Serializable


//@OptIn(InternalSerializationApi::class)
//private fun Any?.toJsonElement(): JsonElement {
//    return if (this == null) {
//        JsonNull
//    } else {
//        try {
//            @Suppress("UNCHECKED_CAST")
//            val serializer = this::class.serializer() as kotlinx.serialization.KSerializer<Any>
//            val jsonString = apiJson.encodeToString(serializer, this)
//            apiJson.parseToJsonElement(jsonString)
//        } catch (e: Exception) {
//            JsonNull
//        }
//    }
//}
//
//fun <T> successResponse(
//    message: String? = null,
//    data: T? = null
//): FailureResponse {
//    return FailureResponse(
//        success = true,
//        message = message,
//        data = data?.toJsonElement()
//    )
//}

fun failureResponse(
    message: String
) = FailureResponse(
    success = false,
    message = message,
    data = null
)

fun internalFailureResponse(
    exception: Exception
) = failureResponse("Internal Server Error${exception.message?.let { ": $it" } ?: ""}")

@Serializable
data class EmptySuccessResponse(
    val success: Boolean = true,
    val message: String? = null,
    val data: Nothing? = null
)

fun emptySuccessResponse(message: String = "Success") = EmptySuccessResponse(message = message)