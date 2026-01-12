package com.example.musicapp.data.model.mapper

import com.example.musicapp.data.model.dto.SongDto
import com.example.musicapp.core.database.entity.SongEntity

/**
 * Mapper chuyển đổi giữa các layer:
 * DTO (từ API) ↔ Entity (Room DB) ↔ Model (UI)
 * 
 * Tại sao cần 3 loại?
 * - DTO: Format của server (có thể thay đổi)
 * - Entity: Optimize cho database (thêm index, cachedAt...)
 * - Model: Clean, chỉ data cần thiết cho UI
 */
object SongMapper {
}