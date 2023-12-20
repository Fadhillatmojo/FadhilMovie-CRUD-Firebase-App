package com.example.uas_papb_2023

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.uas_papb_2023.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {
    private lateinit var binding:FragmentProfileBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // membuat binding
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        val view = binding.root
        val sharedPref = activity?.getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
        val usernameSharedPref = sharedPref?.getString("username", "null")
        val emailSharedPref = sharedPref?.getString("email", "null")
        binding.tvUsername.text = usernameSharedPref
        binding.tvEmail.text = emailSharedPref

        // set onclick listener ketika logout button dipencet
        binding.btnLogout.setOnClickListener{
            MainUserActivity.firebaseAuth.signOut()
            // Di dalam aktivitas login setelah pengguna berhasil login
            val context: Context = requireActivity()
            val sharedPref = context.getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
            val userLogin = sharedPref.edit()
            // Set isLoggedIn menjadi true setelah berhasil login
            userLogin.putBoolean("isUserLoggedIn", false)
            userLogin.putString("userId", "null")
            userLogin.putString("username", "null")
            userLogin.putString("email", "null")
            userLogin.apply()
            // intent untuk logout
            val intenttoLoginActivity = Intent(context, LoginActivity::class.java)
            startActivity(intenttoLoginActivity)
            Toast.makeText(requireContext(), "Berhasil Logout!", Toast.LENGTH_SHORT).show()
            finishCurrentActivity()
        }
        // Inflate the layout for this fragment
        return view
    }

    private fun finishCurrentActivity() {
        activity?.let {
            if (!it.isFinishing) {
                it.finish()
            }
        }
    }
}