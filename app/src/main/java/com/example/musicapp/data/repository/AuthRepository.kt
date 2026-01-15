package com.example.musicapp.data.repository

import com.example.musicapp.core.datastore.UserDataStore
import com.example.musicapp.data.remote.AuthRemoteDataSource
import com.example.musicapp.data.model.dto.*
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.StateFlow


interface AuthRepositoryInterface {

    val tokenFlow: StateFlow<String?>

    suspend fun saveToken(token: String)
    suspend fun clearToken()

    suspend fun register(request: RegisterRequest): RegisterResponse
    suspend fun login(request: LoginRequest): LoginResponse
    suspend fun getCurrentUser(token: String): MeResponse
}


@Singleton
class AuthRepository @Inject constructor(
    private val remoteDataSource: AuthRemoteDataSource,
    private val userDataStore: UserDataStore
) : AuthRepositoryInterface {

    override val tokenFlow = userDataStore.tokenFlow

    override suspend fun saveToken(token: String) {
        userDataStore.saveToken(token)
    }

    override suspend fun clearToken() {
        userDataStore.clearToken()
    }

    override suspend fun register(request: RegisterRequest): RegisterResponse {
        return remoteDataSource.register(request)
    }

    override suspend fun login(request: LoginRequest): LoginResponse {
        val response = remoteDataSource.login(request)
        saveToken(response.token)
        return response
    }

    override suspend fun getCurrentUser(token: String): MeResponse {
        return remoteDataSource.getCurrentUser(token)
    }
}

