package com.sukakotlin.domain.util

/**
 * Mengecek apakah string merupakan format email yang valid
 *
 * Pattern yang digunakan mengikuti standar RFC 5322 (simplified version)
 *
 * Contoh penggunaan:
 * ```
 * val email = "user@example.com"
 * if (email.isValidEmail()) {
 *     // Email valid
 * }
 * ```
 */
fun String.isValidEmail(): Boolean {
    val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$".toRegex()
    return this.matches(emailRegex)
}

/**
 * Mengecek apakah string merupakan format email yang valid (strict version)
 *
 * Versi yang lebih ketat, mengikuti standar RFC 5322 lebih detail
 *
 * Validasi:
 * - Local part (sebelum @): huruf, angka, dan karakter khusus tertentu
 * - Domain part (setelah @): harus valid domain dengan TLD minimal 2 karakter
 * - Tidak boleh ada karakter khusus di awal/akhir local part
 * - Tidak boleh ada titik berturut-turut (..)
 */
fun String.isValidEmailStrict(): Boolean {
    val emailRegex = """^[a-zA-Z0-9][a-zA-Z0-9._-]*[a-zA-Z0-9]@[a-zA-Z0-9]([a-zA-Z0-9-]*[a-zA-Z0-9])?(\.[a-zA-Z0-9]([a-zA-Z0-9-]*[a-zA-Z0-9])?)*\.[a-zA-Z]{2,}$""".toRegex()

    // Cek tidak ada titik berturut-turut
    if (this.contains("..")) return false

    return this.matches(emailRegex)
}

/**
 * Mengecek apakah string merupakan format email yang valid dengan validasi tambahan
 *
 * Validasi tambahan:
 * - Panjang maksimal 254 karakter (sesuai RFC 5321)
 * - Local part maksimal 64 karakter
 * - Domain part minimal 4 karakter (contoh: a@b.co)
 */
fun String.isValidEmailWithLength(): Boolean {
    if (this.length > 254) return false

    val parts = this.split("@")
    if (parts.size != 2) return false

    val localPart = parts[0]
    val domainPart = parts[1]

    if (localPart.length > 64 || domainPart.length < 4) return false

    return this.isValidEmailStrict()
}

/**
 * Normalize email untuk perbandingan
 * - Convert ke lowercase
 * - Trim whitespace
 *
 * Contoh:
 * ```
 * val email = "  User@Example.COM  "
 * val normalized = email.normalizeEmail() // "user@example.com"
 * ```
 */
fun String.normalizeEmail(): String {
    return this.trim().lowercase()
}

/**
 * Validasi dan normalize email sekaligus
 * Return null jika email tidak valid
 *
 * Contoh:
 * ```
 * val email = "  User@Example.COM  "
 * val validEmail = email.validateAndNormalizeEmail() // "user@example.com"
 *
 * val invalid = "invalid-email"
 * val result = invalid.validateAndNormalizeEmail() // null
 * ```
 */
fun String.normalizeAndValidateEmail() = this.normalizeEmail().isValidEmailStrict()

/**
 * Mendapatkan domain dari email
 * Return null jika email tidak valid
 *
 * Contoh:
 * ```
 * val email = "user@example.com"
 * val domain = email.getEmailDomain() // "example.com"
 * ```
 */
fun String.getEmailDomain(): String? {
    if (!this.isValidEmail()) return null
    return this.substringAfter("@", "")
}

/**
 * Mendapatkan local part dari email (sebelum @)
 * Return null jika email tidak valid
 *
 * Contoh:
 * ```
 * val email = "user@example.com"
 * val localPart = email.getEmailLocalPart() // "user"
 * ```
 */
fun String.getEmailLocalPart(): String? {
    if (!this.isValidEmail()) return null
    return this.substringBefore("@", "")
}

/**
 * Mask email untuk privacy
 * Contoh: user@example.com -> u***@example.com
 */
fun String.maskEmail(): String {
    if (!this.isValidEmail()) return this

    val parts = this.split("@")
    val localPart = parts[0]
    val domainPart = parts[1]

    val maskedLocal = if (localPart.length <= 2) {
        localPart[0] + "*"
    } else {
        localPart[0] + "***"
    }

    return "$maskedLocal@$domainPart"
}
