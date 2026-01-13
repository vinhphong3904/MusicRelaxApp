package com.example.musicapp.data.repository

import com.example.musicapp.data.remote.GenreRemoteDataSource
import com.example.musicapp.domain.model.Genre

interface GenreRepositoryInterface {
    suspend fun getGenres(token: String): List<Genre>
    suspend fun getGenreDetail(token: String, id: Int): Genre
}

class GenreRepository(private val remote: GenreRemoteDataSource) : GenreRepositoryInterface {
    override suspend fun getGenres(token: String): List<Genre> {
        val response = remote.fetchGenres(token)
        return response.data.map {
            Genre(
                id = it.id,
                name = it.name,
                slug = it.slug,
                description = it.description,
                createdAt = it.created_at
            )
        }
    }

    override suspend fun getGenreDetail(token: String, id: Int): Genre {
        val response = remote.fetchGenreDetail(token, id)
        val it = response.data
        return Genre(
            id = it.id,
            name = it.name,
            slug = it.slug,
            description = it.description,
            createdAt = it.created_at
        )
    }
}
