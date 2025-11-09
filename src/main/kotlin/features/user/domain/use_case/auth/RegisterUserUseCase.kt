package com.sukakotlin.features.user.domain.use_case.auth

import com.sukakotlin.domain.util.normalizeAndValidateEmail
import com.sukakotlin.features.user.domain.repository.UsersRepository
import com.sukakotlin.features.user.domain.model.User
import com.sukakotlin.features.user.domain.service.AuthService
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class RegisterUserUseCase(
    private val usersRepository: UsersRepository,
    private val authService: AuthService
) {
    @OptIn(ExperimentalTime::class)
    suspend operator fun invoke(
        uid: String,
        idToken: String,
        name: String,
        profilePictureUrl: String?,
        email: String
    ): Result<User> {
        return try {
            /**
             * **Validasi Input**
             * Nama tidak boleh kosong dan panjang minimal 3 karakter
             * Email tidak boleh kosong dan email valid sesuai standar email
            **/
            if(name.isBlank()) {
                return Result.failure(IllegalArgumentException("Name cannot be blank"))
            }
            if(name.length < 3) {
                return Result.failure(IllegalArgumentException("Name must be at least 3 characters long"))
            }
            if(email.isBlank()) {
                return Result.failure(IllegalArgumentException("Email cannot be blank"))
            }
            if(!email.normalizeAndValidateEmail()) {
                return Result.failure(IllegalArgumentException("Email is not valid"))
            }

            /**
             * **Verifikasi Firebase Id Token**
            **/
            val tokenVerification = authService.verifyIdToken(idToken)
            if(!tokenVerification.isValid) {
                return Result.failure(IllegalArgumentException("Invalid Firebase ID token"))
            }

            /**
             * **Verikasi UID**
             **/
            if(tokenVerification.uid != uid) {
                return Result.failure(IllegalArgumentException("Invalid Firebase UID"))
            }

            /**
             * **Cek User terdaftar atau belum**
             */
            if(usersRepository.existsById(uid)) {
                return Result.failure(IllegalStateException("User already registered"))
            }

            /**
             * **Cek Email terdaftar atau belum**
             */
            if(usersRepository.existsByEmail(email)) {
                return Result.failure(IllegalStateException("Email already registered"))
            }

            /**
             * **Buat domain object User Baru**
             */
            val user = User(
                id = uid,
                name = name,
                email = email,
                profilePictureUrl = profilePictureUrl,
                coverPictureUrl = null,
                bio = null,
                isEmailVerified = tokenVerification.isEmailVerified,
                createdAt = Clock.System.now().toLocalDateTime(TimeZone.of("Asia/Jakarta"))
            )

            /**
             * **Simpan User ke Database**
             */
            val savedUser = usersRepository.save(user)

            Result.success(savedUser)
        } catch(e: Exception) {
            Result.failure(e)
        }
    }
}