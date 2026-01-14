package com.example.musicapp.presentation.library

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
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryScreen(navController: NavHostController) {
    var showSortSheet by remember { mutableStateOf(false) }
    var selectedSort by remember { mutableStateOf("Gần đây") }
    var isSearching by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    
    val sheetState = rememberModalBottomSheetState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Danh sách dữ liệu gốc
    val baseList = remember {
        listOf(
            ArtistData("Danh sách phát thứ 2 của tôi", "Danh sách phát"),
            ArtistData("Danh sách phát #3 của chúng tôi", "Danh sách phát"),
            ArtistData("Danh sách phát thứ 1 của tôi", "Danh sách phát"),
            ArtistData("SOOBIN", "Nghệ sĩ"),
            ArtistData("Jack - J97", "Nghệ sĩ")
        )
    }

    // Logic lọc danh sách theo từ khóa tìm kiếm
    val filteredList = remember(searchQuery, baseList) {
        if (searchQuery.isEmpty()) baseList
        else baseList.filter { it.name.contains(searchQuery, ignoreCase = true) }
    }

    // Logic sắp xếp danh sách
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
            // Header cố định
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Black)
                    .statusBarsPadding()
                    .padding(top = 12.dp)
            ) {
                // Row 1: Avatar, Search Bar hoặc Title
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (isSearching) {
                        // Thanh tìm kiếm trong thư viện cải tiến
                        TextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text("Tìm trong thư viện", color = Color.Gray, fontSize = 15.sp) },
                            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(20.dp)) },
                            trailingIcon = {
                                IconButton(onClick = { 
                                    isSearching = false
                                    searchQuery = ""
                                }) {
                                    Icon(Icons.Default.Close, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                                }
                            },
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color(0xFF282828),
                                unfocusedContainerColor = Color(0xFF282828),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent
                            ),
                            shape = RoundedCornerShape(8.dp),
                            singleLine = true
                        )
                    } else {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFFF8A80)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("K", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            }
                            ViewLibraryTitle()
                        }
                        Row {
                            IconButton(onClick = { isSearching = true }) {
                                Icon(Icons.Default.Search, contentDescription = "Tìm kiếm", tint = Color.White, modifier = Modifier.size(28.dp))
                            }
                            Spacer(modifier = Modifier.width(4.dp))
                            IconButton(onClick = { /* Mở menu thêm */ }) {
                                Icon(Icons.Default.Add, contentDescription = "Thêm", tint = Color.White, modifier = Modifier.size(28.dp))
                            }
                        }
                    }
                }
                
                // Row 2: Filter Chips
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    LibraryFilterChip("Danh sách phát", isSelected = false)
                    LibraryFilterChip("Nghệ sĩ", isSelected = false)
                }
                
                // Row 3: Sort & View options
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .clickable { showSortSheet = true }, 
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Menu, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                    Text(selectedSort, color = Color.White, fontSize = 14.sp, modifier = Modifier.padding(start = 4.dp))
                    Spacer(modifier = Modifier.weight(1f))
                    Icon(Icons.Default.List, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                }
            }

            // Danh sách cuộn
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(sortedItems) { item ->
                    LibraryItem(item.name, item.type)
                }

                item {
                    LibraryAddItem("Thêm nghệ sĩ", Icons.Default.Add) {
                        scope.launch { snackbarHostState.showSnackbar("Tính năng thêm nghệ sĩ sẽ sớm ra mắt") }
                    }
                    LibraryAddItem("Thêm podcast và chương trình", Icons.Default.Add) {
                        scope.launch { snackbarHostState.showSnackbar("Tính năng thêm podcast sẽ sớm ra mắt") }
                    }
                }
            }
        }

        // Bottom Sheet Sắp xếp
        if (showSortSheet) {
            ModalBottomSheet(
                onDismissRequest = { showSortSheet = false },
                sheetState = sheetState,
                containerColor = Color(0xFF282828),
                dragHandle = { BottomSheetDefaults.DragHandle(color = Color.Gray) }
            ) {
                SortSheetContent(
                    currentSort = selectedSort,
                    onSortSelected = { 
                        selectedSort = it
                        showSortSheet = false 
                    },
                    onCancel = { showSortSheet = false }
                )
            }
        }
    }
}

@Composable
fun RowScope.ViewLibraryTitle() {
    Spacer(modifier = Modifier.width(16.dp))
    Text("Thư viện", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
}

@Composable
fun SortSheetContent(
    currentSort: String,
    onSortSelected: (String) -> Unit,
    onCancel: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 32.dp)
    ) {
        Text(
            "Sắp xếp theo",
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(16.dp)
        )
        
        val options = listOf("Gần đây", "Mới thêm gần đây", "Thứ tự chữ cái", "Người tạo")
        options.forEach { option ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSortSelected(option) }
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = option,
                    color = if (option == currentSort) Color(0xFF1DB954) else Color.White,
                    fontSize = 15.sp,
                    fontWeight = if (option == currentSort) FontWeight.Bold else FontWeight.Normal
                )
                if (option == currentSort) {
                    Icon(Icons.Default.Check, contentDescription = null, tint = Color(0xFF1DB954))
                }
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                "Huỷ",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .clickable { onCancel() }
                    .padding(16.dp)
            )
        }
    }
}

@Composable
fun LibraryFilterChip(text: String, isSelected: Boolean) {
    Surface(
        color = if (isSelected) Color(0xFF1DB954) else Color(0xFF2A2A2A),
        shape = RoundedCornerShape(20.dp)
    ) {
        Text(
            text = text, 
            color = if (isSelected) Color.Black else Color.White, 
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp), 
            fontSize = 13.sp
        )
    }
}

data class ArtistData(val name: String, val type: String)

@Composable
fun LibraryItem(name: String, type: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(16.dp, 8.dp), 
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(if (type.contains("Nghệ sĩ")) CircleShape else RoundedCornerShape(4.dp))
                .background(Color.DarkGray),
            contentAlignment = Alignment.Center
        ) {
            Icon(if (type.contains("Nghệ sĩ")) Icons.Default.Person else Icons.Default.PlayArrow, contentDescription = null, tint = Color.Gray)
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(name, color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
            Text(type, color = Color.Gray, fontSize = 14.sp)
        }
    }
}

@Composable
fun LibraryAddItem(text: String, icon: androidx.compose.ui.graphics.vector.ImageVector, onClick: () -> Unit = {}) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable { onClick() }.padding(16.dp, 12.dp), 
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(56.dp).background(Color(0xFF1A1A1A), CircleShape), 
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(28.dp))
        }
        Spacer(modifier = Modifier.width(16.dp))
        Text(text, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Medium)
    }
}
