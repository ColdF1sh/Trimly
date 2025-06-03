package com.example.trimly.data

import android.content.Context
import android.util.Log
import com.example.trimly.ui.home.Salon
import com.example.trimly.utils.DatabaseUtils
import timber.log.Timber

class EstablishmentDao(context: Context) {
    private val dbHelper = DatabaseUtils.openDatabase(context)

    fun getAllEstablishments(): List<Salon> {
        Timber.i("Починаємо завантаження закладів з бази даних")
        val establishments = mutableListOf<Salon>()
        val query = """
            SELECT
                establishmentid,
                name,
                address,
                latitude,
                longitude,
                phone_number
            FROM
                Establishments
        """.trimIndent()
        
        Timber.i("SQL Query: $query")
        val cursor = dbHelper.rawQuery(query, null)

        Timber.i("Знайдено закладів: ${cursor.count}")

        if (cursor.moveToFirst()) {
            val idIndex = cursor.getColumnIndex("establishmentid")
            val nameIndex = cursor.getColumnIndex("name")
            val addressIndex = cursor.getColumnIndex("address")
            val latIndex = cursor.getColumnIndex("latitude")
            val lngIndex = cursor.getColumnIndex("longitude")
            val phoneIndex = cursor.getColumnIndex("phone_number")

            Timber.i("Column indices - id: $idIndex, name: $nameIndex, address: $addressIndex, lat: $latIndex, lng: $lngIndex, phone: $phoneIndex")

            do {
                val id = if (idIndex != -1) cursor.getInt(idIndex) else 0
                val name = if (nameIndex != -1) cursor.getString(nameIndex) else "Unknown Salon"
                val address = if (addressIndex != -1) cursor.getString(addressIndex) else ""
                val lat = if (latIndex != -1) cursor.getDouble(latIndex) else 0.0
                val lng = if (lngIndex != -1) cursor.getDouble(lngIndex) else 0.0
                val phone = if (phoneIndex != -1) cursor.getString(phoneIndex) else ""

                Timber.i("Завантажено заклад: id=$id, name=$name, address=$address, lat=$lat, lng=$lng, phone=$phone")
                establishments.add(Salon(name, lat, lng, address, phone, id))
            } while (cursor.moveToNext())
        } else {
            Timber.w("Не знайдено жодного закладу в базі даних")
        }

        cursor.close()
        Timber.i("Завантаження завершено. Всього закладів: ${establishments.size}")
        return establishments
    }
} 