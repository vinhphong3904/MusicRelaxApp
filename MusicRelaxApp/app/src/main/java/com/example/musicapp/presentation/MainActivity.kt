package com.example.musicapp.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.musicapp.R
import com.example.musicapp.presentation.navigation.Screen
import com.example.musicapp.presentation.navigation.NavGraph
import kotlinx.coroutines.delay
import com.example.musicapp.presentation.components.MiniPlayer
import com.example.musicapp.presentation.components.MusicBottomNavigation
import com.example.musicapp.presentation.auth.AuthUiState
import com.example.musicapp.presentation.auth.AuthViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val navController = rememberNavController()
            val authViewModel: AuthViewModel = viewModel()
            val uiState by authViewModel.uiState.collectAsState()

            // State cho trình phát nhạc
            var currentPlayingSong by remember { mutableStateOf<Triple<String, String, Int>?>(null) }
            var isPlaying by remember { mutableStateOf(false) }
            var progress by remember { mutableFloatStateOf(0f) }

            LaunchedEffect(isPlaying) {
                while (isPlaying) {
                    delay(1000)
                    if (progress < 1f) progress += 0.01f else progress = 0f
                }
            }

            // Xử lý logic điều hướng khởi đầu dựa trên trạng thái Login
            // Sử dụng LaunchedEffect để điều hướng thực tế thay vì đổi startDestination của NavHost (tránh crash)
            LaunchedEffect(uiState) {
                when (uiState) {
                    is AuthUiState.LoggedIn, is AuthUiState.Success -> {
                        // Nếu đang ở màn hình Login/Register thì mới navigate sang Home
                        val currentRoute = navController.currentBackStackEntry?.destination?.route
                        if (currentRoute == Screen.Login.route || currentRoute == Screen.Register.route || currentRoute == null) {
                            navController.navigate(Screen.Home.route) {
                                popUpTo(0) // Xóa sạch backstack
                            }
                        }
                    }
                    is AuthUiState.Idle -> {
                        // Trạng thái mặc định hoặc sau khi logout
                    }
                    else -> {}
                }
            }

            Scaffold(
                containerColor = Color.Black,
                bottomBar = {
                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    val currentRoute = navBackStackEntry?.destination?.route
                    val hideBottomBar = currentRoute == Screen.Login.route || currentRoute == Screen.Register.route || currentRoute == Screen.Player.route

                    if (!hideBottomBar && currentRoute != null) {
                        Column {
                            AnimatedVisibility(
                                visible = currentPlayingSong != null,
                                enter = slideInVertically(initialOffsetY = { it }, animationSpec = tween(400)) + fadeIn(),
                                exit = slideOutVertically(targetOffsetY = { it }, animationSpec = tween(400)) + fadeOut()
                            ) {
                                currentPlayingSong?.let { song ->
                                    MiniPlayer(
                                        songTitle = song.first,
                                        artistName = song.second,
                                        imageRes = song.third,
                                        isPlaying = isPlaying,
                                        progress = progress,
                                        onPlayPauseClick = { isPlaying = !isPlaying },
                                        onPreviousClick = { },
                                        onNextClick = { },
                                        onExpand = { navController.navigate(Screen.Player.route) }
                                    )
                                }
                            }
                            MusicBottomNavigation(navController)
                        }
                    }
                }
            ) { innerPadding ->
                NavGraph(
                    navController = navController,
                    modifier = Modifier.padding(bottom = innerPadding.calculateBottomPadding()),
                    startDestination = Screen.Login.route, // Luôn bắt đầu từ Login, logic LaunchedEffect sẽ đẩy sang Home nếu đã log in
                    currentPlayingSong = currentPlayingSong,
                    isPlaying = isPlaying,
                    progress = progress,
                    onSongSelect = { selectedSong ->
                        currentPlayingSong = selectedSong
                        progress = 0f
                        isPlaying = true
                        navController.navigate(Screen.Player.route)
                    },
                    onPlayPauseChange = { isPlaying = it },
                    onProgressChange = { progress = it },
                    onNextPrev = { }
                )
            }
        }
    }
}
