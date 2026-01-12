package com.example.musicapp.data.model.dto

// Gửi lên server
data class LoginRequest(
    val email: String,
    val pass: String
)

// Nhận về từ server
data class LoginResponse(
    val accessToken: String,
    val userId: String,
    val name: String
)