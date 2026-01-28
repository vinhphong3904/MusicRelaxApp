package com.example.musicapp.presentation.library

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.NavHostController
import com.example.musicapp.data.api.ApiClient
import com.example.musicapp.data.model.FavoriteDto
import com.example.musicapp.data.model.SongDto
import com.example.musicapp.data.service.GlobalAppState
import kotlinx.coroutines.launch

@Composable
fun FavoritesScreen(
    navController: NavHostController,
    onSongSelect: (SongDto) -> Unit
) {
    val scope = rememberCoroutineScope()
    val lifecycleOwner = LocalLifecycleOwner.current
    var favorites by remember { mutableStateOf<List<FavoriteDto>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var songToDelete by remember { mutableStateOf<FavoriteDto?>(null) }

    val refreshFavorites = {
        scope.launch {
            try {
                val response = ApiClient.musicApi.getFavorites()
                if (response.success) {
                    // PHÒNG THỦ: Luôn lọc trùng ID ngay khi nhận dữ liệu từ Server để tránh Crash UI
                    val cleanData = response.data.distinctBy { it.song_id }
                    favorites = cleanData
                    GlobalAppState.updateFavorites(cleanData.map { it.song_id })
                }
            } catch (e: Exception) { e.printStackTrace() } finally { isLoading = false }
        }
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) refreshFavorites()
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    Scaffold(containerColor = Color.Black) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(bottom = padding.calculateBottomPadding()).statusBarsPadding().padding(top = 12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(horizontal = 8.dp)) {
                IconButton(onClick = { navController.popBackStack() }) { 
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color.White) 
                }
                Text("Bài hát đã thích", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            }

            if (isLoading && favorites.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = Color(0xFF1DB954)) }
            } else if (favorites.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("Bạn chưa thích bài hát nào", color = Color.Gray) }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize().padding(top = 16.dp)) {
                    items(favorites, key = { it.song_id }) { fav ->
                        val song = SongDto(
                            id = fav.song_id, title = fav.title, artist_id = 0,
                            album_id = null, genre_id = null, duration_seconds = 0,
                            audio_url = "", cover_image_url = "", view_count = "0", slug = "", created_at = null
                        )
                        FavoriteItemUI(fav = fav, onDelete = { songToDelete = fav }, onClick = { onSongSelect(song) })
                    }
                }
            }
        }

        if (songToDelete != null) {
            AlertDialog(
                onDismissRequest = { songToDelete = null },
                containerColor = Color(0xFF2A2A2A),
                title = { Text("Bỏ yêu thích?", color = Color.White) },
                text = { Text("Bạn muốn xóa bài hát '${songToDelete?.title}' khỏi mục yêu thích?", color = Color.Gray) },
                confirmButton = {
                    TextButton(onClick = {
                        val idToRemove = songToDelete!!.song_id
                        scope.launch {
                            try {
                                ApiClient.musicApi.removeFavorite(idToRemove)
                                refreshFavorites() // Tải lại sau khi xóa
                            } catch (e: Exception) { e.printStackTrace() } finally { songToDelete = null }
                        }
                    }) { Text("Xóa", color = Color.Red, fontWeight = FontWeight.Bold) }
                },
                dismissButton = { TextButton(onClick = { songToDelete = null }) { Text("Hủy", color = Color.Gray) } }
            )
        }
    }
}

@Composable
fun FavoriteItemUI(fav: FavoriteDto, onDelete: () -> Unit, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable { onClick() }.padding(horizontal = 16.dp, vertical = 8.dp), 
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(modifier = Modifier.size(50.dp).clip(RoundedCornerShape(4.dp)).background(Color(0xFF282828)), contentAlignment = Alignment.Center) {
            Icon(Icons.Default.Favorite, null, tint = Color(0xFF1DB954), modifier = Modifier.size(24.dp))
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(fav.title, color = Color.White, fontWeight = FontWeight.SemiBold, maxLines = 1)
            Text(fav.artist_name, color = Color.Gray, fontSize = 12.sp, maxLines = 1)
        }
        IconButton(onClick = onDelete) {
            Icon(Icons.Default.Delete, null, tint = Color.Gray.copy(alpha = 0.6f), modifier = Modifier.size(20.dp))
        }
    }
}
