package com.example.trimly.data

data class Service(
    val serviceId: Int,
    val establishmentId: Int,
    val name: String,
    val description: String?,
    val price: Double,
    val duration: Int
) 