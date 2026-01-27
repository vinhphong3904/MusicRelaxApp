//package com.example.musicapp.presentation.components
//
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.layout.*
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.MusicNote
//import androidx.compose.material3.*
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.layout.ContentScale
//import androidx.compose.ui.text.style.TextOverflow
//import androidx.compose.ui.unit.dp
//import coil.compose.AsyncImage
//import com.example.musicapp.domain.model.Song
//
///**
// * Reusable component hiển thị 1 bài hát
// *
// * UI:
// * - Cover image (64x64)
// * - Title + Artist
// * - Duration
// * - Clickable → play song
// *
// * @param song: Song data
// * @param onClick: Callback khi click
// * @param modifier: Custom modifier
// */
//@Composable
//fun SongItem(
//    song: Song,
//    onClick: () -> Unit,
//    modifier: Modifier = Modifier
//) {
//    Row(
//        modifier = modifier
//            .fillMaxWidth()
//            .clickable(onClick = onClick)
//            .padding(vertical = 8.dp),
//        verticalAlignment = Alignment.CenterVertically
//    ) {
//
//        /**
//         * Cover Image
//         */
//        Card(
//            modifier = Modifier.size(64.dp),
//            shape = MaterialTheme.shapes.small
//        ) {
//            if (song.coverUrl.isNotBlank()) {
//                AsyncImage(
//                    model = song.coverUrl,
//                    contentDescription = "Song cover",
//                    modifier = Modifier.fillMaxSize(),
//                    contentScale = ContentScale.Crop
//                )
//            } else {
//                // Placeholder khi không có cover
//                Box(
//                    modifier = Modifier.fillMaxSize(),
//                    contentAlignment = Alignment.Center
//                ) {
//                    Icon(
//                        imageVector = Icons.Default.MusicNote,
//                        contentDescription = null,
//                        modifier = Modifier.size(32.dp),
//                        tint = MaterialTheme.colorScheme.onSurfaceVariant
//                    )
//                }
//            }
//        }
//
//        Spacer(modifier = Modifier.width(16.dp))
//
//        /**
//         * Song Info
//         */
//        Column(
//            modifier = Modifier.weight(1f)
//        ) {
//            Text(
//                text = song.title,
//                style = MaterialTheme.typography.bodyLarge,
//                maxLines = 1,
//                overflow = TextOverflow.Ellipsis
//            )
//
//            Spacer(modifier = Modifier.height(4.dp))
//
//            Text(
//                text = song.artist,
//                style = MaterialTheme.typography.bodyMedium,
//                color = MaterialTheme.colorScheme.onSurfaceVariant,
//                maxLines = 1,
//                overflow = TextOverflow.Ellipsis
//            )
//        }
//
//        /**
//         * Duration
//         */
//        Text(
//            text = song.getFormattedDuration(),
//            style = MaterialTheme.typography.bodySmall,
//            color = MaterialTheme.colorScheme.onSurfaceVariant
//        )
//    }
//}
