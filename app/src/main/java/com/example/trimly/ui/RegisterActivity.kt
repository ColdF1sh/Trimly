package com.example.trimly.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.trimly.R
import com.example.trimly.data.UserDao
import com.example.trimly.data.User
import com.example.trimly.MainActivity

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val etFirstName = findViewById<EditText>(R.id.etFirstName)
        val etLastName = findViewById<EditText>(R.id.etLastName)
        val etEmail = findViewById<EditText>(R.id.etEmail)
        val btnRegister = findViewById<Button>(R.id.btnRegister)
        val userDao = UserDao(this)
        val phone = intent.getStringExtra("phone") ?: ""

        btnRegister.setOnClickListener {
            val firstName = etFirstName.text.toString().trim()
            val lastName = etLastName.text.toString().trim()
            val email = etEmail.text.toString().trim()
            if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty()) {
                Toast.makeText(this, "Заповніть всі поля", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val user = User(
                userid = 0, // автоінкремент
                firstName = firstName,
                lastName = lastName,
                email = email,
                phone = phone,
                role = "client",
                rating = 5.0,
                createdAt = null
            )
            val newId = userDao.insertUser(user)
            if (newId > 0) {
                getSharedPreferences("auth", MODE_PRIVATE).edit()
                    .putInt("userid", newId.toInt())
                    .putString("role", user.role)
                    .apply()
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            } else {
                Toast.makeText(this, "Помилка реєстрації", Toast.LENGTH_SHORT).show()
            }
        }
    }
} 