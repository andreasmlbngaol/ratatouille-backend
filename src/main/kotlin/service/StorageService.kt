package com.sukakotlin.service

import com.sukakotlin.model.ImageData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.slf4j.LoggerFactory
import java.io.ByteArrayInputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import javax.imageio.IIOImage
import javax.imageio.ImageIO
import javax.imageio.ImageWriteParam

class StorageService(
    private val uploadDir: String = "/var/www/uploads/images"
) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    // ============ UPLOAD OPERATIONS ============

    suspend fun uploadProfilePicture(userId: String, imageData: ImageData): String {
        return uploadProfileImage(userId, imageData, "profile", quality = 0.5f)
    }

    suspend fun uploadCoverPicture(userId: String, imageData: ImageData): String {
        return uploadProfileImage(userId, imageData, "cover", quality = 0.8f)
    }

    suspend fun uploadRecipeImage(
        userId: String,
        recipeId: Long,
        imageData: ImageData
    ): String {
        return uploadImage(userId, imageData, "recipe-$recipeId", quality = 0.7f)
    }

    suspend fun uploadStepImage(
        userId: String,
        recipeId: Long,
        stepId: Long,
        imageData: ImageData
    ): String {
        return uploadImage(userId, imageData, "recipe-$recipeId/step-$stepId", quality = 0.5f)
    }

    suspend fun uploadCommentImage(
        userId: String,
        recipeId: Long,
        imageData: ImageData
    ): String {
        return uploadImage(userId, imageData, "recipe-$recipeId/comments", quality = 0.7f)
    }

    private suspend fun uploadProfileImage(
        userId: String,
        imageData: ImageData,
        type: String,
        quality: Float = 0.8f
    ): String = withContext(Dispatchers.IO) {
        try {
            val fileName = "$type-${System.currentTimeMillis()}.webp"
            val userDir = File(uploadDir, userId)
            createDir(userDir)
            val file = File(userDir, fileName)

            convertToWebP(imageData.content, file, quality)

            val absolutePath = "/uploads/images/$userId/$fileName"
            logger.debug("Image uploaded: $absolutePath")
            return@withContext absolutePath
        } catch (e: Exception) {
            logger.error("Failed to upload image for user: $userId", e)
            throw e
        }
    }

    private suspend fun uploadImage(
        userId: String,
        imageData: ImageData,
        childPath: String,
        quality: Float = 0.8f
    ): String = withContext(Dispatchers.IO) {
        try {
            val fileName = "${System.currentTimeMillis()}.webp"
            val userDir = File(uploadDir, "$userId/$childPath")
            createDir(userDir)
            val file = File(userDir, fileName)

            convertToWebP(imageData.content, file, quality)

            val absolutePath = "/uploads/images/$userId/$childPath/$fileName"
            logger.debug("Image uploaded: $absolutePath")
            return@withContext absolutePath
        } catch (e: Exception) {
            logger.error("Failed to upload image for user: $userId, path: $childPath", e)
            throw e
        }
    }

    // ============ DELETE OPERATIONS ============

    suspend fun deleteImage(imageUrl: String): Unit = withContext(Dispatchers.IO) {
        try {
            val relativePath = imageUrl.removePrefix("/uploads/images")
            val file = File(uploadDir, relativePath)

            if (!file.exists()) {
                logger.debug("File does not exist: $imageUrl")
                return@withContext
            }

            if (!file.absolutePath.startsWith(File(uploadDir).absolutePath)) {
                throw SecurityException("Attempted to delete file outside of upload directory")
            }

            if (file.isFile) {
                val deleted = file.delete()
                if (!deleted) {
                    throw IllegalStateException("Failed to delete file: ${file.absolutePath}")
                }
                logger.debug("Image deleted: $imageUrl")
            }
        } catch (e: Exception) {
            logger.error("Failed to delete image: $imageUrl", e)
            throw e
        }
    }

    // ============ HELPER METHODS ============

    private fun createDir(userDir: File) {
        if (!userDir.exists()) {
            val created = userDir.mkdirs()
            if (!created) {
                throw IOException("Failed to create directory: ${userDir.absolutePath}")
            }
            logger.debug("Directory created: ${userDir.absolutePath}")
        }
    }

    private fun convertToWebP(
        imageBytes: ByteArray,
        outputFile: File,
        quality: Float = 0.8f
    ) {
        try {
            logger.debug("Starting WebP conversion. File size: ${imageBytes.size} bytes")

            val input = ByteArrayInputStream(imageBytes)
            val bufferedImage = ImageIO.read(input)
                ?: throw IllegalArgumentException("Failed to read image from bytes")

            logger.debug("Image loaded successfully. Dimension: ${bufferedImage.width}x${bufferedImage.height}")

            val writerNames = ImageIO.getWriterFormatNames()
            logger.debug("Available image formats: ${writerNames.joinToString(", ")}")

            val writers = ImageIO.getImageWritersByFormatName("webp")
            if (!writers.hasNext()) {
                throw RuntimeException("No WebP writers found. Available formats: ${writerNames.joinToString(", ")}")
            }

            val writer = writers.next()
            logger.debug("WebP writer found: ${writer.javaClass.simpleName}")

            FileOutputStream(outputFile).use { fos ->
                val imageOutputStream = ImageIO.createImageOutputStream(fos)
                    ?: throw RuntimeException("Failed to create image output stream")

                writer.output = imageOutputStream

                val writeParam = writer.defaultWriteParam
                logger.debug("Write param compression supported: ${writeParam.canWriteCompressed()}")

                if (writeParam.canWriteCompressed()) {
                    val compressionTypes = writeParam.compressionTypes
                    logger.debug("Available compression types: ${compressionTypes.joinToString(", ")}")

                    if (compressionTypes.isNotEmpty()) {
                        writeParam.compressionMode = ImageWriteParam.MODE_EXPLICIT
                        writeParam.compressionType = compressionTypes.first()
                        writeParam.compressionQuality = quality
                        logger.debug("Compression set: type=${compressionTypes.first()}, quality=$quality")
                    }
                }

                writer.write(null, IIOImage(bufferedImage, null, null), writeParam)
                logger.info("Image successfully converted to WebP: ${outputFile.absolutePath}")

                imageOutputStream.close()
                writer.dispose()
            }
        } catch (e: Exception) {
            logger.error("Failed to convert image to WebP", e)

            if (outputFile.exists()) {
                logger.debug("Deleting partial output file: ${outputFile.absolutePath}")
                outputFile.delete()
            }

            throw RuntimeException("Failed to convert image to WebP: ${e.message}", e)
        }
    }
}