package com.example.musicapp.data.model.dto

import com.google.gson.annotations.SerializedName
import java.time.Instant

///**
// * DTO (Data Transfer Object) cho User API response
// * Map trực tiếp với JSON trả về từ server
// *
// * VD JSON:
// * {
// *   "id": "user123",
// *   "user_name": "john_doe",
// *   "email": "john@example.com",
// *   "access_token": "eyJhbGc..."
// * }
// */
data class UserDto(
    val id: Int,
    val username: String,
    val email: String,
    val passwordHash: String,
    val role: String = "user",
    val status: Boolean = true,
    val avatarUrl: String? = null,
    val lastLogin: Instant? = null,
    val createdAt: Instant,
    val updatedAt: Instant? = null,
    val deletedAt: Instant? = null
)