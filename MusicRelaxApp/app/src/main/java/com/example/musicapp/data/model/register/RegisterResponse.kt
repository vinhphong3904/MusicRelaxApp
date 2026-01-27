package com.example.musicapp.data.model.register

import com.example.musicapp.data.model.UserDto

data class RegisterResponse(
    val success: Boolean,
    val message: String,
    val user: UserDto
)
