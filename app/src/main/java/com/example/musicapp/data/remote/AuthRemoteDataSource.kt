package com.example.musicapp.data.remote

import com.example.musicapp.core.network.ApiService
import com.example.musicapp.data.model.dto.*
import javax.inject.Inject

class AuthRemoteDataSource @Inject constructor(private val api: ApiService) {
    suspend fun register(request: RegisterRequest): RegisterResponse {
        return api.register(request)
    }

    suspend fun login(request: LoginRequest): LoginResponse {
        return api.login(request)
    }

    suspend fun getCurrentUser(token: String): MeResponse {
        return api.getCurrentUser("Bearer $token")
    }
}
