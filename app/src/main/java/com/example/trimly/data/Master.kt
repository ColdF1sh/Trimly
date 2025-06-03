package com.example.trimly.data

data class Master(
    val masterId: Int,
    val establishmentId: Int,
    val name: String,
    val specialty: String?,
    val portfolioUrl: String?,
    val rating: Double?
) 