package com.example.musicapp.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
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
import com.example.musicapp.presentation.navigation.NavGraph
import com.example.musicapp.presentation.navigation.Screen
import com.example.musicapp.presentation.components.MusicBottomNavigation
import com.example.musicapp.presentation.components.MiniPlayer
import com.example.musicapp.presentation.viewmodel.PlayerViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val playerViewModel: PlayerViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val navController = rememberNavController()
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route

            val currentSong by playerViewModel.currentSong.collectAsState()
            val isPlaying by playerViewModel.isPlaying.collectAsState()

            LaunchedEffect(Unit) {
                playerViewModel.loadSongs()
            }

            Scaffold(
                containerColor = Color.Black,
                bottomBar = {
                    val hideBottomBar =
                        currentRoute == Screen.Login.route ||
                                currentRoute == Screen.Register.route ||
                                currentRoute == Screen.Player.route

                    if (!hideBottomBar) {
                        Column {

                            // ===== MINI PLAYER =====
                            AnimatedVisibility(
                                visible = currentSong != null,
                                enter = slideInVertically(
                                    initialOffsetY = { it },
                                    animationSpec = tween(400)
                                ) + fadeIn(),
                                exit = slideOutVertically(
                                    targetOffsetY = { it },
                                    animationSpec = tween(400)
                                ) + fadeOut()
                            ) {
                                currentSong?.let { song ->
                                    MiniPlayer(
                                        songTitle = song.title,
                                        artistName = song.artistName,
                                        imageRes = com.example.musicapp.R.drawable.icon, // ảnh local
                                        isPlaying = isPlaying,
                                        onPlayPauseClick = {
                                            playerViewModel.togglePlayPause()
                                        },
                                        onPreviousClick = {
                                            playerViewModel.skipToPrevious()
                                        },
                                        onNextClick = {
                                            playerViewModel.skipToNext()
                                        },
                                        onExpand = {
                                            navController.navigate(Screen.Player.route)
                                        }
                                    )
                                }
                            }

                            // ===== BOTTOM NAV =====
                            MusicBottomNavigation(navController)
                        }
                    }
                }
            ) { innerPadding ->
                NavGraph(
                    navController = navController,
                    modifier = Modifier.padding(
                        bottom = innerPadding.calculateBottomPadding()
                    ),
                    startDestination = Screen.Login.route,
                    playerViewModel = playerViewModel
                )
            }
        }
    }
}