package com.example.musicapp.presentation.home

import com.example.musicapp.data.model.UserDto
import com.example.musicapp.data.model.home.HomeSongDto

data class HomeUiState(
    val isLoading: Boolean = false,
    val topSongs: List<HomeSongDto> = emptyList(),
    val recommendSongs: List<HomeSongDto> = emptyList(),
    val user: UserDto? = null,
    val error: String? = null,
    val userName: String = "Khách",
    val showEditDialog: Boolean = false
){
    val userInitial: String
        get() = user?.fullName
            ?.takeIf { it.isNotBlank() }
            ?.take(1)
            ?.uppercase()
            ?: user?.username?.take(1)?.uppercase()
            ?: "U"

    val isLoggedIn: Boolean
        get() = user != null
}
