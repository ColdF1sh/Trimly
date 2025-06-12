package com.example.trimly.data

data class Master(
    val userid: Int,
    val firstName: String,
    val lastName: String?,
    val phone: String,
    val email: String,
    val specialization: String?,
    val portfolioUrl: String?,
    val rating: Double?,
    val establishmentId: Int
) 