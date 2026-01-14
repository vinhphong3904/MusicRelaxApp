package com.example.musicapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicapp.domain.model.Genre
import com.example.musicapp.domain.usecase.GetGenresUseCase
import com.example.musicapp.domain.usecase.GetGenreDetailUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GenresViewModel @Inject constructor(
    private val getGenresUseCase: GetGenresUseCase,
    private val getGenreDetailUseCase: GetGenreDetailUseCase
) : ViewModel() {

    private val _genres = MutableStateFlow<List<Genre>>(emptyList())
    val genres: StateFlow<List<Genre>> = _genres

    private val _genreDetail = MutableStateFlow<Genre?>(null)
    val genreDetail: StateFlow<Genre?> = _genreDetail

    fun loadGenres(token: String) {
        viewModelScope.launch {
            _genres.value = getGenresUseCase(token)
        }
    }

    fun loadGenreDetail(token: String, id: Int) {
        viewModelScope.launch {
            _genreDetail.value = getGenreDetailUseCase(token, id)
        }
    }
}
