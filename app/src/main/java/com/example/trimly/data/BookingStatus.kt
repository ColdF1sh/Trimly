package com.example.trimly.data

import java.util.Locale

enum class BookingStatus {
    PENDING, CONFIRMED, CANCELLED, COMPLETED;

    companion object {
        fun fromString(statusString: String): BookingStatus {
            return try {
                valueOf(statusString.uppercase(Locale.ROOT))
            } catch (e: IllegalArgumentException) {
                PENDING
            }
        }
    }
} 