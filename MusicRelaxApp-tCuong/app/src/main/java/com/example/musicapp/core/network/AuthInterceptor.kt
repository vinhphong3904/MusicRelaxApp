package com.example.musicapp.core.network

import android.content.SharedPreferences
import com.example.musicapp.core.common.Constants
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

/**
 * Interceptor tự động thêm JWT token vào header
 * Mọi request sẽ tự động có: Authorization: Bearer {token}
 */
class AuthInterceptor @Inject constructor(
    private val sharedPreferences: SharedPreferences
) : Interceptor {
    
    /**
     * Chặn mọi request, thêm token nếu có
     * 
     * Flow:
     * 1. Lấy token từ SharedPreferences
     * 2. Nếu có token → thêm vào header
     * 3. Proceed request
     */
    override fun intercept(chain: Interceptor.Chain): Response {
        // Lấy request gốc
        val originalRequest = chain.request()
        
        // Lấy token từ SharedPreferences
        val token = sharedPreferences.getString(Constants.PREF_AUTH_TOKEN, null)
        
        // Nếu không có token → send request bình thường
        if (token.isNullOrEmpty()) {
            return chain.proceed(originalRequest)
        }
        
        // Tạo request mới với header Authorization
        val newRequest = originalRequest.newBuilder()
            .header("Authorization", "Bearer $token")
            .build()
        
        // Proceed với request đã thêm token
        return chain.proceed(newRequest)
    }
}