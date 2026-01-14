package com.example.musicapp.presentation.library

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryScreen(navController: NavHostController) {
    var isSearching by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var showSortSheet by remember { mutableStateOf(false) }
    var selectedSort by remember { mutableStateOf("Gần đây") }
    
    val sheetState = rememberModalBottomSheetState()
    val snackbarHostState = remember { SnackbarHostState() }

    val baseList = remember {
        listOf(
            ArtistData("Danh sách phát thứ 2 của tôi", "Danh sách phát"),
            ArtistData("Danh sách phát #3 của chúng tôi", "Danh sách phát"),
            ArtistData("Danh sách phát thứ 1 của tôi", "Danh sách phát"),
            ArtistData("SOOBIN", "Nghệ sĩ"),
            ArtistData("Jack - J97", "Nghệ sĩ"),
            ArtistData("Phụ đề âm nhạc hay", "Podcast"),
            ArtistData("Podcast thư giãn đêm khuya", "Podcast")
        )
    }

    val filteredList = remember(searchQuery, baseList) {
        if (searchQuery.isEmpty()) baseList
        else baseList.filter { it.name.contains(searchQuery, ignoreCase = true) }
    }

    val sortedItems = remember(selectedSort, filteredList) {
        when (selectedSort) {
            "Thứ tự chữ cái" -> filteredList.sortedBy { it.name }
            "Người tạo" -> filteredList.sortedBy { it.type }
            "Mới thêm gần đây" -> filteredList.reversed()
            else -> filteredList
        }
    }

    Scaffold(
        containerColor = Color.Black,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = padding.calculateBottomPadding())
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Black)
                    .statusBarsPadding()
                    .padding(top = 8.dp, bottom = 4.dp)
            ) {
                // Header Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (isSearching) {
                        TextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            modifier = Modifier.fillMaxWidth().height(44.dp),
                            placeholder = { Text("Tìm trong thư viện", fontSize = 13.sp) },
                            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(18.dp)) },
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color(0xFF282828),
                                unfocusedContainerColor = Color(0xFF282828),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent
                            ),
                            shape = RoundedCornerShape(6.dp),
                            singleLine = true
                        )
                    } else {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier.size(26.dp).clip(CircleShape).background(Color(0xFFFF8A80)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("K", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Thư viện", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        }
                        Row {
                            IconButton(onClick = { isSearching = true }) { 
                                Icon(Icons.Default.Search, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp)) 
                            }
                            IconButton(onClick = { }) { 
                                Icon(Icons.Default.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(22.dp)) 
                            }
                        }
                    }
                }
                
                // Filter Chips Row
                Row(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    LibraryFilterChip("Danh sách phát")
                    LibraryFilterChip("Nghệ sĩ")
                    LibraryFilterChip("Podcast")
                }

                // Sort Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 2.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier
                            .clickable { showSortSheet = true }
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.List, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = selectedSort, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                    }
                    Icon(imageVector = Icons.Default.Menu, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                }
            }

            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(sortedItems) { item -> LibraryItem(item.name, item.type) }
                item {
                    LibraryAddItem("Thêm nghệ sĩ", Icons.Default.Add)
                    LibraryAddItem("Thêm podcast", Icons.Default.Add)
                }
            }
        }

        if (showSortSheet) {
            ModalBottomSheet(
                onDismissRequest = { showSortSheet = false },
                sheetState = sheetState,
                containerColor = Color(0xFF282828),
                dragHandle = { BottomSheetDefaults.DragHandle(color = Color.Gray) }
            ) {
                Column(modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp)) {
                    Text("Sắp xếp theo", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(16.dp))
                    listOf("Gần đây", "Mới thêm gần đây", "Thứ tự chữ cái", "Người tạo").forEach { option ->
                        Row(
                            modifier = Modifier.fillMaxWidth().clickable { selectedSort = option; showSortSheet = false }.padding(horizontal = 20.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = option, color = if (option == selectedSort) Color(0xFF1DB954) else Color.White, fontSize = 14.sp, fontWeight = if (option == selectedSort) FontWeight.Bold else FontWeight.Normal)
                            if (option == selectedSort) Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = Color(0xFF1DB954), modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LibraryItem(name: String, type: String) {
    Row(modifier = Modifier.fillMaxWidth().clickable { }.padding(horizontal = 16.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(60.dp).clip(if (type.contains("Nghệ sĩ")) CircleShape else RoundedCornerShape(10.dp)).background(Color(0xFF282828)), contentAlignment = Alignment.Center) {
            // Cải tiến: Icon launch phóng to bự lên (44dp) giống thực tế
            Icon(imageVector = if (type.contains("Nghệ sĩ")) Icons.Default.Person else if (type == "Podcast") Icons.Default.Info else Icons.Default.PlayArrow, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(44.dp))
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp, maxLines = 1)
            Text(type, color = Color.Gray, fontSize = 11.sp)
        }
    }
}

@Composable
fun LibraryAddItem(text: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Row(modifier = Modifier.fillMaxWidth().clickable { }.padding(horizontal = 16.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(60.dp).background(Color(0xFF1A1A1A), CircleShape), contentAlignment = Alignment.Center) {
            Icon(imageVector = icon, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(38.dp))
        }
        Spacer(modifier = Modifier.width(12.dp))
        Text(text, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun LibraryFilterChip(text: String) {
    Surface(color = Color(0xFF2A2A2A), shape = RoundedCornerShape(12.dp)) {
        Text(text, color = Color.White, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), fontSize = 10.sp)
    }
}

data class ArtistData(val name: String, val type: String)
