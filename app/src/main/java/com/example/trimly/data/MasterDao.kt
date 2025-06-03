package com.example.trimly.data

import android.content.Context
import android.util.Log
import com.example.trimly.data.BookingStatus
import com.example.trimly.utils.DatabaseUtils
import timber.log.Timber
import java.util.Locale

class MasterDao(context: Context) {
    private val dbHelper = DatabaseUtils.openDatabase(context)

    fun getMastersByEstablishment(establishmentId: Int): List<Master> {
        Timber.i("Starting to load masters for establishment $establishmentId from the database")
        val masters = mutableListOf<Master>()
        val query = """
            SELECT
                masterid,
                establishmentid,
                first_name,
                last_name,
                specialization,
                portfoliourl,
                rating
            FROM
                Masters
            WHERE
                establishmentid = ?
        """.trimIndent()

        Timber.i("SQL Query: $query with establishmentId: $establishmentId")
        val cursor = dbHelper.rawQuery(query, arrayOf(establishmentId.toString()))

        Timber.i("Знайдено майстрів: ${cursor.count}")

        if (cursor.moveToFirst()) {
            val idIndex = cursor.getColumnIndex("masterid")
            val establishmentIdIndex = cursor.getColumnIndex("establishmentid")
            val firstNameIndex = cursor.getColumnIndex("first_name")
            val lastNameIndex = cursor.getColumnIndex("last_name")
            val specializationIndex = cursor.getColumnIndex("specialization")
            val portfolioUrlIndex = cursor.getColumnIndex("portfoliourl")
            val ratingIndex = cursor.getColumnIndex("rating")

            Timber.i("Column indices - masterId: $idIndex, establishmentId: $establishmentIdIndex, firstName: $firstNameIndex, lastName: $lastNameIndex, specialization: $specializationIndex, portfolioUrl: $portfolioUrlIndex, rating: $ratingIndex")

            do {
                val masterId = if (idIndex != -1) cursor.getInt(idIndex) else 0
                val estId = if (establishmentIdIndex != -1) cursor.getInt(establishmentIdIndex) else 0
                val firstName = if (firstNameIndex != -1) cursor.getString(firstNameIndex) else ""
                val lastName = if (lastNameIndex != -1) cursor.getString(lastNameIndex) else ""
                val fullName = "$firstName $lastName".trim()
                val specialty = if (specializationIndex != -1) cursor.getString(specializationIndex) else null
                val portfolioUrl = if (portfolioUrlIndex != -1) cursor.getString(portfolioUrlIndex) else null
                val rating = if (ratingIndex != -1) cursor.getDouble(ratingIndex) else null

                Timber.i("Loaded master: masterId=$masterId, establishmentId=$estId, name=$fullName, specialty=$specialty, portfolioUrl=$portfolioUrl, rating=$rating")
                masters.add(Master(masterId, estId, fullName, specialty, portfolioUrl, rating))
            } while (cursor.moveToNext())
        } else {
            Timber.w("No masters found for establishment $establishmentId in the database")
        }

        cursor.close()
        Timber.i("Master loading complete. Total masters: ${masters.size}")
        return masters
    }

    fun getAvailableMasterSessions(masterId: Int): List<MasterSession> {
        Timber.i("Starting to load available sessions for master $masterId from the database")
        val sessions = mutableListOf<MasterSession>()
        val query = """
            SELECT
                sessionid,
                masterid,
                date,
                start_time,
                end_time,
                status
            FROM
                MasterSessions
            WHERE
                masterid = ? AND status = ?
            ORDER BY date ASC, start_time ASC
        """.trimIndent()

        val cursor = dbHelper.rawQuery(query, arrayOf(masterId.toString(), BookingStatus.PENDING.name.uppercase(Locale.ROOT)))

        Timber.i("Found available sessions for master $masterId: ${cursor.count}")

        if (cursor.moveToFirst()) {
            val sessionIdIndex = cursor.getColumnIndex("sessionid")
            val masterIdIndex = cursor.getColumnIndex("masterid")
            val dateIndex = cursor.getColumnIndex("date")
            val startTimeIndex = cursor.getColumnIndex("start_time")
            val endTimeIndex = cursor.getColumnIndex("end_time")
            val statusIndex = cursor.getColumnIndex("status")

            do {
                val sessionId = if (sessionIdIndex != -1) cursor.getInt(sessionIdIndex) else 0
                val currentMasterId = if (masterIdIndex != -1) cursor.getInt(masterIdIndex) else 0
                val date = if (dateIndex != -1) cursor.getString(dateIndex) else ""
                val startTime = if (startTimeIndex != -1) cursor.getString(startTimeIndex) else ""
                val endTime = if (endTimeIndex != -1) cursor.getString(endTimeIndex) else ""
                val statusString = if (statusIndex != -1) cursor.getString(statusIndex) else "PENDING" // Default status

                val status = BookingStatus.Companion.fromString(statusString)

                // Ensure correct parameter names and types for MasterSession constructor
                sessions.add(MasterSession(sessionId = sessionId, masterId = currentMasterId, date = date, startTime = startTime, endTime = endTime, status = status))
            } while (cursor.moveToNext())
        } else {
            Timber.w("No available sessions found for master $masterId in the database")
        }

        cursor.close()
        Timber.i("Available sessions loading complete. Total sessions: ${sessions.size}")
        return sessions
    }
}
