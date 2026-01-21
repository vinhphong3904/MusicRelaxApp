package com.example.musicapp.data.model.register

import com.example.musicapp.data.model.UserModel

data class RegisterResponse(
    val success: Boolean,
    val message: String,
    val user: UserModel
)
