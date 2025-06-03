package com.example.trimly.data

import android.content.Context
import android.util.Log
import com.example.trimly.utils.DatabaseUtils
import timber.log.Timber

class ServiceDao(context: Context) {
    private val dbHelper = DatabaseUtils.openDatabase(context)

    fun getServicesByEstablishment(establishmentId: Int): List<Service> {
        Timber.i("Починаємо завантаження послуг для закладу $establishmentId з бази даних")
        val services = mutableListOf<Service>()
        val query = """
            SELECT
                serviceid,
                establishmentid,
                name,
                description,
                price,
                duration
            FROM
                Services
            WHERE
                establishmentid = ?
        """.trimIndent()
        
        Timber.i("SQL Query: $query with establishmentId: $establishmentId")
        val cursor = dbHelper.rawQuery(query, arrayOf(establishmentId.toString()))

        Timber.i("Знайдено послуг: ${cursor.count}")

        if (cursor.moveToFirst()) {
            val idIndex = cursor.getColumnIndex("serviceid")
            val establishmentIdIndex = cursor.getColumnIndex("establishmentid")
            val nameIndex = cursor.getColumnIndex("name")
            val descriptionIndex = cursor.getColumnIndex("description")
            val priceIndex = cursor.getColumnIndex("price")
            val durationIndex = cursor.getColumnIndex("duration")

            Timber.i("Column indices - serviceId: $idIndex, establishmentId: $establishmentIdIndex, name: $nameIndex, description: $descriptionIndex, price: $priceIndex, duration: $durationIndex")

            do {
                val serviceId = if (idIndex != -1) cursor.getInt(idIndex) else 0
                val estId = if (establishmentIdIndex != -1) cursor.getInt(establishmentIdIndex) else 0
                val name = if (nameIndex != -1) cursor.getString(nameIndex) else "Unknown Service"
                val description = if (descriptionIndex != -1) cursor.getString(descriptionIndex) else null
                val price = if (priceIndex != -1) cursor.getDouble(priceIndex) else 0.0
                val duration = if (durationIndex != -1) cursor.getInt(durationIndex) else 0

                Timber.i("Завантажено послугу: serviceId=$serviceId, establishmentId=$estId, name=$name, description=$description, price=$price, duration=$duration")
                services.add(Service(serviceId, estId, name, description, price, duration))
            } while (cursor.moveToNext())
        } else {
            Timber.w("Не знайдено жодної послуги для закладу $establishmentId в базі даних")
        }

        cursor.close()
        Timber.i("Завантаження послуг завершено. Всього послуг: ${services.size}")
        return services
    }
} 