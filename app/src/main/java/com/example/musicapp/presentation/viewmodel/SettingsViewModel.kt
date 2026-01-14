package com.example.musicapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicapp.domain.usecase.GetSettingsUseCase
import com.example.musicapp.domain.usecase.UpdateSettingsUseCase
import com.example.musicapp.data.model.dto.SettingsDto
import com.example.musicapp.data.model.dto.UpdateSettingsRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val getSettingsUseCase: GetSettingsUseCase,
    private val updateSettingsUseCase: UpdateSettingsUseCase
) : ViewModel() {

    private val _settings = MutableStateFlow<SettingsDto?>(null)
    val settings: StateFlow<SettingsDto?> = _settings

    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message

    fun loadSettings(token: String) {
        viewModelScope.launch {
            _settings.value = getSettingsUseCase(token)
        }
    }

    fun updateSettings(token: String, request: UpdateSettingsRequest) {
        viewModelScope.launch {
            _message.value = updateSettingsUseCase(token, request)
        }
    }
}
