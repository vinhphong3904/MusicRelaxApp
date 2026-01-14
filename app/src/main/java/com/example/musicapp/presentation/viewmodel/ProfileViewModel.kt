package com.example.musicapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicapp.domain.usecase.*
import com.example.musicapp.data.model.dto.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getProfileUseCase: GetProfileUseCase,
    private val updateProfileUseCase: UpdateProfileUseCase,
    private val uploadAvatarUseCase: UploadAvatarUseCase,
    private val resetAvatarUseCase: ResetAvatarUseCase
) : ViewModel() {

    private val _profileState = MutableStateFlow<ProfileResponse?>(null)
    val profileState: StateFlow<ProfileResponse?> = _profileState

    private val _updateState = MutableStateFlow<UpdateProfileResponse?>(null)
    val updateState: StateFlow<UpdateProfileResponse?> = _updateState

    private val _avatarState = MutableStateFlow<AvatarResponse?>(null)
    val avatarState: StateFlow<AvatarResponse?> = _avatarState

    fun loadProfile(token: String) {
        viewModelScope.launch {
            _profileState.value = getProfileUseCase(token)
        }
    }

    fun updateProfile(token: String, request: UpdateProfileRequest) {
        viewModelScope.launch {
            _updateState.value = updateProfileUseCase(token, request)
        }
    }

    fun uploadAvatar(token: String, avatarPart: MultipartBody.Part) {
        viewModelScope.launch {
            _avatarState.value = uploadAvatarUseCase(token, avatarPart)
        }
    }

    fun resetAvatar(token: String) {
        viewModelScope.launch {
            _avatarState.value = resetAvatarUseCase(token)
        }
    }
}
