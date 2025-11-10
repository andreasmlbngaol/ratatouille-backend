import com.sukakotlin.features.user.data.repository.UsersRepositoryImpl
import com.sukakotlin.features.user.data.service.AuthServiceImpl
import com.sukakotlin.features.user.domain.repository.UsersRepository
import com.sukakotlin.features.user.domain.service.AuthService
import com.sukakotlin.features.user.domain.use_case.auth.GetOrCreateUserUseCase
import org.koin.dsl.module

val userModule = module {
    single<UsersRepository> { UsersRepositoryImpl }
    single<AuthService> { AuthServiceImpl() }
    factory { GetOrCreateUserUseCase(get(), get()) }
}