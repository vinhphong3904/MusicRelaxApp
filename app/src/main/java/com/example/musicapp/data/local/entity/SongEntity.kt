package com.example.musicapp.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "songs",
    foreignKeys = [
        ForeignKey(entity = ArtistEntity::class, parentColumns = ["id"], childColumns = ["artist_id"]),
        ForeignKey(entity = AlbumEntity::class, parentColumns = ["id"], childColumns = ["album_id"]),
        ForeignKey(entity = GenreEntity::class, parentColumns = ["id"], childColumns = ["genre_id"])
    ],
    indices = [Index("artist_id"), Index("album_id"), Index("genre_id")]
)
data class SongEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val artist_id: Int,
    val album_id: Int?,
    val genre_id: Int?,
    val lyrics: String?,
    val duration: Int,
    val audio_url: String,
    val image: String?,
    val view_count: Long = 0,
    val status: Int = 1,
    val created_at: Long = System.currentTimeMillis(),
    val updated_at: Long = System.currentTimeMillis()
)
