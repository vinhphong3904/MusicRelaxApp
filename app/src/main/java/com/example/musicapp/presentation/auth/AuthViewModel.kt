package com.example.musicapp.presentation.auth

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicapp.core.common.Resource
import com.example.musicapp.core.common.UiState
import com.example.musicapp.domain.model.User
import com.example.musicapp.domain.repository.AuthRepository
import com.example.musicapp.domain.usecase.LoginUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

/**
 * ViewModel cho Auth screens (Login, Register)
 *
 * Nhiệm vụ:
 * - Gọi AuthRepository để login / register
 * - Quản lý UI state (Loading, Success, Error)
 * - Validate input trước khi gọi API
 *
 * @HiltViewModel: Hilt tự động inject dependencies
 */
@HiltViewModel
class AuthViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase
) : ViewModel() {

    // State cho các ô nhập liệu
    var email by mutableStateOf("")
    var password by mutableStateOf("")

    // State cho UI (Loading, Error, Success)
    private val _loginState = MutableSharedFlow<Resource<Unit>>()
    val loginState = _loginState.asSharedFlow()

    fun login() {
        viewModelScope.launch {
            _loginState.emit(Resource.Loading()) // Bắt đầu xoay loading
            val result = loginUseCase(email, password)
            _loginState.emit(result) // Bắn kết quả ra UI
        }
    }
}