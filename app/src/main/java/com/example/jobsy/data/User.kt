package com.example.jobsy.data

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: Int? = null,
    val email: String,
    val password: String,
    val name: String,
    val bio: String,
    val avatar_url: String,
    val rating: Float,
    val created_at: String
)
