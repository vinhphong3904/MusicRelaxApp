package com.example.musicapp.data.repository

import com.example.musicapp.data.remote.ArtistRemoteDataSource
import com.example.musicapp.domain.model.Artist
import javax.inject.Inject

interface ArtistRepositoryInterface {
    suspend fun getArtists(token: String, keyword: String? = null): List<Artist>
    suspend fun getArtistDetail(token: String, id: Int): Artist
}

class ArtistRepository @Inject constructor(
    private val remoteDataSource: ArtistRemoteDataSource
) : ArtistRepositoryInterface {
    override suspend fun getArtists(token: String, keyword: String?): List<Artist> {
        val response = remoteDataSource.fetchArtists(token, keyword)
        return response.data.map {
            Artist(
                id = it.id,
                name = it.name,
                bio = it.bio,
                imageUrl = it.image_url,
                isVerified = it.is_verified,
                slug = it.slug,
                createdAt = it.created_at
            )
        }
    }

    override suspend fun getArtistDetail(token: String, id: Int): Artist {
        val response = remoteDataSource.fetchArtistDetail(token, id)
        val it = response.data
        return Artist(
            id = it.id,
            name = it.name,
            bio = it.bio,
            imageUrl = it.image_url,
            isVerified = it.is_verified,
            slug = it.slug,
            createdAt = it.created_at
        )
    }
}
