package com.sukakotlin.features.user.data.service

import com.sukakotlin.features.user.domain.service.ImageCleanupPort
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class LocalFileImageCleanupAdapter(
    private val uploadDir: String = "/var/www/uploads/images"
): ImageCleanupPort {
    override suspend fun deleteImage(imageUrl: String) = withContext(Dispatchers.IO) {
        try {
            val relativePath = imageUrl
                .removePrefix("/uploads/images")

            val file = File(uploadDir, relativePath)
            if(!file.exists()) {
                return@withContext
            }

            if(!file.absolutePath.startsWith(File(uploadDir).absolutePath)) {
                throw SecurityException("Attempted to delete file outside of upload directory")
            }

            if(file.isFile) {
                val deleted = file.delete()
                if(!deleted) {
                    throw IllegalStateException("Failed to delete file: ${file.absolutePath}")
                }
            }
        } catch(e: Exception) {
            throw IllegalStateException("Failed to delete image: $imageUrl", e)
        }
    }
}