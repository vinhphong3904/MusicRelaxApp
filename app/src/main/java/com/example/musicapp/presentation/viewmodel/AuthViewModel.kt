package com.example.musicapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicapp.core.datastore.UserDataStore
import com.example.musicapp.data.model.dto.LoginRequest
import com.example.musicapp.data.model.dto.LoginResponse
import com.example.musicapp.data.model.dto.MeResponse
import com.example.musicapp.data.model.dto.RegisterRequest
import com.example.musicapp.data.model.dto.RegisterResponse
import com.example.musicapp.domain.usecase.CheckLoginStateUseCase
import com.example.musicapp.domain.usecase.GetCurrentUserUseCase
import com.example.musicapp.domain.usecase.LoginUseCase
import com.example.musicapp.domain.usecase.RegisterUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val registerUseCase: RegisterUseCase,
    private val loginUseCase: LoginUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val checkLoginStateUseCase: CheckLoginStateUseCase,
    private val userDataStore: UserDataStore
) : ViewModel() {

    private val _registerState = MutableStateFlow<RegisterResponse?>(null)
    val registerState: StateFlow<RegisterResponse?> = _registerState

    private val _loginState = MutableStateFlow<LoginResponse?>(null)
    val loginState: StateFlow<LoginResponse?> = _loginState

    private val _meState = MutableStateFlow<MeResponse?>(null)
    val meState: StateFlow<MeResponse?> = _meState

    /** NULL = đang check, true/false = kết quả */
    private val _isLoggedIn = MutableStateFlow<Boolean?>(null)
    val isLoggedIn: StateFlow<Boolean?> = _isLoggedIn

    /** expose token nếu UI / interceptor cần */
    val tokenFlow: Flow<String?> = userDataStore.tokenFlow

    /** Splash gọi */
    fun checkLogin() {
        viewModelScope.launch {
            _isLoggedIn.value = checkLoginStateUseCase()
        }
    }

    fun register(
        username: String,
        email: String,
        password: String,
        confirmPassword: String
    ) {
        viewModelScope.launch {
            _registerState.value =
                registerUseCase(RegisterRequest(username, email, password, confirmPassword))
        }
    }

    fun login(username: String, password: String) {
        viewModelScope.launch {
            val response = loginUseCase(LoginRequest(username, password))

            if (response.success && !response.token.isNullOrEmpty()) {
                userDataStore.saveToken(response.token!!)
                _isLoggedIn.value = true
            } else {
                _isLoggedIn.value = false
            }

            _loginState.value = response
        }
    }

    fun getCurrentUser() {
        viewModelScope.launch {
            val token = userDataStore.tokenFlow.first()
            if (!token.isNullOrEmpty()) {
                _meState.value = getCurrentUserUseCase(token)
            }
        }
    }
}

