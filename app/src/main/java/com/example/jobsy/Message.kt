package com.example.jobsy

import kotlinx.serialization.Serializable

@Serializable
data class Message(
    val id: Int? = null,
    val sender_id: Int,
    val receiver_id: Int,
    val message: String,
    val created_at: String = ""
)
