package com.example.musicapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicapp.core.datastore.UserDataStore
import com.example.musicapp.data.model.dto.LoginRequest
import com.example.musicapp.data.model.dto.LoginResponse
import com.example.musicapp.data.model.dto.MeResponse
import com.example.musicapp.data.model.dto.RegisterRequest
import com.example.musicapp.data.model.dto.RegisterResponse
import com.example.musicapp.domain.usecase.GetCurrentUserUseCase
import com.example.musicapp.domain.usecase.LoginUseCase
import com.example.musicapp.domain.usecase.RegisterUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
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

    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn

    // quan sát token từ UseCase
    val tokenFlow: Flow<String?> = loginUseCase.getTokenFlow()

    fun register(username: String, email: String, password: String, confirmPassword: String) {
        viewModelScope.launch {
            val response = registerUseCase(RegisterRequest(username, email, password, confirmPassword))
            _registerState.value = response
        }
    }

    fun login(username: String, password: String) {
        viewModelScope.launch {
            val response = loginUseCase(LoginRequest(username, password))
            _loginState.value = response
        }
    }

    fun getCurrentUser() {
        viewModelScope.launch {
            // lấy token từ flow
            val token = tokenFlow.first()
            if (token != null) {
                val response = getCurrentUserUseCase(token)
                _meState.value = response
            }
        }
    }
}
