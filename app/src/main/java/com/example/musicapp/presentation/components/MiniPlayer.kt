//package com.example.musicapp.presentation.components
//
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.layout.*
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.PlayArrow
//import androidx.compose.material3.*
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.getValue
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.layout.ContentScale
//import androidx.compose.ui.text.style.TextOverflow
//import androidx.compose.ui.unit.dp
//import androidx.hilt.navigation.compose.hiltViewModel
//import androidx.lifecycle.compose.collectAsStateWithLifecycle
//
///**
// * Mini Player bar ở bottom navigation
// * Hiển thị khi có bài hát đang phát
// *
// * Features:
// * - Show current song info
// * - Play/Pause button
// * - Click → expand to full player
// *
// * @param onExpand: Callback navigate to PlayerScreen
// * @param viewModel: Shared PlayerViewModel
// */
//@Composable
//fun MiniPlayer(
//    onExpand: () -> Unit,
//    viewModel: PlayerViewModel = hiltViewModel()
//) {
//    /**
//     * Observe player state
//     */
//    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
//
//    /**
//     * Chỉ hiện khi có song đang phát
//     */
//    if (uiState.currentSong == null) return
//
//    Card(
//        modifier = Modifier
//            .fillMaxWidth()
//            .height(72.dp)
//            .clickable(onClick = onExpand),
//        elevation = CardDefaults.cardElevation(8.dp)
//    ) {
//        Row(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(8.dp),
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            /**
//             * Cover Image (nhỏ - 56x56)
//             */
//            AsyncImage(
//                model = uiState.currentSong?.coverUrl ?: "",
//                contentDescription = "Song cover",
//                modifier = Modifier.size(56.dp),
//                contentScale = ContentScale.Crop
//            )
//
//            Spacer(modifier = Modifier.width(12.dp))
//
//            /**
//             * Song Info
//             */
//            Column(
//                modifier = Modifier.weight(1f)
//            ) {
//                Text(
//                    text = uiState.currentSong?.title ?: "",
//                    style = MaterialTheme.typography.bodyMedium,
//                    maxLines = 1,
//                    overflow = TextOverflow.Ellipsis
//                )
//                Text(
//                    text = uiState.currentSong?.artist ?: "",
//                    style = MaterialTheme.typography.bodySmall,
//                    color = MaterialTheme.colorScheme.onSurfaceVariant,
//                    maxLines = 1,
//                    overflow = TextOverflow.Ellipsis
//                )
//            }
//
//            /**
//             * Play/Pause Button
//             */
//            IconButton(
//                onClick = { viewModel.togglePlayPause() }
//            ) {
//                Icon(
//                    imageVector = if (uiState.isPlaying)
//                        Icons.Default.Pause
//                    else
//                        Icons.Default.PlayArrow,
//                    contentDescription = if (uiState.isPlaying) "Pause" else "Play"
//                )
//            }
//        }
//    }
//}