package com.example.uas_papb_2023

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.uas_papb_2023.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseFirestore: FirebaseFirestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        firebaseAuth = FirebaseAuth.getInstance()
        firebaseFirestore = FirebaseFirestore.getInstance()
        setContentView(binding.root)
        with(binding){
            btnRegister.setOnClickListener(){
                val intentToRegisterActivity = Intent(this@LoginActivity, RegisterActivity::class.java)
                startActivity(intentToRegisterActivity)
                finish()
            }

            // register button
            btnLogin.setOnClickListener(){
                val email = etEmail.text.toString()
                val password = etPassword.text.toString()
                if(email.isNotEmpty() && password.isNotEmpty()){
                    firebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener{
                        if(it.isSuccessful){
                            val currentUser = firebaseAuth.currentUser
                            loginCheck(currentUser)
                        } else{
                            Log.e("failure", "Gagal Login!")
                        }
                    }.addOnFailureListener{
                        Toast.makeText(this@LoginActivity, "Masukkan Data yang benar", Toast.LENGTH_LONG).show()
                    }
                } else {
                    Toast.makeText(this@LoginActivity, "Kolom wajib diisi!", Toast.LENGTH_LONG).show()
                }
            }

        }
    }

    private fun loginCheck(currentUser: FirebaseUser?){
        if (currentUser!=null){
            firebaseFirestore.collection("users").document(currentUser.uid)
                .get().addOnSuccessListener { document->
                    if(document!=null && document.exists()){
                        val userData=document.data!!

                        // checking user data role
                        val role = userData["role"] as String
                        if (role == "Admin") {
                            // Di dalam aktivitas login setelah pengguna berhasil login
                            val sharedPref = getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
                            val userLogin = sharedPref.edit()
                            // Set isLoggedIn menjadi true setelah berhasil login
                            userLogin.putBoolean("isAdminLoggedIn", true)
                            userLogin.putString("email", currentUser.email)
                            userLogin.apply()

                            val intentToAdminActivity = Intent(this@LoginActivity, MainAdminActivity::class.java)
                            intentToAdminActivity.putExtra("EXT_USERNAME",userData["username"] as String)
                            startActivity(intentToAdminActivity)
                            finish()
                        } else {
                            // Di dalam aktivitas login setelah pengguna berhasil login
                            val sharedPref = getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
                            val userLogin = sharedPref.edit()
                            // Set isLoggedIn menjadi true setelah berhasil login
                            userLogin.putBoolean("isUserLoggedIn", true)
                            userLogin.putString("email", currentUser.email)
                            userLogin.apply()

                            val intentToUserActivity = Intent(this@LoginActivity, MainUserActivity::class.java)
                            intentToUserActivity.putExtra("EXT_USERNAME", userData["username"] as String)
                            startActivity(intentToUserActivity)
                            finish()
                        }
                    }
                }
        }
    }

}