package com.example.uas_papb_2023

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
        with(binding){
            // code disini
            layoutSplashScreen.setOnClickListener(){
                val intentToLoginActivity = Intent(this@SplashScreenActivity, LoginActivity::class.java)
                startActivity(intentToLoginActivity)
            }
        }
    }
}