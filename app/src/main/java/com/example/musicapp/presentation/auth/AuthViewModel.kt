package com.example.musicapp.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicapp.domain.usecase.*
import com.example.musicapp.data.model.dto.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val registerUseCase: RegisterUseCase,
    private val loginUseCase: LoginUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase
) : ViewModel() {

    private val _registerState = MutableStateFlow<RegisterResponse?>(null)
    val registerState: StateFlow<RegisterResponse?> = _registerState

    private val _loginState = MutableStateFlow<LoginResponse?>(null)
    val loginState: StateFlow<LoginResponse?> = _loginState

    private val _meState = MutableStateFlow<MeResponse?>(null)
    val meState: StateFlow<MeResponse?> = _meState

    fun register(username: String, email: String, password: String, fullName: String) {
        viewModelScope.launch {
            val response = registerUseCase(RegisterRequest(username, email, password, fullName))
            _registerState.value = response
        }
    }

    fun login(username: String, password: String) {
        viewModelScope.launch {
            val response = loginUseCase(LoginRequest(username, password))
            _loginState.value = response
        }
    }

    fun getCurrentUser(token: String) {
        viewModelScope.launch {
            val response = getCurrentUserUseCase(token)
            _meState.value = response
        }
    }
}
