package com.example.jobsy

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: Int,
    val email: String,
    val password_hash: String,
    val name: String,
    val bio: String,
    val avatar_url: String,
    val rating: Float,
    val created_at: String
)
