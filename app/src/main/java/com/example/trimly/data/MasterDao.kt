package com.example.trimly.data

import android.content.Context
import com.example.trimly.utils.DatabaseUtils
import timber.log.Timber
import java.util.Locale

class MasterDao(context: Context) {
    private val dbHelper = DatabaseUtils.openDatabase(context)

    fun getMastersByEstablishment(establishmentId: Int): List<Master> {
        Timber.i("Loading masters for establishment $establishmentId from the database")
        val masters = mutableListOf<Master>()
        val query = """
            SELECT
                U.userid,
                U.first_name,
                U.last_name,
                U.phone,
                U.email,
                EM.specialization,
                EM.portfolio_url,
                EM.rating,
                EM.establishmentid
            FROM
                Users AS U
            JOIN
                EstablishmentMasters AS EM ON U.userid = EM.userid
            WHERE
                EM.establishmentid = ?
        """.trimIndent()
        val cursor = dbHelper.rawQuery(query, arrayOf(establishmentId.toString()))
        if (cursor.moveToFirst()) {
            do {
                val userid = cursor.getInt(cursor.getColumnIndexOrThrow("userid"))
                val firstName = cursor.getString(cursor.getColumnIndexOrThrow("first_name"))
                val lastName = cursor.getString(cursor.getColumnIndexOrThrow("last_name"))
                val phone = cursor.getString(cursor.getColumnIndexOrThrow("phone"))
                val email = cursor.getString(cursor.getColumnIndexOrThrow("email"))
                val specialization = cursor.getString(cursor.getColumnIndexOrThrow("specialization"))
                val portfolioUrl = cursor.getString(cursor.getColumnIndexOrThrow("portfolio_url"))
                val rating = cursor.getDouble(cursor.getColumnIndexOrThrow("rating"))
                val estId = cursor.getInt(cursor.getColumnIndexOrThrow("establishmentid"))
                masters.add(Master(userid, firstName, lastName, phone, email, specialization, portfolioUrl, rating, estId))
            } while (cursor.moveToNext())
        }
        cursor.close()
        Timber.i("Master loading complete. Total masters: ${masters.size}")
        return masters
    }

    fun getAvailableMasterSessions(masterId: Int, establishmentId: Int): List<MasterSession> {
        Timber.i("Loading available sessions for master $masterId at establishment $establishmentId from the database")
        val sessions = mutableListOf<MasterSession>()
        val query = """
            SELECT
                sessionid,
                masterid,
                establishmentid,
                date,
                start_time,
                end_time,
                status
            FROM
                MasterSessions
            WHERE
                masterid = ? AND establishmentid = ? AND status = ?
            ORDER BY date ASC, start_time ASC
        """.trimIndent()
        val cursor = dbHelper.rawQuery(query, arrayOf(masterId.toString(), establishmentId.toString(), BookingStatus.PENDING.name.uppercase(Locale.ROOT)))
        if (cursor.moveToFirst()) {
            do {
                val sessionId = cursor.getInt(cursor.getColumnIndexOrThrow("sessionid"))
                val masterUserId = cursor.getInt(cursor.getColumnIndexOrThrow("masterid"))
                val estId = cursor.getInt(cursor.getColumnIndexOrThrow("establishmentid"))
                val date = cursor.getString(cursor.getColumnIndexOrThrow("date"))
                val startTime = cursor.getString(cursor.getColumnIndexOrThrow("start_time"))
                val endTime = cursor.getString(cursor.getColumnIndexOrThrow("end_time"))
                val statusString = cursor.getString(cursor.getColumnIndexOrThrow("status"))
                val status = BookingStatus.fromString(statusString)
                sessions.add(MasterSession(sessionId, masterUserId, date, startTime, endTime, status))
            } while (cursor.moveToNext())
        }
        cursor.close()
        Timber.i("Available sessions loading complete. Total sessions: ${sessions.size}")
        return sessions
    }

