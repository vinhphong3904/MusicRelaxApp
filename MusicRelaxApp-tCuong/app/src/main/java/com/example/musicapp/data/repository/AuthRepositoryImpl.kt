package com.example.musicapp.data.repository

import android.content.SharedPreferences
import com.example.musicapp.core.common.Constants
import com.example.musicapp.core.common.Resource
import com.example.musicapp.core.datastore.UserDataStore
import com.example.musicapp.core.network.ApiService
import com.example.musicapp.data.model.dto.LoginRequest
import com.example.musicapp.domain.model.User
import com.example.musicapp.domain.repository.AuthRepository
import javax.inject.Inject

/**
 * Implementation của AuthRepository
 * Quản lý authentication: login, register, token storage
 */
class AuthRepositoryImpl @Inject constructor(
    private val api: ApiService,
    private val userStore: UserDataStore
) : AuthRepository {

    override suspend fun login(email: String, pass: String): Resource<Unit> {
        return try {
            // 1. Gọi API
            val response = api.login(LoginRequest(email, pass))

            // 2. Nếu thành công, lưu token vào máy
            userStore.saveToken(response.accessToken)

            // 3. Trả về Success
            Resource.Success(Unit)
        } catch (e: Exception) {
            // 4. Nếu lỗi trả về Error (cần xử lý HttpException để lấy message từ server)
            Resource.Error(e.localizedMessage ?: "Login failed")
        }
    }
}