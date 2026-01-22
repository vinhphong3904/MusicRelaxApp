package com.example.musicapp.data.model.home

import com.example.musicapp.data.model.UserDto

data class MeResponse(
    val success: Boolean,
    val user: UserDto
)