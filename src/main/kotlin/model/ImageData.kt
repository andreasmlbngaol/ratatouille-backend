package com.sukakotlin.model

data class ImageData(
    val content: ByteArray,
    val mimeType: String,
    val fileName: String
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ImageData) return false
        return mimeType == other.mimeType && fileName == other.fileName
    }

    override fun hashCode(): Int {
        return 31 * mimeType.hashCode() + fileName.hashCode()
    }
}