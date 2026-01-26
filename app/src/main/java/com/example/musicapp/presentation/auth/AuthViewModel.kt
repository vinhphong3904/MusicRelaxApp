package com.example.musicapp.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicapp.data.api.ApiClient
import com.example.musicapp.data.model.login.LoginRequest
import com.example.musicapp.data.model.register.RegisterRequest
import com.example.musicapp.data.service.TokenManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> = _uiState

    init {
        checkLogin()
    }

    fun checkLogin() {
        if (TokenManager.getToken() != null) {
            _uiState.value = AuthUiState.LoggedIn
        } else {
            _uiState.value = AuthUiState.Idle
        }
    }

    fun login(username: String, password: String) {
        authLogin(username, password)
    }

    fun register(
        username: String,
        email: String,
        password: String,
        confirmPassword: String
    ) {
        authRegister(username, email, password, confirmPassword)
    }

    private fun authLogin(username: String, password: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            try {
                val res = ApiClient.musicApi.login(
                    LoginRequest(username, password)
                )

                if (res.success) {
                    // LƯU TOKEN ĐÚNG CHỖ
                    TokenManager.saveToken(res.token)

                    _uiState.value = AuthUiState.Success(
                        res.user,
                        AuthAction.LOGIN
                    )
                } else {
                    _uiState.value = AuthUiState.Error(res.message)
                }

            } catch (e: Exception) {
                _uiState.value = AuthUiState.Error(
                    e.message ?: "Login thất bại"
                )
            }
        }
    }

    private fun authRegister(
        username: String,
        email: String,
        password: String,
        confirmPassword: String
    ) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            try {
                val res = ApiClient.musicApi.register(
                    RegisterRequest(username, email, password, confirmPassword)
                )

                if (res.success) {
                    _uiState.value = AuthUiState.Success(
                        res.user,
                        AuthAction.REGISTER
                    )
                } else {
                    _uiState.value = AuthUiState.Error(res.message)
                }

            } catch (e: Exception) {
                _uiState.value = AuthUiState.Error(
                    e.message ?: "Register thất bại"
                )
            }
        }
    }
}


