package com.example.musicapp.presentation.player

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.musicapp.R
import com.example.musicapp.presentation.viewmodel.PlayerViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerScreen(
    navController: NavController,
    viewModel: PlayerViewModel = hiltViewModel()
) {
    val currentSong by viewModel.currentSong.collectAsState()
    val isPlaying by viewModel.isPlaying.collectAsState()
    val progress by viewModel.progress.collectAsState()
    val currentPosition by viewModel.currentPosition.collectAsState()
    val duration by viewModel.duration.collectAsState()

    if (currentSong == null) {
        navController.popBackStack()
        return
    }

    val song = currentSong!!
    val pagerState = rememberPagerState(pageCount = { 2 })
    val backgroundBrush = Brush.verticalGradient(
        colors = listOf(Color(0xFF5D2525), Color(0xFF121212))
    )

    Scaffold(
        containerColor = Color.Transparent,
        modifier = Modifier.background(backgroundBrush),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = if (pagerState.currentPage == 0) "ĐANG PHÁT" else "LỜI BÀI HÁT",
                            color = Color.Gray,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            song.title,
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.Default.KeyboardArrowDown,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(30.dp)
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { }) {
                        Icon(
                            Icons.Default.MoreVert,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        }
    ) { padding ->
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) { page ->
            if (page == 0) {
                MainPlayerPage(
                    songTitle = song.title,
                    artistName = song.artistName,
                    coverImageUrl = song.coverImageUrl,
                    isPlaying = isPlaying,
                    progress = progress,
                    currentPosition = currentPosition,
                    duration = duration,
                    onPlayPauseClick = { viewModel.togglePlayPause() },
                    onPrevClick = { viewModel.skipToPrevious() },
                    onNextClick = { viewModel.skipToNext() },
                    onProgressChange = { viewModel.seekTo(it) },
                    formatTime = { viewModel.formatTime(it) }
                )
            } else {
                LyricsPage(
                    title = song.title,
                    lyrics = song.lyricsContent
                )
            }
        }
    }
}

@Composable
fun MainPlayerPage(
    songTitle: String,
    artistName: String,
    coverImageUrl: String?,
    isPlaying: Boolean,
    progress: Float,
    currentPosition: Long,
    duration: Long,
    onPlayPauseClick: () -> Unit,
    onPrevClick: () -> Unit,
    onNextClick: () -> Unit,
    onProgressChange: (Float) -> Unit,
    formatTime: (Long) -> String
) {
    val infiniteTransition = rememberInfiniteTransition(label = "Disk Rotation")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 15000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "Disk Rotation"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
            .padding(bottom = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight(0.85f)
                    .aspectRatio(1f)
                    .clip(CircleShape)
                    .background(Color.Black),
                contentAlignment = Alignment.Center
            ) {
                if (coverImageUrl != null) {
                    Image(
                        painter = painterResource(id = R.drawable.icon),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxSize()
                            .rotate(if (isPlaying) rotation else 0f),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Image(
                        painter = painterResource(id = R.drawable.icon),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxSize()
                            .rotate(if (isPlaying) rotation else 0f),
                        contentScale = ContentScale.Crop
                    )
                }

                Box(
                    modifier = Modifier
                        .fillMaxSize(0.2f)
                        .clip(CircleShape)
                        .background(Color(0xFF121212))
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    songTitle,
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    maxLines = 1
                )
                Text(artistName, color = Color.Gray, fontSize = 14.sp)
            }
            Icon(
                Icons.Default.AddCircle,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Slider(
            value = progress,
            onValueChange = onProgressChange,
            modifier = Modifier.height(32.dp),
            colors = SliderDefaults.colors(
                thumbColor = Color.White,
                activeTrackColor = Color.White,
                inactiveTrackColor = Color.White.copy(alpha = 0.2f)
            )
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(formatTime(currentPosition), color = Color.Gray, fontSize = 11.sp)
            Text(
                "-${formatTime(duration - currentPosition)}",
                color = Color.Gray,
                fontSize = 11.sp
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.Refresh,
                contentDescription = null,
                tint = Color(0xFF1DB954),
                modifier = Modifier.size(22.dp)
            )

            IconButton(onClick = onPrevClick) {
                Icon(
                    painter = painterResource(id = android.R.drawable.ic_media_previous),
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(36.dp)
                )
            }

            Surface(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .clickable { onPlayPauseClick() },
                color = Color.White,
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        painter = painterResource(
                            id = if (isPlaying) android.R.drawable.ic_media_pause
                            else android.R.drawable.ic_media_play
                        ),
                        contentDescription = null,
                        tint = Color.Black,
                        modifier = Modifier.size(40.dp)
                    )
                }
            }

            IconButton(onClick = onNextClick) {
                Icon(
                    painter = painterResource(id = android.R.drawable.ic_media_next),
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(36.dp)
                )
            }

            Icon(
                Icons.Default.Star,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(22.dp)
            )
        }
    }
}

@Composable
fun LyricsPage(title: String, lyrics: String?) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            "LỜI BÀI HÁT: $title",
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.ExtraBold,
            lineHeight = 28.sp
        )
        Spacer(modifier = Modifier.height(16.dp))

        if (lyrics != null) {
            Text(
                text = lyrics,
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Normal,
                modifier = Modifier.fillMaxWidth(),
                lineHeight = 26.sp
            )
        } else {
            Text(
                text = "Lời bài hát không khả dụng",
                color = Color.Gray,
                fontSize = 16.sp
            )
        }

        Spacer(modifier = Modifier.height(80.dp))
    }
}