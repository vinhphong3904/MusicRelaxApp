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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
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
import androidx.navigation.NavController
import com.example.musicapp.R
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerScreen(
    navController: NavController,
    currentPlayingSong: Triple<String, String, Int>?,
    isPlaying: Boolean,
    progress: Float,
    onPlayPauseChange: (Boolean) -> Unit,
    onProgressChange: (Float) -> Unit,
    onNextPrev: (Int) -> Unit
) {
    if (currentPlayingSong == null) {
        navController.popBackStack()
        return
    }

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
                        Text(text = if (pagerState.currentPage == 0) "ĐANG PHÁT" else "LỜI BÀI HÁT", color = Color.Gray, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        Text(currentPlayingSong.first, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.KeyboardArrowDown, contentDescription = null, tint = Color.White, modifier = Modifier.size(32.dp))
                    }
                },
                actions = {
                    IconButton(onClick = { }) { Icon(Icons.Default.MoreVert, contentDescription = null, tint = Color.White) }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { padding ->
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize().padding(padding)
        ) { page ->
            if (page == 0) {
                MainPlayerPage(
                    songTitle = currentPlayingSong.first,
                    artistName = currentPlayingSong.second,
                    imageRes = currentPlayingSong.third,
                    isPlaying = isPlaying,
                    progress = progress,
                    onPlayPauseClick = { onPlayPauseChange(!isPlaying) },
                    onPrevClick = { onNextPrev(-1) },
                    onNextClick = { onNextPrev(1) },
                    onProgressChange = onProgressChange
                )
            } else {
                LyricsPage(currentPlayingSong.first)
            }
        }
    }
}

@Composable
fun MainPlayerPage(
    songTitle: String,
    artistName: String,
    imageRes: Int,
    isPlaying: Boolean,
    progress: Float,
    onPlayPauseClick: () -> Unit,
    onPrevClick: () -> Unit,
    onNextClick: () -> Unit,
    onProgressChange: (Float) -> Unit
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
        modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(20.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .rotate(if (isPlaying) rotation else 0f)
                    .clip(CircleShape)
                    .background(Color.Black),
                contentScale = ContentScale.Crop
            )
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF121212))
            )
        }

        Spacer(modifier = Modifier.height(48.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Column {
                Text(songTitle, color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                Text(artistName, color = Color.Gray, fontSize = 16.sp)
            }
            Icon(Icons.Default.AddCircle, contentDescription = null, tint = Color.White, modifier = Modifier.size(28.dp))
        }

        Spacer(modifier = Modifier.height(24.dp))

        Slider(
            value = progress,
            onValueChange = onProgressChange,
            colors = SliderDefaults.colors(thumbColor = Color.White, activeTrackColor = Color.White, inactiveTrackColor = Color.Gray.copy(alpha = 0.3f))
        )
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            val totalSeconds = 248
            val currentSeconds = (progress * totalSeconds).toInt()
            Text(formatTime(currentSeconds), color = Color.Gray, fontSize = 12.sp)
            Text("-${formatTime(totalSeconds - currentSeconds)}", color = Color.Gray, fontSize = 12.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Refresh, contentDescription = null, tint = Color(0xFF1DB954), modifier = Modifier.size(24.dp))
            
            IconButton(onClick = onPrevClick) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = Color.White, modifier = Modifier.size(36.dp))
            }
            
            Box(
                modifier = Modifier.size(64.dp).clip(CircleShape).background(Color.White).clickable { onPlayPauseClick() },
                contentAlignment = Alignment.Center
            ) {
                // Đã sửa: Thay Icons.Default.Pause (lỗi) bằng Icons.Default.Close
                Icon(
                    imageVector = if (isPlaying) Icons.Default.Close else Icons.Default.PlayArrow,
                    contentDescription = null,
                    tint = Color.Black,
                    modifier = Modifier.size(40.dp)
                )
            }

            IconButton(onClick = onNextClick) {
                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = Color.White, modifier = Modifier.size(36.dp))
            }
            
            Icon(Icons.Default.Star, contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
        }
    }
}

fun formatTime(seconds: Int): String {
    val m = seconds / 60
    val s = seconds % 60
    return String.format(Locale.getDefault(), "%d:%02d", m, s)
}

@Composable
fun LyricsPage(title: String) {
    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp).verticalScroll(rememberScrollState())) {
        Spacer(modifier = Modifier.height(20.dp))
        Text("LỜI BÀI HÁT: $title", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, lineHeight = 34.sp)
        Spacer(modifier = Modifier.height(20.dp))
        val lyrics = listOf("Em là ai từ đâu bước đến nơi đây", "Dịu dàng chân phương", "(Dịu dàng chân phương)", "Em là ai tựa như ánh nắng ban mai", "Ngọt ngào trong sương", "Ngắm em thật lâu con tim anh yếu mềm")
        lyrics.forEach { line ->
            Text(text = line, color = if (line.contains("(")) Color.Gray else Color.White, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp), lineHeight = 34.sp)
        }
        Spacer(modifier = Modifier.height(100.dp))
    }
}
