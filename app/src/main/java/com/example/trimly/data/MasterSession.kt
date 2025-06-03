package com.example.trimly.data


data class MasterSession(
    val sessionId: Int,
    val masterId: Int,
    val date: String,
    val startTime: String,
    val endTime: String,
    val status: BookingStatus
)
