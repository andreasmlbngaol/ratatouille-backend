package com.sukakotlin.utils

import com.sukakotlin.model.ImageData
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.PartData
import io.ktor.http.content.forEachPart
import io.ktor.server.application.ApplicationCall
import io.ktor.server.request.receiveMultipart
import io.ktor.server.response.respond
import io.ktor.utils.io.readRemaining
import kotlinx.io.readByteArray

suspend fun ApplicationCall.respondFailure(error: Throwable) {
    when(error) {
        is IllegalArgumentException -> respond(
            HttpStatusCode.BadRequest,
            error.message ?: "Bad Request"
        )
        is NoSuchElementException -> respond(
            HttpStatusCode.NotFound,
            error.message ?: "Resource Not Found"
        )
        is IllegalStateException -> respond(
            HttpStatusCode.Conflict,
            error.message ?: "Conflict"
        )
        else -> throw error
    }
}

suspend fun extractImageFromMultipart(call: ApplicationCall): ImageData? {
    val multipartData = call.receiveMultipart()
    var imageData: ImageData? = null

    multipartData.forEachPart { part ->
        if (part is PartData.FileItem && part.name == "image") {
            imageData = ImageData(
                content = part.provider().readRemaining().readByteArray(),
                mimeType = part.contentType?.toString() ?: "image/jpeg",
                fileName = part.originalFileName ?: "image.jpeg"
            )
        }
        part.dispose()
    }

    return imageData
}