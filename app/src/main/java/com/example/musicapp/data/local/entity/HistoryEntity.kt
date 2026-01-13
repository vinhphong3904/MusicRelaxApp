package com.example.musicapp.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "histories",
    foreignKeys = [
        ForeignKey(entity = UserEntity::class, parentColumns = ["id"], childColumns = ["user_id"]),
        ForeignKey(entity = SongEntity::class, parentColumns = ["id"], childColumns = ["song_id"])
    ],
    indices = [Index("user_id"), Index("song_id")]
)
data class HistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val user_id: Int,
    val song_id: Int,
    val played_at: Long = System.currentTimeMillis()
)
