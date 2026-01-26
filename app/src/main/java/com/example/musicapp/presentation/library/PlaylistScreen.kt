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

@Composable
fun PlaylistScreen(navController: NavHostController) {
    val playlists = remember { mutableStateListOf<String>() }
    var selectedPlaylistName by remember { mutableStateOf<String?>(null) }
    
    var showDialog by remember { mutableStateOf(false) }
    var showSongPicker by remember { mutableStateOf(false) }

    val addedSongs = remember { mutableStateListOf<Triple<String, String, Int>>() }

    val allSongs = listOf(
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

    val suggestions = allSongs.filter { it !in addedSongs }.take(5)

    Scaffold(
        containerColor = Color.Black,
        floatingActionButton = {
            if (selectedPlaylistName == null) {
                FloatingActionButton(
                    onClick = { showDialog = true },
                    containerColor = Color.White,
                    contentColor = Color.Black,
                    shape = CircleShape
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Tạo playlist")
                }
            }
        }
    ) { padding ->
        // SỬA TẠI ĐÂY: Chỉ dùng padding bottom, còn top để statusBarsPadding và 12.dp quản lý cho đồng bộ
        val contentModifier = Modifier
            .fillMaxSize()
            .padding(bottom = padding.calculateBottomPadding()) 
            .statusBarsPadding()
            .padding(top = 12.dp)

        if (selectedPlaylistName == null) {
            Column(modifier = contentModifier) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Playlist", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                }

                if (playlists.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Chưa có danh sách phát nào", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Nhấn vào nút bên dưới để tạo mới", color = Color.Gray, fontSize = 14.sp)
                        }
                    }
                } else {
                    LazyColumn(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
                        items(playlists) { name ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 12.dp)
                                    .clickable { selectedPlaylistName = name },
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
                                    Text(name, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                                    Text("Playlist • Kha", color = Color.Gray, fontSize = 13.sp)
                                }
                            }
                        }
                    }
                }
            }
        } else {
            LazyColumn(modifier = contentModifier) {
                item {
                    IconButton(
                        onClick = { selectedPlaylistName = null },
                        modifier = Modifier.padding(horizontal = 8.dp)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = Color.White)
                    }
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier.size(150.dp).clip(RoundedCornerShape(4.dp)).background(Color(0xFF282828)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(64.dp))
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(selectedPlaylistName!!, color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold, lineHeight = 28.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            Surface(
                                color = Color(0xFF333333),
                                shape = RoundedCornerShape(20.dp),
                                modifier = Modifier.height(32.dp).clickable { showDialog = true }
                            ) {
                                Text("Thay đổi", color = Color.White, fontSize = 12.sp, modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp))
                            }
                        }
                    }
                }

                item {
                    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(24.dp).clip(CircleShape).background(Color(0xFFFF8A80)), contentAlignment = Alignment.Center) {
                                Text("K", color = Color.Black, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Kha", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Info, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("${addedSongs.size * 3} phút", color = Color.Gray, fontSize = 13.sp)
                        }
                    }
                }

                item {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Share, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(28.dp))
                        Spacer(modifier = Modifier.width(24.dp))
                        Icon(Icons.Default.MoreVert, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(28.dp))
                    }
                }

                items(addedSongs) { song ->
                    PlaylistItem(song.first, song.second, song.third)
                }

                item {
                    Box(modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp), contentAlignment = Alignment.Center) {
                        Button(
                            onClick = { showSongPicker = true },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White), 
                            shape = RoundedCornerShape(25.dp), 
                            modifier = Modifier.height(48.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Add, contentDescription = null, tint = Color.Black)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Thêm vào danh sách phát này", color = Color.Black, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                if (suggestions.isNotEmpty()) {
                    item {
                        Text("Các bài hát được đề xuất", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 16.dp, vertical = 20.dp))
                    }
                    items(suggestions) { song ->
                        RecommendedSongItem(song.first, song.second, song.third) {
                            addedSongs.add(song)
                        }
                    }
                }
            }
        }
    }

    if (showSongPicker) {
        AlertDialog(
            onDismissRequest = { showSongPicker = false },
            containerColor = Color(0xFF121212),
            title = { Text("Chọn bài hát để thêm", color = Color.White) },
            text = {
                LazyColumn(modifier = Modifier.height(400.dp)) {
                    items(allSongs) { song ->
                        Row(
                            modifier = Modifier.fillMaxWidth().clickable {
                                if (song !in addedSongs) addedSongs.add(song)
                                showSongPicker = false
                            }.padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Image(painter = painterResource(id = song.third), contentDescription = null, modifier = Modifier.size(40.dp).clip(RoundedCornerShape(4.dp)), contentScale = ContentScale.Crop)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(song.first, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                Text(song.second, color = Color.Gray, fontSize = 12.sp)
                            }
                        }
                    }
                }
            },
            confirmButton = { TextButton(onClick = { showSongPicker = false }) { Text("Đóng", color = Color(0xFF1DB954)) } }
        )
    }

    if (showDialog) {
        var tempName by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showDialog = false },
            containerColor = Color(0xFF2A2A2A),
            title = { Text("Tên danh sách phát", color = Color.White) },
            text = {
                TextField(
                    value = tempName,
                    onValueChange = { tempName = it },
                    placeholder = { Text("Nhập tên playlist...") },
                    colors = TextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent
                    )
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    if (tempName.isNotBlank()) {
                        playlists.add(tempName)
                        showDialog = false
                    }
                }) {
                    Text("Tạo", color = Color(0xFF1DB954), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) { Text("Hủy", color = Color.Gray) }
            }
        )
    }
}

@Composable
fun PlaylistItem(title: String, artist: String, imageRes: Int) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(painter = painterResource(id = imageRes), contentDescription = null, modifier = Modifier.size(52.dp).clip(RoundedCornerShape(4.dp)), contentScale = ContentScale.Crop)
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            Text(artist, color = Color.Gray, fontSize = 14.sp)
        }
        Icon(Icons.Default.MoreVert, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(24.dp))
    }
}

@Composable
fun RecommendedSongItem(title: String, artist: String, imageRes: Int, onAdd: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(painter = painterResource(id = imageRes), contentDescription = null, modifier = Modifier.size(52.dp).clip(RoundedCornerShape(4.dp)), contentScale = ContentScale.Crop)
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, color = Color(0xFF1DB954), fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            Text(artist, color = Color.Gray, fontSize = 14.sp)
        }
        Icon(Icons.Default.Add, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(28.dp).clickable { onAdd() })
    }
}
