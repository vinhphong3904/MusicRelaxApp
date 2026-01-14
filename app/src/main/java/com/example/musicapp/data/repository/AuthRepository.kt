package com.example.musicapp.data.repository

import com.example.musicapp.data.remote.AuthRemoteDataSource
import com.example.musicapp.data.model.dto.*

interface AuthRepositoryInterface {
    suspend fun register(request: RegisterRequest): RegisterResponse
    suspend fun login(request: LoginRequest): LoginResponse
    suspend fun getCurrentUser(token: String): MeResponse
}

class AuthRepository(
    private val remoteDataSource: AuthRemoteDataSource
) : AuthRepositoryInterface {
    override suspend fun register(request: RegisterRequest): RegisterResponse {
        return remoteDataSource.register(request)
    }

    override suspend fun login(request: LoginRequest): LoginResponse {
        return remoteDataSource.login(request)
    }

    override suspend fun getCurrentUser(token: String): MeResponse {
        return remoteDataSource.getCurrentUser(token)
    }
}
