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

class PhoneAuthActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_phone_auth)

        val etPhone = findViewById<EditText>(R.id.etPhone)
        val btnContinue = findViewById<Button>(R.id.btnContinuePhone)
        val userDao = UserDao(this)

        btnContinue.setOnClickListener {
            val phone = etPhone.text.toString().trim()
            if (phone.length < 10) {
                Toast.makeText(this, "Введіть коректний номер телефону", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val user = userDao.getUserByPhone(phone)
            if (user != null) {
                // Зберігаємо userid та роль у SharedPreferences
                getSharedPreferences("auth", MODE_PRIVATE).edit()
                    .putInt("userid", user.userid)
                    .putString("role", user.role)
                    .apply()
                // Переходимо на головну
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            } else {
                // Переходимо на реєстрацію, передаємо номер телефону
                val intent = Intent(this, RegisterActivity::class.java)
                intent.putExtra("phone", phone)
                startActivity(intent)
                finish()
            }
        }
    }
} 