    fun insertMaster(master: Master): Long {
        val values = android.content.ContentValues().apply {
            put("userid", master.userid)
            put("establishmentid", master.establishmentId)
            put("specialization", master.specialization)
            put("portfolio_url", master.portfolioUrl)
            put("rating", master.rating ?: 5.0)
        }
        return dbHelper.insert("EstablishmentMasters", null, values)
    }

    fun deleteMaster(userid: Int, establishmentId: Int): Int {
        // Видаляємо з EstablishmentMasters
        val rows = dbHelper.delete("EstablishmentMasters", "userid = ? AND establishmentid = ?", arrayOf(userid.toString(), establishmentId.toString()))
        // Також видаляємо користувача (майстра) з Users, якщо потрібно (cascade має спрацювати, але можна явно)
        dbHelper.delete("Users", "userid = ?", arrayOf(userid.toString()))
        return rows
    }

    fun insertSessionsForDays(masterId: Int, establishmentId: Int, startTime: String, endTime: String, dates: List<String>): List<String> {
        val conflicts = mutableListOf<String>()
        for (dateStr in dates) {
            // Перевірка на перетин часу
            val query = """
                SELECT * FROM MasterSessions WHERE masterid = ? AND establishmentid = ? AND date = ?
            """.trimIndent()
            val cursor = dbHelper.rawQuery(query, arrayOf(masterId.toString(), establishmentId.toString(), dateStr))
            var overlap = false
            if (cursor.moveToFirst()) {
                do {
                    val existingStart = cursor.getString(cursor.getColumnIndexOrThrow("start_time"))
                    val existingEnd = cursor.getString(cursor.getColumnIndexOrThrow("end_time"))
                    if (timesOverlap(startTime, endTime, existingStart, existingEnd)) {
                        overlap = true
                        break
                    }
                } while (cursor.moveToNext())
            }
            cursor.close()
            if (overlap) {
                conflicts.add(dateStr)
                continue
            }
            val values = android.content.ContentValues().apply {
                put("masterid", masterId)
                put("establishmentid", establishmentId)
                put("date", dateStr)
                put("start_time", startTime)
                put("end_time", endTime)
                put("status", "PENDING")
            }
            dbHelper.insert("MasterSessions", null, values)
        }
        return conflicts
    }

    private fun timesOverlap(start1: String, end1: String, start2: String, end2: String): Boolean {
        // Формат часу: HH:mm
        val fmt = java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault())
        val s1 = fmt.parse(start1)
        val e1 = fmt.parse(end1)
        val s2 = fmt.parse(start2)
        val e2 = fmt.parse(end2)
        return s1.before(e2) && s2.before(e1)
    }

    fun getAllSessionsByEstablishment(establishmentId: Int): List<MasterSession> {
        val sessions = mutableListOf<MasterSession>()
        val query = """
            SELECT
                sessionid,
                masterid,
                establishmentid,
                date,
                start_time,
                end_time,
                status
            FROM
                MasterSessions
            WHERE
                establishmentid = ? AND date >= date('now')
            ORDER BY date ASC, start_time ASC
        """.trimIndent()
        val cursor = dbHelper.rawQuery(query, arrayOf(establishmentId.toString()))
        if (cursor.moveToFirst()) {
            do {
                val sessionId = cursor.getInt(cursor.getColumnIndexOrThrow("sessionid"))
                val masterUserId = cursor.getInt(cursor.getColumnIndexOrThrow("masterid"))
                val estId = cursor.getInt(cursor.getColumnIndexOrThrow("establishmentid"))
                val date = cursor.getString(cursor.getColumnIndexOrThrow("date"))
                val startTime = cursor.getString(cursor.getColumnIndexOrThrow("start_time"))
                val endTime = cursor.getString(cursor.getColumnIndexOrThrow("end_time"))
                val statusString = cursor.getString(cursor.getColumnIndexOrThrow("status"))
                val status = BookingStatus.fromString(statusString)
                sessions.add(MasterSession(sessionId, masterUserId, date, startTime, endTime, status))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return sessions
    }
}
