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
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.musicapp.R
import com.example.musicapp.presentation.navigation.Screen
import kotlinx.coroutines.delay
import androidx.navigation.compose.composable
import com.example.musicapp.presentation.components.MiniPlayer
import com.example.musicapp.presentation.components.MusicBottomNavigation
import com.example.musicapp.presentation.auth.AuthUiState
import com.example.musicapp.presentation.auth.AuthViewModel
import com.example.musicapp.presentation.auth.LoginScreen
import com.example.musicapp.presentation.home.HomeScreen
import com.example.musicapp.presentation.home.HomeViewModel
import com.example.musicapp.presentation.player.PlayerScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val navController = rememberNavController()
            val authViewModel: AuthViewModel = viewModel()
            val uiState by authViewModel.uiState.collectAsState()

            // check login 1 lần duy nhất
            LaunchedEffect(Unit) {
                authViewModel.checkLogin()
            }

            // Playlist giữ nguyên
            val playlist = listOf(
                Triple("Đừng Làm Trái Tim Anh Đau", "Sơn Tùng M-TP", R.drawable.icon),
                Triple("Chúng Ta Của Tương Lai", "Sơn Tùng M-TP", R.drawable.tieude),
                Triple("Thiên Lý Ơi", "Jack - J97", R.drawable.nen),
                Triple("Giá Như", "SOOBIN", R.drawable.tieude),
                Triple("Exit Sign", "HIEUTHUHAI", R.drawable.icon),
                Triple("Em Xinh", "MONO", R.drawable.nen),
                Triple("Lệ Lưu Ly", "Vũ Phụng Tiên", R.drawable.tieude),
                Triple("Cắt Đôi Nỗi Sầu", "Tăng Duy Tân", R.drawable.icon),
                Triple("Ngày Mai Người Ta Lấy Chồng", "Anh Tú", R.drawable.nen),
                Triple("Mưa Tháng Sáu", "Văn Mai Hương", R.drawable.tieude),
                Triple("Nơi Này Có Anh", "Sơn Tùng M-TP", R.drawable.icon),
                Triple("Lạc Trôi", "Sơn Tùng M-TP", R.drawable.nen),
                Triple("Sau Lời Từ Khước", "Phan Mạnh Quỳnh", R.drawable.tieude),
                Triple("Thanh Xuân", "Da LAB", R.drawable.icon),
                Triple("Thằng Điên", "JustaTee", R.drawable.nen),
                Triple("Anh Nhà Ở Đâu Thế", "AMEE", R.drawable.tieude),
                Triple("Tòng Phu", "Keyo", R.drawable.icon),
                Triple("See Tình", "Hoàng Thùy Linh", R.drawable.nen),
                Triple("Waiting For You", "MONO", R.drawable.tieude),
                Triple("Khuất Lối", "H-Kray", R.drawable.icon)
            )

            var currentSongIndex by remember { mutableIntStateOf(-1) }
            val currentPlayingSong = if (currentSongIndex != -1) playlist[currentSongIndex] else null
            var isPlaying by remember { mutableStateOf(false) }
            var progress by remember { mutableFloatStateOf(0f) }

            LaunchedEffect(isPlaying) {
                while (isPlaying) {
                    delay(1000)
                    if (progress < 1f) progress += 0.01f else {
                        currentSongIndex = (currentSongIndex + 1) % playlist.size
                        progress = 0f
                    }
                }
            }

            val startDestination = when (uiState) {
                AuthUiState.LoggedIn -> "home"
                AuthUiState.Idle,
                AuthUiState.Loading -> null // chờ
                else -> "login"
            }

            if (startDestination != null) {
                Scaffold(
                    containerColor = Color.Black,
                    bottomBar = {
                        val navBackStackEntry by navController.currentBackStackEntryAsState()
                        val currentRoute = navBackStackEntry?.destination?.route
                        val hideBottomBar = currentRoute == "login" || currentRoute == "register" || currentRoute == "player"

                        if (!hideBottomBar) {
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
                                            onPlayPauseClick = { isPlaying = !isPlaying },
                                            onPreviousClick = {
                                                currentSongIndex = (currentSongIndex - 1 + playlist.size) % playlist.size
                                                progress = 0f
                                                isPlaying = true
                                            },
                                            onNextClick = {
                                                currentSongIndex = (currentSongIndex + 1) % playlist.size
                                                progress = 0f
                                                isPlaying = true
                                            },
                                            onExpand = { navController.navigate(Screen.Player.route) }
                                        )
                                    }
                                }
                                MusicBottomNavigation(navController)
                            }
                        }
                    }
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = startDestination,
                        modifier = Modifier.padding(bottom = innerPadding.calculateBottomPadding())
                    ) {
                        composable("login") {
                            LoginScreen(navController)
                        }
                        composable("home") {
                            val homeViewModel: HomeViewModel = viewModel()
                            HomeScreen(navController, homeViewModel)
                        }
                        composable("player") {
                            PlayerScreen(
                                navController = navController,
                                currentPlayingSong = currentPlayingSong,
                                isPlaying = isPlaying,
                                progress = progress,
                                onPlayPauseChange = { isPlaying = it },
                                onProgressChange = { progress = it },
                                onNextPrev = { offset ->
                                    currentSongIndex = (currentSongIndex + offset + playlist.size) % playlist.size
                                    progress = 0f
                                    isPlaying = true
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
