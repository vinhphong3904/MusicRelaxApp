package com.example.musicapp.presentation.search

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.musicapp.R
import com.example.musicapp.presentation.viewmodel.GenresViewModel
import com.example.musicapp.presentation.viewmodel.SearchViewModel
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.filled.PlayArrow


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    navController: NavHostController,
    onSongSelect: (Triple<String, String, Int>) -> Unit,
    searchViewModel: SearchViewModel = hiltViewModel(),
    genresViewModel: GenresViewModel = hiltViewModel(),
    token: String
) {
    var isSearching by remember { mutableStateOf(false) }
    var searchText by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<CategoryData?>(null) }

    val artists by searchViewModel.artists.collectAsState()
    val albums by searchViewModel.albums.collectAsState()
    val songs by searchViewModel.songs.collectAsState()
    val isLoading by searchViewModel.isLoading.collectAsState()
    val genres by genresViewModel.genres.collectAsState()

    val listState = rememberLazyListState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .statusBarsPadding()
    ) {

        /* ================= HEADER ================= */
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isSearching) {
                TextField(
                    value = searchText,
                    onValueChange = {
                        searchText = it
                        selectedCategory = null
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp),
                    placeholder = {
                        Text("Tìm bài hát, nghệ sĩ, album", fontSize = 13.sp)
                    },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = null)
                    },
                    trailingIcon = {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = null,
                            modifier = Modifier.clickable {
                                searchText = ""
                                isSearching = false
                            }
                        )
                    },
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
                        modifier = Modifier
                            .size(26.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF1DB954)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Search,
                            contentDescription = null,
                            tint = Color.Black,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "Tìm kiếm",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                IconButton(onClick = { isSearching = true }) {
                    Icon(
                        Icons.Default.Search,
                        contentDescription = null,
                        tint = Color.White
                    )
                }
            }
        }

        /* ================= CONTENT ================= */

        when {
            searchText.isEmpty() && selectedCategory == null -> {
                Text(
                    "Duyệt tìm tất cả",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(genres) { genre ->
                        CategoryCard(
                            title = genre.name,
                            color = getGenreColor(genre.name)
                        ) {
                            selectedCategory =
                                CategoryData(genre.name, getGenreColor(genre.name))
                            genresViewModel.loadGenreDetail(token, genre.id)
                        }
                    }
                }
            }

            searchText.isNotEmpty() -> {
                LaunchedEffect(searchText) {
                    searchViewModel.search(token)
                }

                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    item { SectionTitle("Nghệ sĩ") }
                    items(artists) {
                        SearchSuggestionItem(it.name, it.bio ?: "")
                    }

                    item { SectionTitle("Album") }
                    items(albums) {
                        SearchSuggestionItem(it.title, it.artistName ?: "")
                    }

                    item { SectionTitle("Bài hát") }
                    items(songs) {
                        SearchSuggestionItem(it.title, it.artistName ?: "") {
                            onSongSelect(
                                Triple(it.title, it.artistName ?: "", R.drawable.icon)
                            )
                        }
                    }

                    if (isLoading) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(color = Color.White, strokeWidth = 2.dp)
                            }
                        }
                    }
                }

                LaunchedEffect(listState) {
                    snapshotFlow {
                        listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index
                    }.collect { index ->
                        if (index == songs.lastIndex && !isLoading) {
                            searchViewModel.loadMore()
                        }
                    }
                }
            }

            selectedCategory != null -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    item { SectionTitle("Bài hát") }
                    items(
                        count = songs.size
                    ) { index ->
                        val song = songs[index]

                        SearchSuggestionItem(
                            title = song.title,
                            artist = song.artistName ?: ""
                        ) {
                            onSongSelect(
                                Triple(song.title, song.artistName ?: "", R.drawable.icon)
                            )
                        }
                    }
                }
            }
        }
    }
}

/* ================= SMALL UI ================= */

@Composable
private fun SectionTitle(text: String) {
    Text(
        text,
        color = Color.White,
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp,
        modifier = Modifier.padding(vertical = 8.dp)
    )
}

// Map màu cho genre
val genreColors = mapOf(
    "Nhạc Việt" to Color(0xFF477D95),
    "Pop" to Color(0xFF778F13),
    "K-Pop" to Color(0xFFE91E63),
    "Hip-Hop" to Color(0xFFE8115B),
    "Rock" to Color(0xFFD84000),
    "R&B" to Color(0xFF8D67AB),
    "Dance" to Color(0xFF006450),
    "Indie" to Color(0xFFAF2896)
)

// Hàm lấy màu theo tên genre
fun getGenreColor(name: String): Color {
    return genreColors[name] ?: Color(
        listOf(
            0xFF1E3264, 0xFFEB1E32, 0xFF7D4B32, 0xFF05691B
        ).random()
    )
}



data class CategoryData(val title: String, val color: Color)

@Composable
fun CategoryCard(title: String, color: Color, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(65.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(color)
            .clickable { onClick() }
            .padding(10.dp)
    ) {
        Text(
            text = title,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            modifier = Modifier.align(Alignment.TopStart).fillMaxWidth(0.8f)
        )
    }
}

@Composable
fun SearchSuggestionItem(title: String, artist: String, onClick: () -> Unit = {}) {
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
