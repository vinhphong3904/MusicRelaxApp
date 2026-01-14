package com.example.musicapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicapp.data.model.dto.LoginRequest
import com.example.musicapp.data.model.dto.LoginResponse
import com.example.musicapp.data.model.dto.MeResponse
import com.example.musicapp.data.model.dto.RegisterRequest
import com.example.musicapp.data.model.dto.RegisterResponse
import com.example.musicapp.domain.usecase.GetCurrentUserUseCase
import com.example.musicapp.domain.usecase.LoginUseCase
import com.example.musicapp.domain.usecase.RegisterUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
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