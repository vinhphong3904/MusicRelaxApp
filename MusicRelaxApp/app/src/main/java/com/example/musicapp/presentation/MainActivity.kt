package com.example.musicapp.presentation

import android.os.Bundle
import android.util.Log
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
import com.example.musicapp.presentation.components.MiniPlayer
import com.example.musicapp.presentation.components.MusicBottomNavigation
import com.example.musicapp.presentation.auth.AuthUiState
import com.example.musicapp.presentation.auth.AuthViewModel
import com.example.musicapp.data.service.MusicPlayerManager
import com.example.musicapp.data.model.SongDto
import kotlinx.coroutines.delay
import java.text.Normalizer
import java.util.regex.Pattern

class MainActivity : ComponentActivity() {
    
    // IP MÁY TÍNH CỦA BẠN (Đảm bảo điện thoại dùng chung Wifi)
    private val SERVER_IP = "192.168.0.103" 

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MusicPlayerManager.init(this)

        setContent {
            val navController = rememberNavController()
            val authViewModel: AuthViewModel = viewModel()
            val uiState by authViewModel.uiState.collectAsState()

            val isPlaying by MusicPlayerManager.isPlaying.collectAsState()
            val progress by MusicPlayerManager.progress.collectAsState()
            
            var currentPlayingSong by remember { mutableStateOf<SongDto?>(null) }

            // Cập nhật progress từ ExoPlayer mỗi giây
            LaunchedEffect(isPlaying) {
                while (isPlaying) {
                    MusicPlayerManager.updateProgress()
                    delay(1000)
                }
            }

            LaunchedEffect(uiState) {
                when (uiState) {
                    is AuthUiState.LoggedIn, is AuthUiState.Success -> {
                        val currentRoute = navController.currentBackStackEntry?.destination?.route
                        if (currentRoute == Screen.Login.route || currentRoute == Screen.Register.route || currentRoute == null) {
                            navController.navigate(Screen.Home.route) { popUpTo(0) }
                        }
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
                                        songTitle = song.title,
                                        artistName = "Nghệ sĩ #${song.artist_id}",
                                        imageRes = R.drawable.icon,
                                        isPlaying = isPlaying,
                                        progress = progress,
                                        onPlayPauseClick = { MusicPlayerManager.togglePlayPause() },
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
                    startDestination = Screen.Login.route,
                    currentPlayingSong = currentPlayingSong?.let { Triple(it.title, "Nghệ sĩ #${it.artist_id}", R.drawable.icon) },
                    isPlaying = isPlaying,
                    progress = progress,
                    onSongSelect = { triple ->
                        // 1. Cập nhật bài hát đang phát
                        currentPlayingSong = SongDto(
                            id = 0, title = triple.first, artist_id = 0, album_id = null, genre_id = null,
                            duration_seconds = 0, audio_url = "", cover_image_url = "", view_count = "0", slug = "", created_at = null
                        )

                        // 2. Chuyển tên bài hát thành Slug không dấu (Ví dụ: "Nơi này có anh" -> "noi-nay-co-anh")
                        val songFileName = removeAccent(triple.first.lowercase())
                            .replace(" ", "-")
                            .replace(Regex("[^a-z0-9-]"), "")
                        
                        val songUrl = "http://$SERVER_IP:8080/play/$songFileName.mp3"
                        Log.d("MusicApp", "Playing URL: $songUrl")
                        
                        // 3. Phát nhạc và chuyển màn hình
                        MusicPlayerManager.play(songUrl)
                        navController.navigate(Screen.Player.route)
                    },
                    onPlayPauseChange = { MusicPlayerManager.togglePlayPause() },
                    onProgressChange = { MusicPlayerManager.seekTo(it) },
                    onNextPrev = { }
                )
            }
        }
    }

    // Hàm hỗ trợ loại bỏ dấu tiếng Việt
    private fun removeAccent(s: String): String {
        val temp = Normalizer.normalize(s, Normalizer.Form.NFD)
        val pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+")
        return pattern.matcher(temp).replaceAll("").replace('đ', 'd').replace('Đ', 'D')
    }
}
