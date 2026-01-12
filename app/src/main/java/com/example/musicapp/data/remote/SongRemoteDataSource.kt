package com.example.musicapp.data.remote

import com.example.musicapp.core.network.ApiService
import com.example.musicapp.data.model.dto.SongDto
import javax.inject.Inject

/**
 * Remote Data Source cho Songs
 * Wrapper cho ApiService - giúp dễ test và maintain
 * 
 * Tại sao không gọi ApiService trực tiếp trong Repository?
 * - Dễ mock để unit test
 * - Có thể thêm logic (retry, caching header...) ở đây
 * - Tách biệt concern: Repository không cần biết API details
 */
class SongRemoteDataSource{
}