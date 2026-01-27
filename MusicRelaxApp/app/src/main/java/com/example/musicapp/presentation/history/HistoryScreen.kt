package com.example.musicapp.presentation.history

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(navController: NavHostController) {
    Scaffold(
        containerColor = Color(0xFF121212),
        topBar = {
            TopAppBar(
                title = { Text("Lịch sử phát", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF121212))
            )
        }
    ) { padding ->
        val historyItems = listOf(
            HistoryItem("3107 - 2", "W/N, DuongG, Nau", "Hôm nay"),
            HistoryItem("Dù Cho Mai Về Sau", "buitruonglinh", "Hôm nay"),
            HistoryItem("Liệu Giờ", "2T", "Hôm qua"),
            HistoryItem("Đường Tôi Chở Em Về", "buitruonglinh", "Hôm qua"),
            HistoryItem("3107-3", "W/N", "2 ngày trước")
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            items(historyItems) { item ->
                HistoryRow(item)
            }
        }
    }
}

data class HistoryItem(val title: String, val artist: String, val time: String)

@Composable
fun HistoryRow(item: HistoryItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(50.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(Color.DarkGray)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(item.title, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Text("${item.artist} • ${item.time}", color = Color.Gray, fontSize = 12.sp)
        }
        Icon(Icons.Default.MoreVert, contentDescription = null, tint = Color.Gray)
    }
}
