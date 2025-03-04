package com.example.jobsy

import kotlinx.serialization.Serializable

@Serializable
data class Service(
    val id: Int,
    val user_id: Int,
    val title: String,
    val description: String,
    val content: String,
    val price: Float,
    val category_id: Int?,
    val image_url: String?,
    val created_at: String
)
