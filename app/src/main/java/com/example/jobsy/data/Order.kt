package com.example.jobsy.data

import kotlinx.serialization.Serializable

@Serializable
data class Order(
    val id: Int,
    val user_id: Int,
    val title: String,
    val description: String,
    val content: String,
    val budget: Float,
    val category_id: Int?,
    val image_url: String?,
    val created_at: String
)

