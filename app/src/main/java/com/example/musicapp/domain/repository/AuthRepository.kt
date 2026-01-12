package com.example.musicapp.domain.repository

import com.example.musicapp.core.common.Resource
import com.example.musicapp.domain.model.User

/**
 * Repository Interface cho Authentication
 */
interface AuthRepository {
    
    /**
     * Đăng nhập
     * 
     * @param email: Email
     * @param password: Password
     * @return User nếu thành công
     * @throws Exception nếu fail
     */
    suspend fun login(email: String, pass: String): Resource<Unit>
    
    /**
     * Đăng ký
     * 
     * @param userName: Tên user
     * @param email: Email
     * @param password: Password
     * @return User mới tạo
     * @throws Exception nếu fail
     */
    //suspend fun register(userName: String, email: String, password: String): User
    
    /**
     * Đăng xuất
     */
    //suspend fun logout()
    
    /**
     * Check user đã login chưa
     * 
     * @return true nếu có token
     */
    //fun isLoggedIn(): Boolean
    
    /**
     * Lấy thông tin user hiện tại
     * 
     * @return User hoặc null
     */
    //suspend fun getCurrentUser(): User?
}