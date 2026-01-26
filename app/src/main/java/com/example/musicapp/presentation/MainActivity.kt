package com.example.musicapp.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.musicapp.R
import com.example.musicapp.presentation.navigation.NavGraph
import com.example.musicapp.presentation.navigation.Screen
import kotlinx.coroutines.delay
import dagger.hilt.android.AndroidEntryPoint

import com.example.musicapp.presentation.components.MusicBottomNavigation
import com.example.musicapp.presentation.components.MiniPlayer

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route

            // DANH SÁCH BÀI HÁT MỞ RỘNG (20 BÀI)
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

            Scaffold(
                containerColor = Color.Black,
                bottomBar = {
                    val hideBottomBar = currentRoute == Screen.Login.route || 
                                      currentRoute == Screen.Register.route || 
                                      currentRoute == Screen.Player.route
                    
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
                NavGraph(
                    navController = navController,
                    modifier = Modifier.padding(bottom = innerPadding.calculateBottomPadding()),
                    startDestination = Screen.Login.route,
                    currentPlayingSong = currentPlayingSong,
                    isPlaying = isPlaying,
                    progress = progress,
                    onSongSelect = { song ->
                        val index = playlist.indexOf(song)
                        if (index != -1) {
                            currentSongIndex = index
                            progress = 0f
                            isPlaying = true
                        }
                    },
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
