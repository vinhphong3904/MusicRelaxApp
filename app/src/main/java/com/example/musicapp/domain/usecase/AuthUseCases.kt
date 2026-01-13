package com.example.musicapp.domain.usecase

import com.example.musicapp.data.repository.AuthRepositoryInterface
import com.example.musicapp.data.model.dto.*

class RegisterUseCase(private val repository: AuthRepositoryInterface) {
    suspend operator fun invoke(request: RegisterRequest): RegisterResponse {
        return repository.register(request)
    }
}

class LoginUseCase(private val repository: AuthRepositoryInterface) {
    suspend operator fun invoke(request: LoginRequest): LoginResponse {
        return repository.login(request)
    }
}

class GetCurrentUserUseCase(private val repository: AuthRepositoryInterface) {
    suspend operator fun invoke(token: String): MeResponse {
        return repository.getCurrentUser(token)
    }
}
