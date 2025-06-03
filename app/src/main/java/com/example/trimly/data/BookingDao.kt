package com.example.trimly.data

import android.content.Context
import android.util.Log
import com.example.trimly.utils.DatabaseUtils
import java.text.SimpleDateFormat
import java.util.*
import timber.log.Timber
import java.util.Locale

class BookingDao(context: Context) {

    private val dbHelper = DatabaseUtils.openDatabase(context)

    // Updated method to get detailed bookings by joining tables
    fun getAllDetailedBookings(): List<DetailedBooking> {
        Timber.d("BookingDao", "Починаємо завантаження деталізованих записів з бази даних")
        val detailedBookings = mutableListOf<DetailedBooking>()
        val query = """
            SELECT
                A.appointmentid AS id,
                A.sessionid AS sessionId,
                (C.first_name || ' ' || IFNULL(C.last_name, '')) AS clientName,
                (M.first_name || ' ' || IFNULL(M.last_name, '')) AS masterName,
                E.name AS salonName,
                S.name AS serviceName,
                MS.date AS date,
                MS.start_time AS startTime,
                MS.end_time AS endTime,
                A.status AS status
            FROM
                Appointments AS A
            JOIN
                Clients AS C ON A.clientid = C.clientid
            JOIN
                MasterSessions AS MS ON A.sessionid = MS.sessionid
            JOIN
                Masters AS M ON MS.masterid = M.masterid
            JOIN
                Establishments AS E ON M.establishmentid = E.establishmentid
            JOIN
                Services AS S ON A.serviceid = S.serviceid
        """.trimIndent()
        val cursor = dbHelper.rawQuery(query, null)

        Timber.d("BookingDao", "Знайдено деталізованих записів: ${cursor.count}")

        if (cursor.moveToFirst()) {
            val idIndex = cursor.getColumnIndex("id")
            val sessionIdIndex = cursor.getColumnIndex("sessionId")
            val clientNameIndex = cursor.getColumnIndex("clientName")
            val masterNameIndex = cursor.getColumnIndex("masterName")
            val salonNameIndex = cursor.getColumnIndex("salonName")
            val serviceNameIndex = cursor.getColumnIndex("serviceName")
            val dateIndex = cursor.getColumnIndex("date")
            val startTimeIndex = cursor.getColumnIndex("startTime")
            val endTimeIndex = cursor.getColumnIndex("endTime")
            val statusIndex = cursor.getColumnIndex("status")

            do {
                val id = if (idIndex != -1) cursor.getInt(idIndex) else 0
                val sessionId = if (sessionIdIndex != -1) cursor.getInt(sessionIdIndex) else 0
                val clientName = if (clientNameIndex != -1) cursor.getString(clientNameIndex) else "Unknown Client"
                val masterName = if (masterNameIndex != -1) cursor.getString(masterNameIndex) else "Unknown Master"
                val salonName = if (salonNameIndex != -1) cursor.getString(salonNameIndex) else "Unknown Salon"
                val serviceName = if (serviceNameIndex != -1) cursor.getString(serviceNameIndex) else "Unknown Service"
                val date = if (dateIndex != -1) cursor.getString(dateIndex) else ""
                val startTime = if (startTimeIndex != -1) cursor.getString(startTimeIndex) else ""
                val endTime = if (endTimeIndex != -1) cursor.getString(endTimeIndex) else ""
                val statusString = if (statusIndex != -1) cursor.getString(statusIndex) else "Pending"

                val status = try {
                    BookingStatus.valueOf(statusString.uppercase(Locale.ROOT))
                } catch (e: IllegalArgumentException) {
                    Timber.e(e, "Помилка конвертації статусу: $statusString")
                    BookingStatus.PENDING
                }

                Timber.d("BookingDao", "Завантажено деталізований запис: id=$id, sessionId=$sessionId, clientName=$clientName, masterName=$masterName, salonName=$salonName, serviceName=$serviceName, date=$date, startTime=$startTime, endTime=$endTime, status=$status")
                detailedBookings.add(DetailedBooking(id, sessionId, clientName, masterName, salonName, serviceName, date, startTime, endTime, status))
            } while (cursor.moveToNext())
        }

        cursor.close()
        Timber.d("BookingDao", "Завантаження деталізованих записів завершено. Всього записів: ${detailedBookings.size}")
        return detailedBookings
    }

