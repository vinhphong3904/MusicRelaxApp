package com.example.musicapp.domain.usecase

import com.example.musicapp.core.datastore.UserDataStore
import com.example.musicapp.data.repository.AuthRepositoryInterface
import com.example.musicapp.data.model.dto.*
import kotlinx.coroutines.flow.Flow

class RegisterUseCase(private val repository: AuthRepositoryInterface) {
    suspend operator fun invoke(request: RegisterRequest): RegisterResponse {
        return repository.register(request)
    }
}

class LoginUseCase(private val repository: AuthRepositoryInterface,
                   private val userDataStore: UserDataStore) {
    suspend operator fun invoke(request: LoginRequest): LoginResponse {
        val dto = repository.login(request)
        userDataStore.saveToken(dto.token)
        return repository.login(request)
    }
    fun getTokenFlow(): Flow<String?> = userDataStore.tokenFLow
}

class GetCurrentUserUseCase(private val repository: AuthRepositoryInterface) {
    suspend operator fun invoke(token: String): MeResponse {
        return repository.getCurrentUser(token)
    }
}
