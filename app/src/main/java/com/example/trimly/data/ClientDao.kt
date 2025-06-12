package com.example.trimly.data

import android.content.ContentValues
import android.content.Context
import com.example.trimly.utils.DatabaseUtils

class UserDao(context: Context) {
    private val dbHelper = DatabaseUtils.openDatabase(context)

    fun getUserByPhone(phone: String): User? {
        val cursor = dbHelper.rawQuery("SELECT * FROM Users WHERE phone = ? LIMIT 1", arrayOf(phone))
        return if (cursor.moveToFirst()) {
            val user = User(
                userid = cursor.getInt(cursor.getColumnIndexOrThrow("userid")),
                firstName = cursor.getString(cursor.getColumnIndexOrThrow("first_name")),
                lastName = cursor.getString(cursor.getColumnIndexOrThrow("last_name")),
                email = cursor.getString(cursor.getColumnIndexOrThrow("email")),
                phone = cursor.getString(cursor.getColumnIndexOrThrow("phone")),
                role = cursor.getString(cursor.getColumnIndexOrThrow("role")),
                rating = cursor.getDouble(cursor.getColumnIndexOrThrow("rating")),
                createdAt = cursor.getString(cursor.getColumnIndexOrThrow("created_at"))
            )
            cursor.close()
            user
        } else {
            cursor.close()
            null
        }
    }

    fun insertUser(user: User): Long {
        val values = ContentValues().apply {
            put("first_name", user.firstName)
            put("last_name", user.lastName)
            put("email", user.email)
            put("phone", user.phone)
            put("role", user.role)
            put("rating", user.rating)
        }
        return dbHelper.insert("Users", null, values)
    }

    fun getUserById(userid: Int): User? {
        val cursor = dbHelper.rawQuery("SELECT * FROM Users WHERE userid = ? LIMIT 1", arrayOf(userid.toString()))
        return if (cursor.moveToFirst()) {
            val user = User(
                userid = cursor.getInt(cursor.getColumnIndexOrThrow("userid")),
                firstName = cursor.getString(cursor.getColumnIndexOrThrow("first_name")),
                lastName = cursor.getString(cursor.getColumnIndexOrThrow("last_name")),
                email = cursor.getString(cursor.getColumnIndexOrThrow("email")),
                phone = cursor.getString(cursor.getColumnIndexOrThrow("phone")),
                role = cursor.getString(cursor.getColumnIndexOrThrow("role")),
                rating = cursor.getDouble(cursor.getColumnIndexOrThrow("rating")),
                createdAt = cursor.getString(cursor.getColumnIndexOrThrow("created_at"))
            )
            cursor.close()
            user
        } else {
            cursor.close()
            null
        }
    }
} 