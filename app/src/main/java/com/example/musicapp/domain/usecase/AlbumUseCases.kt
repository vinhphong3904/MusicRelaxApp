package com.example.musicapp.domain.usecase

import com.example.musicapp.data.repository.AlbumRepositoryInterface
import com.example.musicapp.domain.model.Album
import javax.inject.Inject

class GetAlbumsUseCase @Inject constructor(
    private val repository: AlbumRepositoryInterface
) {
    suspend operator fun invoke(
        token: String,
        keyword: String? = null,
        artistId: Int? = null,
        page: Int = 1,
        limit: Int = 20
    ): List<Album> {
        return repository.getAlbums(token, keyword, artistId, page, limit)
    }
}

class GetAlbumDetailUseCase @Inject constructor(
    private val repository: AlbumRepositoryInterface
) {
    suspend operator fun invoke(token: String, id: Int): Album =
        repository.getAlbumDetail(token, id)
}

