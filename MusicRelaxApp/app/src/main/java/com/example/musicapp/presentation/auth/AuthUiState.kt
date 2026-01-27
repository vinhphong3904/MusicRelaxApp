package com.example.musicapp.presentation.auth

import com.example.musicapp.data.model.UserDto

sealed class AuthUiState {
    object Idle : AuthUiState()
    object Loading : AuthUiState()
    object LoggedIn : AuthUiState()
    data class Success(
        val user: UserDto,
        val action: AuthAction   // login hay register
    ) : AuthUiState()

    data class Error(
        val message: String
    ) : AuthUiState()
}

enum class AuthAction {
    LOGIN, REGISTER
}

