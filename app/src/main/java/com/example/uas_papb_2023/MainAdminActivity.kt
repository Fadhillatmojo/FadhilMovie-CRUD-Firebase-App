package com.example.uas_papb_2023

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import com.example.uas_papb_2023.adapter.RvAdminAdapter
import com.example.uas_papb_2023.adapter.TabAdapter
import com.example.uas_papb_2023.dataClass.Movie
import com.example.uas_papb_2023.databinding.ActivityMainAdminBinding
import com.example.uas_papb_2023.databinding.FragmentCrudBinding
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MainAdminActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainAdminBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        firebaseAuth = FirebaseAuth.getInstance()
        binding = ActivityMainAdminBinding.inflate(layoutInflater)

        setContentView(binding.root)

        val username = intent.getStringExtra("EXT_USERNAME")
        val sharedPref = getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
        val usernameSharedPref = sharedPref.getString("username", "")

        with(binding){
            // set tv email welcoming user
            var textUsername = ""
            textUsername = if (username != null) {
                "Welcome, $username!!"
            } else {
                "Welcome, $usernameSharedPref!!"
            }
            binding.tvUsername.text = textUsername
            // set adapter dari si tab layout
            val adapterTabLayout = TabAdapter(supportFragmentManager)
            viewPager.adapter = adapterTabLayout
            viewPagerAdmin = viewPager
            tabLayout.setupWithViewPager(viewPager)
        }
    }

    companion object{
        lateinit var firebaseAuth: FirebaseAuth
        lateinit var viewPagerAdmin: ViewPager
    }

}