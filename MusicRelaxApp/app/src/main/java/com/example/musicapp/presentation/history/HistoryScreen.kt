package com.example.musicapp.presentation.history

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.musicapp.data.api.ApiClient
import com.example.musicapp.data.model.HistoryDto
import com.example.musicapp.data.model.SongDto
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    navController: NavHostController,
    onSongSelect: (SongDto) -> Unit // THÊM THAM SỐ NÀY
) {
    val scope = rememberCoroutineScope()
    var histories by remember { mutableStateOf<List<HistoryDto>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    // Gọi API lấy lịch sử thật
    LaunchedEffect(Unit) {
        try {
            val response = ApiClient.musicApi.getHistories()
            if (response.success) {
                histories = response.data
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            isLoading = false
        }
    }

    Scaffold(
        containerColor = Color.Black,
        topBar = {
            TopAppBar(
                title = { Text("Lịch sử phát", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Black)
            )
        }
    ) { padding ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color(0xFF1DB954))
            }
        } else if (histories.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Chưa có lịch sử phát nhạc", color = Color.Gray)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                items(histories, key = { it.id }) { item ->
                    // Chuyển đổi HistoryDto sang SongDto để có thể phát nhạc
                    val song = SongDto(
                        id = item.song_id, title = item.title, artist_id = 0,
                        artist_name = item.artist_name,
                        album_id = null, genre_id = null, duration_seconds = 0,
                        audio_url = "", cover_image_url = "", view_count = "0", slug = "", created_at = null
                    )
                    HistoryRow(item, onClick = { onSongSelect(song) })
                }
            }
        }
    }
}

@Composable
fun HistoryRow(item: HistoryDto, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() } // THÊM SỰ KIỆN CLICK
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(50.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(Color(0xFF282828)),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.PlayArrow, null, tint = Color.Gray, modifier = Modifier.size(24.dp))
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(item.title, color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, maxLines = 1)
            Text(item.artist_name, color = Color.Gray, fontSize = 13.sp, maxLines = 1)
        }
        Text(
            text = item.played_at.take(10), // Hiển thị ngày YYYY-MM-DD
            color = Color.Gray,
            fontSize = 11.sp
        )
    }
}
