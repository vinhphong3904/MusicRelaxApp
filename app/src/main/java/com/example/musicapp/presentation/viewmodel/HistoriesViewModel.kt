package com.example.musicapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicapp.domain.usecase.*
import com.example.musicapp.data.model.dto.HistoryDto
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistoriesViewModel @Inject constructor(
    private val getHistoriesUseCase: GetHistoriesUseCase,
    private val addHistoryUseCase: AddHistoryUseCase
) : ViewModel() {

    private val _histories = MutableStateFlow<List<HistoryDto>>(emptyList())
    val histories: StateFlow<List<HistoryDto>> = _histories

    private val _added = MutableStateFlow<Boolean?>(null)
    val added: StateFlow<Boolean?> = _added

    fun loadHistories(token: String, limit: Int = 50) {
        viewModelScope.launch {
            _histories.value = getHistoriesUseCase(token, limit)
        }
    }

    fun addHistory(token: String, songId: Int, durationPlayed: Int) {
        viewModelScope.launch {
            _added.value = addHistoryUseCase(token, AddHistoryRequest(songId, durationPlayed))
        }
    }
}
