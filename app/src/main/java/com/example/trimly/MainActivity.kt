package com.example.trimly

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.trimly.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val prefs = getSharedPreferences("auth", MODE_PRIVATE)
        if (!prefs.contains("userid")) {
            startActivity(Intent(this, com.example.trimly.ui.PhoneAuthActivity::class.java))
            finish()
            return
        }
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        binding.bottomNavigation.setupWithNavController(navController)

        // Reset профілю при перемиканні вкладки
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            if (item.itemId == R.id.navigation_profile) {
                navController.popBackStack(R.id.navigation_profile, false)
                navController.navigate(R.id.navigation_profile)
                true
            } else {
                navController.navigate(item.itemId)
                true
            }
        }
    }
}
