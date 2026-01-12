//package com.example.musicapp.presentation.home
//
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.lazy.items
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.Search
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.unit.dp
//import androidx.hilt.navigation.compose.hiltViewModel
//import androidx.lifecycle.compose.collectAsStateWithLifecycle
//import com.example.musicapp.domain.model.Song
//import com.example.musicapp.presentation.components.SongItem
//
///**
// * Home Screen - Màn hình chính
// *
// * Hiển thị:
// * - Search bar
// * - Danh sách bài hát
// * - Danh sách playlists
// *
// * @param onSongClick: Callback khi click vào bài hát → play nhạc
// * @param onPlaylistClick: Callback khi click playlist
// * @param viewModel: Hilt auto inject
// */
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun HomeScreen(
//
//)