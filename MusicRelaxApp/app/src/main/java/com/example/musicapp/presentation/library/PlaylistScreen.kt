package com.example.musicapp.presentation.library

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.musicapp.R
import com.example.musicapp.data.api.ApiClient
import com.example.musicapp.data.model.PlaylistDto
import com.example.musicapp.data.model.PlaylistDetail
import com.example.musicapp.data.model.SongDto
import kotlinx.coroutines.launch

@Composable
fun PlaylistScreen(
    navController: NavHostController,
    onSongSelect: (Triple<String, String, Int>) -> Unit
) {
    val playlists = remember { mutableStateListOf<PlaylistDto>() }
    var isLoading by remember { mutableStateOf(true) }
    
    LaunchedEffect(Unit) {
        try {
            val response = ApiClient.musicApi.getPlaylists()
            if (response.success) {
                playlists.clear()
                playlists.addAll(response.data)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            isLoading = false
        }
    }

    var selectedPlaylistId by remember { mutableStateOf<Int?>(null) }
    var playlistDetail by remember { mutableStateOf<PlaylistDetail?>(null) }
    var isDetailLoading by remember { mutableStateOf(false) }

    LaunchedEffect(selectedPlaylistId) {
        selectedPlaylistId?.let { id ->
            isDetailLoading = true
            try {
                val response = ApiClient.musicApi.getPlaylistDetail(id)
                if (response.success) {
                    playlistDetail = response.data
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                isDetailLoading = false
            }
        }
    }

    Scaffold(
        containerColor = Color.Black
    ) { padding ->
        val contentModifier = Modifier
            .fillMaxSize()
            .padding(bottom = padding.calculateBottomPadding()) 
            .statusBarsPadding()
            .padding(top = 12.dp)

        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color(0xFF1DB954))
            }
        } else if (selectedPlaylistId == null) {
            // MÀN HÌNH DANH SÁCH PLAYLIST
            Column(modifier = contentModifier) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(horizontal = 8.dp)) {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                    Text("Playlist của tôi", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                }

                if (playlists.isEmpty()) {
                    Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text("Chưa có danh sách phát nào", color = Color.Gray)
                    }
                } else {
                    LazyColumn(modifier = Modifier.weight(1f).padding(horizontal = 16.dp)) {
                        items(playlists) { playlist ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 12.dp)
                                    .clickable { selectedPlaylistId = playlist.id },
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier.size(56.dp).clip(RoundedCornerShape(4.dp)).background(Color(0xFF282828)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color.Gray)
                                }
                                Spacer(modifier = Modifier.width(16.dp))
                                Column {
                                    Text(playlist.name, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                                    Text("${playlist.song_count} bài hát", color = Color.Gray, fontSize = 13.sp)
                                }
                            }
                        }
                    }
                }
            }
        } else {
            // MÀN HÌNH CHI TIẾT PLAYLIST
            Column(modifier = contentModifier) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(horizontal = 8.dp)) {
                    IconButton(onClick = { selectedPlaylistId = null; playlistDetail = null }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = Color.White)
                    }
                    Text("Quay lại", color = Color.White)
                }

                if (isDetailLoading) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Color(0xFF1DB954))
                    }
                } else {
                    playlistDetail?.let { detail ->
                        Text(detail.name, color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(16.dp))
                        
                        LazyColumn(modifier = Modifier.fillMaxSize()) {
                            items(detail.songs) { song ->
                                SongItem(song) {
                                    onSongSelect(Triple(song.title, "Artist ${song.artist_id}", R.drawable.icon))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SongItem(song: SongDto, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(50.dp).clip(RoundedCornerShape(4.dp)).background(Color(0xFF282828)),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color.Gray)
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(song.title, color = Color.White, fontWeight = FontWeight.SemiBold)
            Text("Nghệ sĩ #${song.artist_id}", color = Color.Gray, fontSize = 12.sp)
        }
    }
}
