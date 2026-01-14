//package com.example.musicapp.presentation.components
//
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.layout.*
//import androidx.compose.material3.*
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.unit.dp
//import com.example.musicapp.domain.model.Playlist
//
//@Composable
//fun PlaylistItem(
//    playlist: Playlist,
//    onClick: () -> Unit
//) {
//    Card(
//        modifier = Modifier
//            .fillMaxWidth()
//            .clickable(onClick = onClick),
//        elevation = CardDefaults.cardElevation(2.dp)
//    ) {
//        Column(modifier = Modifier.padding(16.dp)) {
//            Text(
//                text = playlist.name,
//                style = MaterialTheme.typography.bodyLarge
//            )
//            if (!playlist.description.isNullOrBlank()) {
//                Spacer(modifier = Modifier.height(4.dp))
//                Text(
//                    text = playlist.description,
//                    style = MaterialTheme.typography.bodySmall,
//                    color = MaterialTheme.colorScheme.onSurfaceVariant
//                )
//            }
//        }
//    }
//}
