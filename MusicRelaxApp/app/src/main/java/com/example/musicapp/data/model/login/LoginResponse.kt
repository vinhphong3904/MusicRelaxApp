package com.example.musicapp.data.model.login

import com.example.musicapp.data.model.UserDto

data class LoginResponse(
    val success: Boolean,
    val message: String,
    val user: UserDto,
    val token: String
)
