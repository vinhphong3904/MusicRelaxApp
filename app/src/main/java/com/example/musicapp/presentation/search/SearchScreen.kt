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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.musicapp.R

@Composable
fun SearchScreen(
    navController: NavHostController,
    onSongSelect: (Triple<String, String, Int>) -> Unit
) {
    var searchText by remember { mutableStateOf("") }
    
    val playlist = listOf(
        Triple("Đừng Làm Trái Tim Anh Đau", "Sơn Tùng M-TP", R.drawable.icon),
        Triple("Chúng Ta Của Tương Lai", "Sơn Tùng M-TP", R.drawable.tieude),
        Triple("Thiên Lý Ơi", "Jack - J97", R.drawable.nen),
        Triple("Giá Như", "SOOBIN", R.drawable.tieude),
        Triple("Exit Sign", "HIEUTHUHAI", R.drawable.icon),
        Triple("Em Xinh", "MONO", R.drawable.nen),
        Triple("Lệ Lưu Ly", "Vũ Phụng Tiên", R.drawable.tieude),
        Triple("Cắt Đôi Nỗi Sầu", "Tăng Duy Tân", R.drawable.icon),
        Triple("Ngày Mai Người Ta Lấy Chồng", "Anh Tú", R.drawable.nen),
        Triple("Mưa Tháng Sáu", "Văn Mai Hương", R.drawable.tieude)
    )
    
    val filteredSongs = remember(searchText) {
        if (searchText.isBlank()) emptyList()
        else playlist.filter { it.first.contains(searchText, ignoreCase = true) || it.second.contains(searchText, ignoreCase = true) }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .statusBarsPadding()
            .padding(horizontal = 16.dp)
            .padding(top = 8.dp) // Cải tiến: Sát trên 8dp
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(28.dp) // Thu nhỏ avatar 32 -> 28
                        .clip(CircleShape)
                        .background(Color(0xFFFF8A80)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("K", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text("Tìm kiếm", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }
            Icon(Icons.Default.Settings, contentDescription = null, tint = Color.White, modifier = Modifier.size(22.dp))
        }

        Spacer(modifier = Modifier.height(12.dp))

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(42.dp) // Thu nhỏ thanh search 48 -> 42
                .clip(RoundedCornerShape(6.dp)),
            color = Color.White
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 10.dp)
            ) {
                Icon(Icons.Default.Search, contentDescription = null, tint = Color.Black, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(10.dp))
                BasicTextField(
                    value = searchText,
                    onValueChange = { searchText = it },
                    modifier = Modifier.weight(1f),
                    textStyle = TextStyle(color = Color.Black, fontSize = 14.sp),
                    decorationBox = { innerTextField ->
                        if (searchText.isEmpty()) {
                            Text("Bạn muốn nghe gì?", color = Color.DarkGray, fontSize = 14.sp)
                        }
                        innerTextField()
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        if (searchText.isEmpty()) {
            Text(
                "Duyệt tìm tất cả", 
                color = Color.White, 
                fontWeight = FontWeight.Bold, 
                fontSize = 16.sp // Thu nhỏ 18 -> 16
            )
            Spacer(modifier = Modifier.height(12.dp))
            
            val categories = listOf(
                CategoryData("Bản phát hành sắp ra mắt", Color(0xFF0D725B)),
                CategoryData("Mới phát hành", Color(0xFF778F13)),
                CategoryData("Nhạc Việt", Color(0xFF477D95)),
                CategoryData("Pop", Color(0xFF477D95)),
                CategoryData("K-Pop", Color(0xFFE91E63)),
                CategoryData("Hip-Hop", Color(0xFFE8115B)),
                CategoryData("Bảng xếp hạng", Color(0xFF8D67AB)),
                CategoryData("Bảng xếp hạng Podcast", Color(0xFF006450)),
                CategoryData("Sự phạm", Color(0xFFAF2896)),
                CategoryData("Tài liệu", Color(0xFF503750)),
                CategoryData("Hài kịch", Color(0xFFAF2896)),
                CategoryData("Khám phá", Color(0xFF8D67AB)),
                CategoryData("Radio", Color(0xFFEB1E32)),
                CategoryData("Fresh Finds", Color(0xFFFF00FF)),
                CategoryData("EQUAL", Color(0xFF05691B)),
                CategoryData("GLOW", Color(0xFF1E3264)),
                CategoryData("RADAR", Color(0xFF7D4B32)),
                CategoryData("Karaoke", Color(0xFF1E3264)),
                CategoryData("Tâm trạng", Color(0xFFE1118C)),
                CategoryData("Rock", Color(0xFFE91E63)),
                CategoryData("La-tinh", Color(0xFFE1118C)),
                CategoryData("Dance/Điện tử", Color(0xFF477D95)),
                CategoryData("Indie", Color(0xFFE91E63)),
                CategoryData("Tập luyện", Color(0xFF777777)),
                CategoryData("Đồng quê", Color(0xFFD84000)),
                CategoryData("R&B", Color(0xFFD84000)),
                CategoryData("Thư giãn", Color(0xFF7D4B32)),
                CategoryData("Ngủ ngon", Color(0xFF1E3264)),
                CategoryData("Tiệc tùng", Color(0xFFAF2896)),
                CategoryData("Ở nhà", Color(0xFF477D95))
            )

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(categories) { category ->
                    CategoryCard(category.title, category.color)
                }
            }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(filteredSongs) { song ->
                    SearchSuggestionItem(song.first, song.second) {
                        onSongSelect(song)
                    }
                }
            }
        }
    }
}

data class CategoryData(val title: String, val color: Color)

@Composable
fun CategoryCard(title: String, color: Color) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(65.dp) // Thu nhỏ card category 100 -> 65
            .clip(RoundedCornerShape(6.dp))
            .background(color)
            .padding(10.dp)
    ) {
        Text(
            text = title,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp, // Thu nhỏ chữ 14 -> 12
            modifier = Modifier.align(Alignment.TopStart).fillMaxWidth(0.8f)
        )
    }
}

@Composable
fun SearchSuggestionItem(title: String, artist: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(42.dp) // Thu nhỏ 48 -> 42
                .clip(RoundedCornerShape(4.dp))
                .background(Color.DarkGray),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(24.dp))
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(title, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Medium)
            Text(artist, color = Color.Gray, fontSize = 12.sp)
        }
    }
}