    // Method to add a new booking using clientId, sessionId, and serviceId
    fun addBooking(clientId: Int, sessionId: Int, serviceId: Int): Long {
        val values = android.content.ContentValues().apply {
            put("clientId", clientId)
            put("sessionId", sessionId)
            put("serviceId", serviceId)
            // Status will be set to 'confirmed' by the database trigger/default
        }
        val newRowId = dbHelper.insert("Appointments", null, values)
        if (newRowId == -1L) {
            Timber.e("BookingDao", "Помилка при додаванні запису до бази даних")
        } else {
            Timber.i("BookingDao", "Запис успішно додано до бази даних з ID: $newRowId")
            // Update MasterSession status to 'confirmed' after successful booking creation
            updateMasterSessionStatus(sessionId, BookingStatus.CONFIRMED)
        }
        return newRowId
    }

    // Method to update MasterSession status
    fun updateMasterSessionStatus(sessionId: Int, status: BookingStatus): Int {
        val values = android.content.ContentValues().apply {
            put("status", status.name.uppercase(Locale.ROOT))
        }
        val rowsAffected = dbHelper.update("MasterSessions", values, "sessionId = ?", arrayOf(sessionId.toString()))
        Timber.d("BookingDao", "Оновлено статус для MasterSession sessionId=$sessionId до $status. Кількість оновлених рядків: $rowsAffected")
        return rowsAffected
    }

    // Keep the existing getBookingStatus function here or move it if needed
    // It's currently used in BookingsPagerAdapter
    fun getBookingStatus(dateStr: String, timeStr: String, currentStatus: BookingStatus): BookingStatus {
        Log.d("BookingStatus", "Input: Date=$dateStr, Time=$timeStr, CurrentStatus=$currentStatus")

        // If the status is already CANCELLED, keep it as CANCELLED.
        if (currentStatus == BookingStatus.CANCELLED) {
            Log.d("BookingStatus", "Status is CANCELLED, returning CANCELLED.")
            return BookingStatus.CANCELLED
        }

        val dateTimeFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        val now = Calendar.getInstance()
        val bookingDateTime = Calendar.getInstance()

        try {
            val parsedDate = dateTimeFormat.parse("$dateStr $timeStr")
            if (parsedDate == null) {
                Log.e("BookingStatus", "Failed to parse date/time, returning current status: $currentStatus.")
                return currentStatus
            }
            bookingDateTime.time = parsedDate
        } catch (e: Exception) {
            Log.e("BookingStatus", "Exception parsing date/time", e)
            return currentStatus
        }

        // Compare booking time with current time only if status is not CANCELLED
        return if (bookingDateTime.before(now)) {
            BookingStatus.COMPLETED
        } else {
            currentStatus
        }
    }

    // Додаю метод для оновлення статусу запису (Appointment)
    fun updateAppointmentStatus(appointmentId: Int, status: BookingStatus): Int {
        val values = android.content.ContentValues().apply {
            put("status", status.name.uppercase(Locale.ROOT))
        }
        val rowsAffected = dbHelper.update("Appointments", values, "appointmentId = ?", arrayOf(appointmentId.toString()))
        Timber.d("BookingDao", "Оновлено статус для Appointment appointmentId=$appointmentId до $status. Кількість оновлених рядків: $rowsAffected")
        return rowsAffected
    }
}

// Keep the existing getBookingStatus function here or move it if needed
// It's currently used in BookingsPagerAdapter
fun getBookingStatus(dateStr: String, timeStr: String, currentStatus: BookingStatus): BookingStatus {
    Log.d("BookingStatus", "Input: Date=$dateStr, Time=$timeStr, CurrentStatus=$currentStatus")

    // If the status is already CANCELLED, keep it as CANCELLED.
    if (currentStatus == BookingStatus.CANCELLED) {
        Log.d("BookingStatus", "Status is CANCELLED, returning CANCELLED.")
        return BookingStatus.CANCELLED
    }

    val dateTimeFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
    val now = Calendar.getInstance()
    val bookingDateTime = Calendar.getInstance()

    try {
        val parsedDate = dateTimeFormat.parse("$dateStr $timeStr")
        if (parsedDate == null) {
            Log.e("BookingStatus", "Failed to parse date/time, returning current status: $currentStatus.")
            return currentStatus
        }
        bookingDateTime.time = parsedDate
    } catch (e: Exception) {
        Log.e("BookingStatus", "Exception parsing date/time", e)
        return currentStatus
    }

    // Compare booking time with current time only if status is not CANCELLED
    return if (bookingDateTime.before(now)) {
        BookingStatus.COMPLETED
    } else {
        currentStatus
    }
}
