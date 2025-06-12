package com.example.trimly.data

import java.io.Serializable

data class Booking(
    val id: Int,
    val userid: Int, // id користувача (клієнта)
    val sessionId: Int,
    val serviceId: Int,
    val establishmentRating: Int? = null,
    val masterRating: Int? = null,
    val clientRating: Int? = null
) : Serializable

// Data class for displaying detailed booking information in the UI, obtained by joining tables
data class DetailedBooking(
    val id: Int,
    val sessionId: Int,
    val clientName: String,
    val masterName: String,
    val salonName: String,
    val serviceName: String,
    val date: String,
    val startTime: String,
    val endTime: String,
    val status: BookingStatus
) : Serializable
