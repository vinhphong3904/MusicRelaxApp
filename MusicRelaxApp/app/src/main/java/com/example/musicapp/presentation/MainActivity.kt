package com.example.musicapp.presentation

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
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
import com.example.musicapp.data.service.GlobalAppState
import com.example.musicapp.core.player.MusicService
import com.example.musicapp.data.model.SongDto
import com.example.musicapp.data.api.ApiClient
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.Normalizer
import java.util.regex.Pattern

class MainActivity : ComponentActivity() {
    
    private val SERVER_IP = "192.168.0.103" 

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MusicPlayerManager.getOrCreatePlayer(this)

        setContent {
            val context = LocalContext.current
            val scope = rememberCoroutineScope()
            val snackbarHostState = remember { SnackbarHostState() }
            val navController = rememberNavController()
            val authViewModel: AuthViewModel = viewModel()
            val uiState by authViewModel.uiState.collectAsState()

            val launcher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                if (!isGranted) Log.e("MusicApp", "Quyền thông báo bị từ chối")
            }

            // ĐỒNG BỘ BAN ĐẦU
            LaunchedEffect(uiState) {
                if (uiState is AuthUiState.LoggedIn || uiState is AuthUiState.Success) {
                    try {
                        val response = ApiClient.musicApi.getFavorites()
                        if (response.success) {
                            GlobalAppState.updateFavorites(response.data.map { it.song_id })
                        }
                    } catch (e: Exception) { e.printStackTrace() }
                }
            }

            LaunchedEffect(Unit) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                        launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    }
                }
            }

            val isPlaying by MusicPlayerManager.isPlaying.collectAsState()
            val progress by MusicPlayerManager.progress.collectAsState()
            var currentPlayingSong by remember { mutableStateOf<SongDto?>(null) }

            // Quan sát trực tiếp từ danh sách ID
            val isFavorite = currentPlayingSong?.let { GlobalAppState.isFavorite(it.id) } ?: false

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
                snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
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
                    currentPlayingSong = currentPlayingSong,
                    isPlaying = isPlaying,
                    progress = progress,
                    isFavorite = isFavorite,
                    onFavoriteToggle = { shouldFavorite ->
                        val songId = currentPlayingSong?.id ?: return@NavGraph
                        
                        // 1. CẬP NHẬT UI TRƯỚC (OPTIMISTIC)
                        GlobalAppState.toggleLocal(songId, shouldFavorite)
                        
                        scope.launch {
                            try {
                                if (shouldFavorite) {
                                    ApiClient.musicApi.addFavorite(mapOf("songId" to songId))
                                    snackbarHostState.showSnackbar("Đã thêm vào mục yêu thích")
                                } else {
                                    ApiClient.musicApi.removeFavorite(songId)
                                    snackbarHostState.showSnackbar("Đã xóa khỏi mục yêu thích")
                                }
                            } catch (e: Exception) {
                                // 2. HOÀN TÁC NẾU LỖI (ROLLBACK)
                                GlobalAppState.toggleLocal(songId, !shouldFavorite)
                                snackbarHostState.showSnackbar("Lỗi: Không thể cập nhật server")
                            }
                        }
                    },
                    onSongSelect = { song ->
                        currentPlayingSong = song
                        val songFileName = removeAccent(song.title.lowercase()).replace(" ", "-").replace(Regex("[^a-z0-9-]") , "")
                        val songUrl = "http://$SERVER_IP:8080/play/$songFileName.mp3"
                        MusicPlayerManager.play(context, songUrl, song.title, "Music Relax")
                        navController.navigate(Screen.Player.route)
                    },
                    onPlayPauseChange = { MusicPlayerManager.togglePlayPause() },
                    onProgressChange = { MusicPlayerManager.seekTo(it) },
                    onNextPrev = { }
                )
            }
        }
    }

    private fun removeAccent(s: String): String {
        val temp = Normalizer.normalize(s, Normalizer.Form.NFD)
        val pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+")
        return pattern.matcher(temp).replaceAll("").replace('đ', 'd').replace('Đ', 'D')
    }
}
