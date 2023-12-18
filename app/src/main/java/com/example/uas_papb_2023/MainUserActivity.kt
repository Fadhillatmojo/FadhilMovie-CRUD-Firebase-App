package com.example.uas_papb_2023

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.uas_papb_2023.databinding.ActivityMainUserBinding

class MainUserActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainUserBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainUserBinding.inflate(layoutInflater)
        setContentView(binding.root)
        with(binding){
            replaceFragment(HomeUserFragment())
            bottomNavbar.setOnItemSelectedListener {
                when(it.itemId){
                    R.id.nav_home -> replaceFragment(HomeUserFragment())
                    R.id.nav_profile -> replaceFragment(ProfileFragment())
                    else -> {}
                }
                true
            }
        }
    }

    // ini gunanya buat replace isinya frame layout ketika navbar bottom di klik
    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout_user, fragment)
        fragmentTransaction.commit()
    }
}