package com.example.uas_papb_2023

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.uas_papb_2023.databinding.ActivitySplashScreenBinding

class SplashScreenActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // Mendapatkan status login dari Shared Preferences
        val sharedPref = getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
        val isAdminLoggedIn = sharedPref.getBoolean("isAdminLoggedIn", false)
        val isUserLoggedIn = sharedPref.getBoolean("isUserLoggedIn", false)

        with(binding){
            // target activity untuk yang akan di intent
            val targetActivity = if (isAdminLoggedIn) {
                MainAdminActivity::class.java
            } else if (isUserLoggedIn){
                MainUserActivity::class.java
            } else {
                LoginActivity::class.java
            }
            layoutSplashScreen.setOnClickListener(){
                val intentToLoginActivity = Intent(this@SplashScreenActivity, targetActivity)
                startActivity(intentToLoginActivity)
                finish()
            }
        }
    }
}