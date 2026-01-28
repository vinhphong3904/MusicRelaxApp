package com.example.musicapp.presentation.search

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.musicapp.data.api.ApiClient
import com.example.musicapp.data.model.SongDto
import kotlinx.coroutines.delay

@Composable
fun SearchScreen(
    navController: NavHostController,
    onSongSelect: (SongDto) -> Unit
) {
    var searchText by remember { mutableStateOf("") }
    var userInitial by remember { mutableStateOf("U") }
    var searchResults by remember { mutableStateOf<List<SongDto>>(emptyList()) }
    var isSearchingApi by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        try {
            val response = ApiClient.musicApi.getMe()
            if (response.success) {
                userInitial = response.user.username.take(1).uppercase()
            }
        } catch (e: Exception) { e.printStackTrace() }
    }

    // Tối ưu hóa: Nhạy hơn với 1 ký tự và delay thấp hơn
    LaunchedEffect(searchText) {
        if (searchText.length >= 1) { // Chỉ cần 1 ký tự là bắt đầu tìm
            delay(300) // Phản hồi nhanh hơn (300ms thay vì 500ms)
            isSearchingApi = true
            try {
                val response = ApiClient.musicApi.search(searchText)
                if (response.success) {
                    searchResults = response.data.songs
                }
            } catch (e: Exception) {
                e.printStackTrace()
                searchResults = emptyList()
            } finally {
                isSearchingApi = false
            }
        } else {
            searchResults = emptyList()
        }
    }

    Column(modifier = Modifier.fillMaxSize().background(Color.Black).statusBarsPadding().padding(horizontal = 16.dp).padding(top = 8.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(28.dp).clip(CircleShape).background(Color(0xFFFF8A80)), contentAlignment = Alignment.Center) {
                    Text(userInitial, color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text("Tìm kiếm", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }
            Icon(Icons.Default.Settings, null, tint = Color.White, modifier = Modifier.size(22.dp))
        }

        Spacer(modifier = Modifier.height(12.dp))

        Surface(modifier = Modifier.fillMaxWidth().height(42.dp).clip(RoundedCornerShape(6.dp)), color = Color.White) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(horizontal = 10.dp)) {
                Icon(Icons.Default.Search, null, tint = Color.Black, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(10.dp))
                BasicTextField(
                    value = searchText, onValueChange = { searchText = it },
                    modifier = Modifier.weight(1f),
                    textStyle = TextStyle(color = Color.Black, fontSize = 14.sp),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    decorationBox = { innerTextField ->
                        if (searchText.isEmpty()) Text("Bạn muốn nghe gì?", color = Color.DarkGray, fontSize = 14.sp)
                        innerTextField()
                    }
                )
                if (isSearchingApi) {
                    CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp, color = Color.Black)
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        if (searchText.isEmpty()) {
            Text("Duyệt tìm tất cả", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(12.dp))
            val categories = listOf(CategoryData("Pop", Color(0xFF477D95)), CategoryData("Nhạc Việt", Color(0xFFE91E63)), CategoryData("K-Pop", Color(0xFF1DB954)))
            LazyVerticalGrid(columns = GridCells.Fixed(2), horizontalArrangement = Arrangement.spacedBy(10.dp), verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxSize()) {
                items(categories) { CategoryCard(it.title, it.color) }
            }
        } else {
            if (searchResults.isEmpty() && !isSearchingApi) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Không tìm thấy kết quả nào", color = Color.Gray)
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(searchResults) { song ->
                        SearchSuggestionItem(song.title, "Nghệ sĩ #${song.artist_id}") {
                            onSongSelect(song)
                        }
                    }
                }
            }
        }
    }
}

data class CategoryData(val title: String, val color: Color)

@Composable
fun CategoryCard(title: String, color: Color) {
    Box(modifier = Modifier.fillMaxWidth().height(65.dp).clip(RoundedCornerShape(6.dp)).background(color).padding(10.dp)) {
        Text(text = title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp, modifier = Modifier.align(Alignment.TopStart).fillMaxWidth(0.8f))
    }
}

@Composable
fun SearchSuggestionItem(title: String, artist: String, onClick: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().clickable { onClick() }.padding(vertical = 10.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(42.dp).clip(RoundedCornerShape(4.dp)).background(Color.DarkGray), contentAlignment = Alignment.Center) {
            Icon(Icons.Default.PlayArrow, null, tint = Color.Gray, modifier = Modifier.size(24.dp))
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(title, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Medium)
            Text(artist, color = Color.Gray, fontSize = 12.sp)
        }
    }
}
