package com.example.musicapp.presentation.auth

import com.example.musicapp.data.model.UserModel

sealed class AuthUiState {
    object Idle : AuthUiState()
    object Loading : AuthUiState()

    data class Success(
        val user: UserModel,
        val from: AuthAction   // login hay register
    ) : AuthUiState()

    data class Error(
        val message: String
    ) : AuthUiState()
}

enum class AuthAction {
    LOGIN, REGISTER
}

