package com.example.trimly.data

import java.io.Serializable

// Відповідає структурі таблиці Users
data class User(
    val userid: Int,
    val firstName: String,
    val lastName: String?,
    val email: String,
    val phone: String,
    val role: String, // "client", "master", "admin"
    val rating: Double = 5.0,
    val createdAt: String? = null
) : Serializable 