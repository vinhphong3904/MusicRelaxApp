package com.example.musicapp.presentation.player
//
//import androidx.compose.foundation.layout.*
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.*
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.layout.ContentScale
//import androidx.compose.ui.unit.dp
//import androidx.hilt.navigation.compose.hiltViewModel
//import androidx.lifecycle.compose.collectAsStateWithLifecycle
//import coil.compose.AsyncImage
//
///**
// * Player Screen - Màn hình phát nhạc full
// *
// * Hiển thị:
// * - Cover art lớn
// * - Title + Artist
// * - Slider seek bar
// * - Control buttons (prev, play/pause, next)
// *
// * @param onBackClick: Callback đóng player
// * @param viewModel: Hilt auto inject
// */
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun PlayerScreen(
//    onBackClick: () -> Unit,
//    viewModel: PlayerViewModel = hiltViewModel()
//) {
//    /**
//     * Collect UI state
//     */
//    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
//
//    /**
//     * Local state cho slider (tránh lag khi kéo)
//     */
//    var sliderPosition by remember { mutableStateOf(0f) }
//    var isUserSeeking by remember { mutableStateOf(false) }
//
//    /**
//     * Sync slider với playback position
//     */
//    LaunchedEffect(uiState.currentPosition) {
//        if (!isUserSeeking) {
//            sliderPosition = uiState.currentPosition.toFloat()
//        }
//    }
//
//    Scaffold(
//        topBar = {
//            TopAppBar(
//                title = { Text("Now Playing") },
//                navigationIcon = {
//                    IconButton(onClick = onBackClick) {
//                        Icon(
//                            imageVector = Icons.Default.ArrowBack,
//                            contentDescription = "Back"
//                        )
//                    }
//                }
//            )
//        }
//    ) { paddingValues ->
//
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(paddingValues)
//                .padding(24.dp),
//            horizontalAlignment = Alignment.CenterHorizontally,
//            verticalArrangement = Arrangement.SpaceBetween
//        ) {
//
//            /**
//             * Cover Art
//             */
//            Card(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .aspectRatio(1f),
//                elevation = CardDefaults.cardElevation(8.dp)
//            ) {
//                AsyncImage(
//                    model = uiState.currentSong?.coverUrl ?: "",
//                    contentDescription = "Album Cover",
//                    modifier = Modifier.fillMaxSize(),
//                    contentScale = ContentScale.Crop
//                )
//            }
//
//            /**
//             * Song Info
//             */
//            Column(
//                horizontalAlignment = Alignment.CenterHorizontally
//            ) {
//                Text(
//                    text = uiState.currentSong?.title ?: "No song",
//                    style = MaterialTheme.typography.headlineMedium
//                )
//                Spacer(modifier = Modifier.height(8.dp))
//                Text(
//                    text = uiState.currentSong?.artist ?: "",
//                    style = MaterialTheme.typography.bodyLarge,
//                    color = MaterialTheme.colorScheme.onSurfaceVariant
//                )
//            }
//
//            /**
//             * Seek Bar + Time
//             */
//            Column(modifier = Modifier.fillMaxWidth()) {
//                Slider(
//                    value = sliderPosition,
//                    onValueChange = { newValue ->
//                        isUserSeeking = true
//                        sliderPosition = newValue
//                    },
//                    onValueChangeFinished = {
//                        viewModel.seekTo(sliderPosition.toLong())
//                        isUserSeeking = false
//                    },
//                    valueRange = 0f..(uiState.currentSong?.duration?.toFloat() ?: 0f)
//                )
//
//                Row(
//                    modifier = Modifier.fillMaxWidth(),
//                    horizontalArrangement = Arrangement.SpaceBetween
//                ) {
//                    Text(
//                        text = formatDuration(uiState.currentPosition),
//                        style = MaterialTheme.typography.bodySmall
//                    )
//                    Text(
//                        text = formatDuration(uiState.currentSong?.duration ?: 0L),
//                        style = MaterialTheme.typography.bodySmall
//                    )
//                }
//            }
//
//            /**
//             * Control Buttons
//             */
//            Row(
//                modifier = Modifier.fillMaxWidth(),
//                horizontalArrangement = Arrangement.SpaceEvenly,
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//
//                IconButton(
//                    onClick = { viewModel.skipToPrevious() },
//                    modifier = Modifier.size(64.dp)
//                ) {
//                    Icon(
//                        imageVector = Icons.Default.SkipPrevious,
//                        contentDescription = "Previous",
//                        modifier = Modifier.size(48.dp)
//                    )
//                }
//
//                FloatingActionButton(
//                    onClick = { viewModel.togglePlayPause() },
//                    modifier = Modifier.size(72.dp)
//                ) {
//                    if (uiState.isBuffering) {
//                        CircularProgressIndicator(
//                            modifier = Modifier.size(32.dp),
//                            color = MaterialTheme.colorScheme.onPrimaryContainer
//                        )
//                    } else {
//                        Icon(
//                            imageVector = if (uiState.isPlaying)
//                                Icons.Default.Pause
//                            else
//                                Icons.Default.PlayArrow,
//                            contentDescription = if (uiState.isPlaying) "Pause" else "Play",
//                            modifier = Modifier.size(48.dp)
//                        )
//                    }
//                }
//
//                IconButton(
//                    onClick = { viewModel.skipToNext() },
//                    modifier = Modifier.size(64.dp)
//                ) {
//                    Icon(
//                        imageVector = Icons.Default.SkipNext,
//                        contentDescription = "Next",
//                        modifier = Modifier.size(48.dp)
//                    )
//                }
//            }
//        }
//    }
//}
//
///**
// * Helper: format milliseconds → M:SS
// */
//private fun formatDuration(millis: Long): String {
//    val totalSeconds = millis / 1000
//    val minutes = totalSeconds / 60
//    val seconds = totalSeconds % 60
//    return String.format("%d:%02d", minutes, seconds)
//}
