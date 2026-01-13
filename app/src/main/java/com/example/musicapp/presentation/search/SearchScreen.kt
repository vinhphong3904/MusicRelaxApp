package com.example.musicapp.presentation.search

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import com.example.musicapp.presentation.home.MusicBottomNavigation
import com.example.musicapp.presentation.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(navController: NavHostController) {
    var searchText by remember { mutableStateOf("") }
    
    val allSongs = listOf(
        "3107 - 2", "3107 - 3", "Dù Cho Mai Về Sau", "Liệu Giờ", "Đường Tôi Chở Em Về",
        "Sơn Tùng M-TP", "Jack - J97", "SOOBIN", "HIEUTHUHAI", "MONO"
    )
    
    val filteredSongs = remember(searchText) {
        if (searchText.isBlank()) emptyList()
        else allSongs.filter { it.contains(searchText, ignoreCase = true) }
    }

    Scaffold(
        containerColor = Color.Black,
        bottomBar = { MusicBottomNavigation(navController) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFFF8A80)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("K", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("Tìm kiếm", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                }
                Icon(Icons.Default.Settings, contentDescription = null, tint = Color.White)
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextField(
                value = searchText,
                onValueChange = { searchText = it },
                placeholder = { Text("Bạn muốn nghe gì?", color = Color.DarkGray) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .clip(RoundedCornerShape(4.dp)),
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.Black) },
                trailingIcon = {
                    if (searchText.isNotEmpty()) {
                        IconButton(onClick = { searchText = "" }) {
                            Icon(Icons.Default.Close, contentDescription = null, tint = Color.Black)
                        }
                    }
                },
                colors = TextFieldDefaults.colors(
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Đã bọc AnimatedVisibility vào Column để fix lỗi Scope
            Column(modifier = Modifier.fillMaxSize()) {
                AnimatedVisibility(
                    visible = searchText.isNotEmpty(),
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(filteredSongs) { song ->
                            SearchSuggestionItem(song) {
                                navController.navigate(Screen.Player.route)
                            }
                        }
                        if (filteredSongs.isEmpty() && searchText.isNotEmpty()) {
                            item {
                                Text(
                                    "Không tìm thấy kết quả cho \"$searchText\"",
                                    color = Color.Gray,
                                    modifier = Modifier.padding(16.dp)
                                )
                            }
                        }
                    }
                }

                AnimatedVisibility(
                    visible = searchText.isEmpty(),
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        item {
                            Text("Khám phá nội dung mới mẻ", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Spacer(modifier = Modifier.height(16.dp))
                            ExploreRow()
                            Spacer(modifier = Modifier.height(24.dp))
                            Text("Duyệt tìm tất cả", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                        item {
                            CategoryGrid()
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SearchSuggestionItem(text: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(48.dp).clip(RoundedCornerShape(4.dp)).background(Color.DarkGray),
            contentAlignment = Alignment.Center
        ) {
            // Đã thay MusicNote bằng PlayArrow để tránh lỗi Unresolved reference
            Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color.Gray)
        }
        Spacer(modifier = Modifier.width(16.dp))
        Text(text, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun ExploreRow() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        ExploreItem("#v-pop", Color(0xFF3F51B5))
        ExploreItem("#indie việt", Color(0xFF4CAF50))
        ExploreItem("#delulu", Color(0xFFE91E63))
    }
}

@Composable
fun ExploreItem(tag: String, bgColor: Color) {
    Box(
        modifier = Modifier
            .width(110.dp)
            .height(160.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
    ) {
        Text(
            tag,
            color = Color.White,
            modifier = Modifier.align(Alignment.BottomStart).padding(8.dp),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun CategoryGrid() {
    val categories = listOf(
        Pair("Nhạc", Color(0xFFE91E63)),
        Pair("Podcasts", Color(0xFF009688)),
        Pair("Sự kiện trực tiếp", Color(0xFF673AB7)),
        Pair("Mới phát hành", Color(0xFF827717)),
        Pair("Nhạc Việt", Color(0xFF37474F)),
        Pair("Pop", Color(0xFF455A64))
    )

    Column {
        for (i in categories.indices step 2) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                CategoryCard(categories[i].first, categories[i].second, Modifier.weight(1f))
                if (i + 1 < categories.size) {
                    CategoryCard(categories[i+1].first, categories[i+1].second, Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
fun CategoryCard(title: String, color: Color, modifier: Modifier) {
    Box(
        modifier = modifier
            .height(100.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(color)
            .padding(12.dp)
    ) {
        Text(
            title,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
        )
    }
}
