package com.example.musicapp.data.model.dto

data class RegisterRequest(
    val username: String,
    val email: String,
    val password: String,
    val full_name: String
)

data class LoginRequest(
    val username: String,
    val password: String
)

data class UserDto(
    val id: Int,
    val username: String,
    val email: String,
    val full_name: String?,
    val avatar_url: String?,
    val role: String,
    val status: Boolean? = null,
    val created_at: String? = null,
    val date_of_birth: String? = null,
    val gender: String? = null,
    val phone: String? = null,
    val address: String? = null,
    val bio: String? = null
)

data class RegisterResponse(
    val success: Boolean,
    val message: String,
    val user: UserDto
)

data class LoginResponse(
    val success: Boolean,
    val message: String,
    val user: UserDto,
    val token: String
)

data class MeResponse(
    val success: Boolean,
    val user: UserDto
)
