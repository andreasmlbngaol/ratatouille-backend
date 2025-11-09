package com.sukakotlin.features.user.domain.use_case.auth

import com.sukakotlin.features.user.domain.model.User
import com.sukakotlin.features.user.domain.repository.UsersRepository
import com.sukakotlin.features.user.domain.service.AuthService
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

/**
 * UseCase untuk mendapatkan user yang sedang authenticated
 * Jika user belum ada (OAuth first time), akan otomatis membuat user baru
 * dengan data minimal dari Firebase token
 */
class GetOrCreateUserUseCase(
    private val usersRepository: UsersRepository,
    private val authService: AuthService
) {
    @OptIn(ExperimentalTime::class)
    suspend operator fun invoke(idToken: String): Result<User> {
        return try {
            /**
             * **Verifikasi Firebase Id Token**
             */
            val tokenVerification = authService.verifyIdToken(idToken)
            if(!tokenVerification.isValid) {
                return Result.failure(IllegalArgumentException("Invalid Firebase ID token"))
            }

            val uid = tokenVerification.uid
            val email = tokenVerification.email
                ?: return Result.failure(IllegalArgumentException("Email not found in token"))

            /**
             * **Cek User terdaftar atau belum**
             */
            val existingUser = usersRepository.findById(uid)
            if(existingUser != null) {
                if(existingUser.isEmailVerified != tokenVerification.isEmailVerified) {
                    usersRepository.updateEmailVerified(uid, tokenVerification.isEmailVerified)
                    return Result.success(existingUser.copy(isEmailVerified = tokenVerification.isEmailVerified))
                }
                return Result.success(existingUser)
            }

            /**
             * **Buat User baru**
             */
            val name = tokenVerification.name?.takeIf { it.isNotBlank() }
                ?: email.split("@").firstOrNull()
                ?: "Ratatouille User"

            val newUser = User(
                id = uid,
                name = name,
                email = email,
                profilePictureUrl = tokenVerification.pictureUrl,
                coverPictureUrl = null,
                bio = null,
                isEmailVerified = tokenVerification.isEmailVerified,
                createdAt = Clock.System.now().toLocalDateTime(TimeZone.of("Asia/Jakarta"))
            )

            val savedUser = usersRepository.save(newUser)
            return Result.success(savedUser)
        } catch(e: Exception) {
            Result.failure(e)
        }
    }
}