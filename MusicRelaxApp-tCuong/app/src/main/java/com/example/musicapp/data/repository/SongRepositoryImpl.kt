//package com.example.musicapp.data.repository
//
//import com.example.musicapp.data.local.SongLocalDataSource
//import com.example.musicapp.data.remote.SongRemoteDataSource
//import com.example.musicapp.domain.repository.SongRepository
//import kotlinx.coroutines.flow.Flow
//import kotlinx.coroutines.flow.first
//import kotlinx.coroutines.flow.flow
//import javax.inject.Inject
//
///**
// * Implementation của SongRepository
// * Single Source of Truth: Quản lý data từ cả Remote (API) và Local (DB)
// *
// * Strategy: Cache-first
// * 1. Emit cached data ngay lập tức (nếu có)
// * 2. Fetch fresh data từ API
// * 3. Update cache
// * 4. Emit fresh data
// *
// * → User thấy data cũ ngay (fast), sau đó tự update (accurate)
// */
//class SongRepositoryImpl @Inject constructor(
//    private val remoteDataSource: SongRemoteDataSource,
//    private val localDataSource: SongLocalDataSource
//) : SongRepository {
//
//    /**
//     * Lấy danh sách songs với cache strategy
//     *
//     * Flow:
//     * 1. Load từ cache → emit ngay
//     * 2. Fetch API → cache → emit fresh data
//     * 3. Nếu API fail: giữ nguyên cached data
//     *
//     * @return Flow<List<Song>> - UI observe và tự update
//     */
//    override fun getSongs(): Flow<List<Song>> = flow {
//        // STEP 1: Emit cached data first (if exists)
//        val cachedSongs = localDataSource.getAllSongs().first()
//        if (cachedSongs.isNotEmpty()) {
//            emit(cachedSongs.map { it.toModel() })
//        }
//
//        // STEP 2: Fetch from API
//        try {
//            val remoteSongs = remoteDataSource.fetchSongs()
//
//            // STEP 3: Update cache
//            localDataSource.saveSongs(remoteSongs.map { it.toEntity() })
//
//            // STEP 4: Emit fresh data
//            emit(remoteSongs.map { it.toModel() })
//
//        } catch (e: Exception) {
//            // Nếu API fail + không có cache → throw error
//            if (cachedSongs.isEmpty()) {
//                throw e
//            }
//            // Nếu có cache → giữ nguyên (đã emit ở step 1)
//        }
//    }
//
//    /**
//     * Lấy chi tiết 1 bài hát
//     *
//     * Strategy:
//     * 1. Check cache first
//     * 2. Nếu không có → fetch API
//     *
//     * @param id: Song ID
//     * @return Song hoặc null
//     */
//    override suspend fun getSongById(id: String): Song? {
//        // Check cache
//        val cached = localDataSource.getSongById(id)
//        if (cached != null) {
//            return cached.toModel()
//        }
//
//        // Fetch from API
//        return try {
//            val remote = remoteDataSource.fetchSongById(id)
//            localDataSource.saveSongs(listOf(remote.toEntity()))
//            remote.toModel()
//        } catch (e: Exception) {
//            null
//        }
//    }
//
//    /**
//     * Tìm kiếm bài hát
//     *
//     * Strategy: API-first (tìm kiếm cần data mới nhất)
//     * Fallback: Tìm trong cache nếu offline
//     *
//     * @param query: Từ khóa
//     * @return Flow<List<Song>>
//     */
//    override fun searchSongs(query: String): Flow<List<Song>> = flow {
//        try {
//            // Tìm trên server
//            val results = remoteDataSource.searchSongs(query)
//            emit(results.map { it.toModel() })
//        } catch (e: Exception) {
//            // Fallback: Tìm trong cache
//            localDataSource.searchSongs(query).collect { cached ->
//                emit(cached.map { it.toModel() })
//            }
//        }
//    }
//}