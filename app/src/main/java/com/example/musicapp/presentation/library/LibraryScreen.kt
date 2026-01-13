package com.example.musicapp.presentation.library

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import com.example.musicapp.presentation.home.MusicBottomNavigation

@Composable
fun LibraryScreen(navController: NavHostController) {
    Scaffold(
        containerColor = Color.Black,
        bottomBar = { MusicBottomNavigation(navController) }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                // Header
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier.size(32.dp).clip(CircleShape).background(Color(0xFFFF8A80)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("K", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Text("Thư viện", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                        }
                        Row {
                            Icon(Icons.Default.Search, contentDescription = null, tint = Color.White)
                            Spacer(modifier = Modifier.width(16.dp))
                            Icon(Icons.Default.Add, contentDescription = null, tint = Color.White)
                        }
                    }
                    
                    // Filter Chips
                    Row(modifier = Modifier.padding(horizontal = 16.dp)) {
                        Surface(
                            color = Color(0xFF2A2A2A),
                            shape = RoundedCornerShape(20.dp),
                            modifier = Modifier.padding(end = 8.dp)
                        ) {
                            Text("Nghệ sĩ", color = Color.White, modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp), fontSize = 12.sp)
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Sort & View options
                    Row(modifier = Modifier.padding(horizontal = 16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.KeyboardArrowDown, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                        Text("Gần đây", color = Color.White, fontSize = 12.sp, modifier = Modifier.padding(start = 4.dp))
                        Spacer(modifier = Modifier.weight(1f))
                        Icon(Icons.Default.Menu, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Artist List
                val artists = listOf(
                    ArtistData("SOOBIN", "Nghệ sĩ"),
                    ArtistData("Jack - J97", "Nghệ sĩ"),
                    ArtistData("Sơn Tùng M-TP", "Nghệ sĩ")
                )
                items(artists) { artist ->
                    LibraryArtistItem(artist.name, artist.type)
                }

                // Function Items
                item {
                    LibraryAddItem("Thêm nghệ sĩ", Icons.Default.Add)
                    LibraryAddItem("Thêm podcast và chương trình", Icons.Default.Add)
                    LibraryAddItem("Thêm nhạc", Icons.Default.KeyboardArrowDown)
                    LibraryAddItem("Add events & venues", Icons.Default.Add)
                    Spacer(modifier = Modifier.height(80.dp)) // Để không bị che bởi MiniPlayer
                }
            }

            // Mini Player Bar (Giả lập ở cuối màn hình)
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(8.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF2A2A2A))
                    .padding(8.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(40.dp).clip(RoundedCornerShape(4.dp)).background(Color.Gray))
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("3107 - 2 (feat. DuongG & Nau)", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Text("W/N, DuongG, Nau", color = Color.Gray, fontSize = 10.sp)
                    }
                    Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color.White)
                }
            }
        }
    }
}

data class ArtistData(val name: String, val type: String)

@Composable
fun LibraryArtistItem(name: String, type: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(16.dp, 8.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(64.dp).clip(CircleShape).background(Color.DarkGray))
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
            Text(type, color = Color.Gray, fontSize = 13.sp)
        }
    }
}

@Composable
fun LibraryAddItem(text: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Row(modifier = Modifier.fillMaxWidth().padding(16.dp, 12.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(50.dp).background(Color(0xFF1A1A1A), CircleShape), contentAlignment = Alignment.Center) {
            Icon(icon, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(24.dp))
        }
        Spacer(modifier = Modifier.width(16.dp))
        Text(text, color = Color.White, fontSize = 15.sp)
    }
}
