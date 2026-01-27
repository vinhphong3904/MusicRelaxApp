package com.example.musicapp.data.model

import com.google.gson.annotations.SerializedName

data class UserDto(
    val id: Int,
    val username: String,
    val email: String,

    @SerializedName("avatar_url")
    val avatarUrl: String?,

    val role: String?,

    val status: Boolean?,

    @SerializedName("full_name")
    val fullName: String?,

    @SerializedName("date_of_birth")
    val dateOfBirth: String?,

    val gender: String?,
    val phone: String?,
    val address: String?,
    val bio: String?
)

