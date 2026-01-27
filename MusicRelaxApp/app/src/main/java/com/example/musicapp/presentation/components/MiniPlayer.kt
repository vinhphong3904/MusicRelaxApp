package com.example.musicapp.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Locale

@Composable
fun MiniPlayer(
    songTitle: String,
    artistName: String,
    imageRes: Int,
    isPlaying: Boolean,
    progress: Float,
    onPlayPauseClick: () -> Unit,
    onNextClick: () -> Unit,
    onPreviousClick: () -> Unit,
    onExpand: () -> Unit
) {
    // Tính toán thời gian thực dựa trên progress (giả định bài hát 4:10)
    val totalSeconds = 250
    val currentSeconds = (progress * totalSeconds).toInt()
    val timeString = String.format(Locale.getDefault(), "%02d:%02d", currentSeconds / 60, currentSeconds % 60)

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 2.dp)
            .height(72.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable { onExpand() },
        color = Color(0xFF282828),
        tonalElevation = 8.dp
    ) {
        Column {
            Row(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = imageRes),
                    contentDescription = null,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.width(10.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = songTitle,
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )
                        // Hiển thị thời gian số (Ví dụ: 01:23)
                        Text(
                            text = timeString,
                            color = Color(0xFF1DB954), // Màu xanh lá cây nổi bật
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(start = 4.dp)
                        )
                    }
                    Text(
                        text = artistName,
                        color = Color.Gray,
                        fontSize = 12.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onPreviousClick, modifier = Modifier.size(32.dp)) {
                        Icon(painterResource(id = android.R.drawable.ic_media_previous), null, tint = Color.White, modifier = Modifier.size(20.dp))
                    }
                    IconButton(onClick = onPlayPauseClick, modifier = Modifier.size(40.dp)) {
                        Icon(
                            painterResource(id = if (isPlaying) android.R.drawable.ic_media_pause else android.R.drawable.ic_media_play),
                            null, tint = Color.White, modifier = Modifier.size(26.dp)
                        )
                    }
                    IconButton(onClick = onNextClick, modifier = Modifier.size(32.dp)) {
                        Icon(painterResource(id = android.R.drawable.ic_media_next), null, tint = Color.White, modifier = Modifier.size(20.dp))
                    }
                }
            }
            
            // Thanh tiến trình mượt mà
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxWidth().height(2.5.dp),
                color = Color.White,
                trackColor = Color.Gray.copy(alpha = 0.2f),
            )
        }
    }
}
