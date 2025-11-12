import com.sukakotlin.features.user.data.repository.FollowsRepositoryImpl
import com.sukakotlin.features.user.data.repository.UsersRepositoryImpl
import com.sukakotlin.features.user.data.service.AuthServiceImpl
import com.sukakotlin.features.user.data.service.LocalFileImageCleanupAdapter
import com.sukakotlin.features.user.data.service.LocalFileImageUploadAdapter
import com.sukakotlin.features.user.domain.repository.FollowsRepository
import com.sukakotlin.features.user.domain.repository.UsersRepository
import com.sukakotlin.features.user.domain.service.AuthService
import com.sukakotlin.features.user.domain.service.ImageCleanupPort
import com.sukakotlin.features.user.domain.service.ImageUploadPort
import com.sukakotlin.features.user.domain.use_case.auth.GetOrCreateUserUseCase
import com.sukakotlin.features.user.domain.use_case.profile.UpdateUserPictureUseCase
import com.sukakotlin.features.user.domain.use_case.profile.UpdateUserProfileUseCase
import com.sukakotlin.features.user.domain.use_case.social.FollowUserUseCase
import com.sukakotlin.features.user.domain.use_case.social.GetUserDetailUseCase
import org.koin.dsl.module

val userModule = module {
    single<AuthService> { AuthServiceImpl() }
    single<ImageUploadPort> { LocalFileImageUploadAdapter() }
    single<ImageCleanupPort> { LocalFileImageCleanupAdapter() }

    single<UsersRepository> { UsersRepositoryImpl }
    single<FollowsRepository> { FollowsRepositoryImpl }

    factory { GetOrCreateUserUseCase(get(), get()) }
    factory { UpdateUserProfileUseCase(get()) }
    factory { UpdateUserPictureUseCase(get(), get(), get()) }
    factory { FollowUserUseCase(get(), get()) }
    factory { GetUserDetailUseCase(get(), get()) }
}