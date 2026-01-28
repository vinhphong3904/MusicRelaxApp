package com.example.musicapp.presentation.library

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.musicapp.data.api.ApiClient
import com.example.musicapp.data.model.PlaylistDto
import com.example.musicapp.data.model.PlaylistDetail
import com.example.musicapp.data.model.SongDto
import com.example.musicapp.data.service.GlobalAppState
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PlaylistScreen(
    navController: NavHostController,
    initialPlaylistId: Int? = null,
    onSongSelect: (SongDto) -> Unit
) {
    val scope = rememberCoroutineScope()
    val playlists = remember { mutableStateListOf<PlaylistDto>() }
    // QUAN SÁT TỪ GLOBAL APP STATE ĐỂ ĐỒNG BỘ
    val favoriteSongIds = GlobalAppState.favoriteSongIds
    var isLoading by remember { mutableStateOf(true) }
    
    // SỬ DỤNG rememberSaveable ĐỂ GIỮ TRẠNG THÁI KHI QUAY LẠI TỪ PLAYER
    var selectedPlaylistId by rememberSaveable { mutableStateOf<Int?>(initialPlaylistId) }
    
    var playlistDetail by remember { mutableStateOf<PlaylistDetail?>(null) }
    var isDetailLoading by remember { mutableStateOf(false) }
    var songToDelete by remember { mutableStateOf<SongDto?>(null) }
    var playlistToDelete by remember { mutableStateOf<PlaylistDto?>(null) }

    val playlistsFlag = GlobalAppState.playlistsChangedFlag

    val loadData = {
        scope.launch {
            try {
                val plResponse = ApiClient.musicApi.getPlaylists()
                if (plResponse.success) {
                    playlists.clear()
                    playlists.addAll(plResponse.data)
                }
                val favResponse = ApiClient.musicApi.getFavorites()
                if (favResponse.success) {
                    GlobalAppState.updateFavorites(favResponse.data.map { it.song_id })
                }
            } catch (e: Exception) { e.printStackTrace() } finally { isLoading = false }
        }
    }

    // Tự động tải lại danh sách playlist khi có thay đổi từ màn hình khác
    LaunchedEffect(playlistsFlag) {
        if (playlistsFlag > 0) loadData()
    }

    val refreshPlaylistDetail = { id: Int ->
        scope.launch {
            isDetailLoading = true
            try {
                val response = ApiClient.musicApi.getPlaylistDetail(id)
                if (response.success) playlistDetail = response.data
            } catch (e: Exception) { e.printStackTrace() } finally { isDetailLoading = false }
        }
    }

    LaunchedEffect(Unit) { loadData() }

    LaunchedEffect(selectedPlaylistId) {
        selectedPlaylistId?.let { refreshPlaylistDetail(it) }
    }

    Scaffold(containerColor = Color.Black) { padding ->
        val contentModifier = Modifier.fillMaxSize().padding(bottom = padding.calculateBottomPadding()).statusBarsPadding().padding(top = 12.dp)

        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = Color(0xFF1DB954)) }
        } else if (selectedPlaylistId == null) {
            Column(modifier = contentModifier) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(horizontal = 8.dp)) {
                    IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color.White) }
                    Text("Playlist của tôi", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                }
                LazyColumn(modifier = Modifier.weight(1f).padding(horizontal = 16.dp)) {
                    items(playlists) { playlist ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp)
                                .combinedClickable(
                                    onClick = { selectedPlaylistId = playlist.id },
                                    onLongClick = { playlistToDelete = playlist }
                                ),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(modifier = Modifier.size(56.dp).clip(RoundedCornerShape(4.dp)).background(Color(0xFF282828)), contentAlignment = Alignment.Center) { Icon(Icons.Default.PlayArrow, null, tint = Color.Gray) }
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text(playlist.name, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                                Text("${playlist.song_count} bài hát", color = Color.Gray, fontSize = 13.sp)
                            }
                        }
                    }
                }
            }
        } else {
            Column(modifier = contentModifier) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(horizontal = 8.dp)) {
                    IconButton(onClick = { 
                        if (initialPlaylistId != null && initialPlaylistId != -1) {
                            navController.popBackStack() 
                        } else { 
                            selectedPlaylistId = null
                            playlistDetail = null 
                        } 
                    }) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color.White) }
                    Text("Quay lại", color = Color.White)
                }
                if (isDetailLoading) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = Color(0xFF1DB954)) }
                } else {
                    playlistDetail?.let { detail ->
                        Text(detail.name, color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(16.dp))
                        LazyColumn(modifier = Modifier.fillMaxSize()) {
                            items(detail.songs) { song ->
                                SongItemUI(
                                    song = song,
                                    isFavorite = favoriteSongIds.contains(song.id),
                                    onDelete = { songToDelete = song },
                                    onClick = { onSongSelect(song) }
                                )
                            }
                        }
                    }
                }
            }
        }

        // Hộp thoại xóa bài hát khỏi playlist
        songToDelete?.let { song ->
            AlertDialog(
                onDismissRequest = { songToDelete = null },
                containerColor = Color(0xFF2A2A2A),
                title = { Text("Xóa khỏi playlist?", color = Color.White) },
                text = { Text("Bạn có chắc chắn muốn xóa bài hát '${song.title}' khỏi danh sách phát này không?", color = Color.Gray) },
                confirmButton = {
                    TextButton(onClick = {
                        scope.launch {
                            try {
                                playlistDetail?.id?.let { pid ->
                                    ApiClient.musicApi.removeSongFromPlaylist(pid, song.id)
                                    refreshPlaylistDetail(pid)
                                    // Cập nhật lại số lượng bài hát hiển thị ngoài danh sách tổng
                                    loadData() 
                                }
                            } catch (e: Exception) { e.printStackTrace() } finally { songToDelete = null }
                        }
                    }) { Text("Xóa", color = Color.Red, fontWeight = FontWeight.Bold) }
                },
                dismissButton = { TextButton(onClick = { songToDelete = null }) { Text("Hủy", color = Color.Gray) } }
            )
        }

        // Hộp thoại xóa playlist (DÙNG CHUNG)
        playlistToDelete?.let { playlist ->
            AlertDialog(
                onDismissRequest = { playlistToDelete = null },
                containerColor = Color(0xFF2A2A2A),
                title = { Text("Xóa danh sách phát?", color = Color.White) },
                text = { Text("Bạn có chắc chắn muốn xóa '${playlist.name}' không?", color = Color.Gray) },
                confirmButton = {
                    TextButton(onClick = {
                        GlobalAppState.deletePlaylistRemote(scope, playlist.id) {
                            playlistToDelete = null
                        }
                    }) { Text("Xóa", color = Color.Red, fontWeight = FontWeight.Bold) }
                },
                dismissButton = { TextButton(onClick = { playlistToDelete = null }) { Text("Hủy", color = Color.Gray) } }
            )
        }
    }
}

@Composable
fun SongItemUI(song: SongDto, isFavorite: Boolean, onDelete: () -> Unit, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable { onClick() }.padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(modifier = Modifier.size(50.dp).clip(RoundedCornerShape(4.dp)).background(Color(0xFF282828)), contentAlignment = Alignment.Center) {
            Icon(
                imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.PlayArrow,
                contentDescription = null,
                tint = if (isFavorite) Color(0xFF1DB954) else Color.Gray
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(song.title, color = Color.White, fontWeight = FontWeight.SemiBold)
            Text("Nghệ sĩ #${song.artist_id}", color = Color.Gray, fontSize = 12.sp)
        }
        IconButton(onClick = onDelete) {
            Icon(Icons.Default.Delete, contentDescription = "Xóa", tint = Color.Gray.copy(alpha = 0.6f), modifier = Modifier.size(20.dp))
        }
    }
}
