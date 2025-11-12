package com.sukakotlin.features.user.data.service

import com.sukakotlin.features.user.domain.model.profile.ImageData
import com.sukakotlin.features.user.domain.service.ImageUploadPort
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayInputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import javax.imageio.IIOImage
import javax.imageio.ImageIO
import javax.imageio.ImageWriteParam

class LocalFileImageUploadAdapter(
    private val uploadDir: String = "/var/www/uploads/images"
): ImageUploadPort {
    private val logger = org.slf4j.LoggerFactory.getLogger(this::class.java)

    private suspend fun uploadImage(
        userId: String,
        imageData: ImageData,
        type: String,
        quality: Float = 0.8f
    ): String = withContext(Dispatchers.IO) {
        try {
            val fileName = "$type-${System.currentTimeMillis()}.webp"
            val userDir = File(uploadDir, userId)

            if (!userDir.exists()) {
                val created = userDir.mkdirs()
                if (!created) {
                    throw IOException("Failed to create directory: ${userDir.absolutePath}")
                }
                logger.debug("Directory created: ${userDir.absolutePath}")
            }

            val file = File(userDir, fileName)

            convertToWebP(imageData.content, file, quality)

            logger.debug("Image uploaded: /uploads/images/$userId/$fileName")
            return@withContext "/uploads/images/$userId/$fileName"
        } catch(e: Exception) {
            logger.error("Failed to upload image for user: $userId", e)
            throw e
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

            // Check available writers
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

    override suspend fun uploadProfilePicture(
        userId: String,
        imageData: ImageData
    ): String = uploadImage(userId, imageData, "profile", 0.5f)

    override suspend fun uploadCoverPicture(
        userId: String,
        imageData: ImageData
    ): String = uploadImage(userId, imageData, "cover")
}