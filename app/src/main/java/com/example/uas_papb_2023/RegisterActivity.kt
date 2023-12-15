package com.example.uas_papb_2023

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.example.uas_papb_2023.dataClass.User
import com.example.uas_papb_2023.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private var firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private var firebaseFirestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private lateinit var roles:Array<String>
    // make collection user
    private val userCollection = firebaseFirestore.collection("users")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        roles =  resources.getStringArray(R.array.roles)

        with(binding){
            // spinner adapter role
            val adapterRole = ArrayAdapter(this@RegisterActivity, android.R.layout.simple_spinner_item, roles)
            var selectedRole ="User"
            adapterRole.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerRole.adapter = adapterRole
            // get data from spinner role
            spinnerRole.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parentView: AdapterView<*>?, selectedItemView: View?, position: Int, id: Long) {
                    selectedRole = roles[position]
                }
                // Handle jika tidak ada item yang dipilih (opsional)
                override fun onNothingSelected(parentView: AdapterView<*>?) {
                    selectedRole = roles[0]
                }
            })
            // end spinner adapter role

            // login button intent
            btnLogin.setOnClickListener(){
                val intentToLoginActivity = Intent(this@RegisterActivity, LoginActivity::class.java)
                startActivity(intentToLoginActivity)
                finish()
            }
            //1707

            // register button
            btnRegister.setOnClickListener(){
                val email = etEmail.text.toString()
                val password = etPassword.text.toString()
                val username = etUsername.text.toString()
                if(email.isNotEmpty() && password.isNotEmpty()) {
                    firebaseAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                val currentUser = firebaseAuth.currentUser
                                // membuat objek data user
                                currentUser?.let {
                                    val user = User(
                                        id = currentUser.uid,
                                        email = email,
                                        username = username,
                                        role = selectedRole
                                    )
                                    addUser(user)
                                }
                                // memasukkan data user ke dalam collection firebase

                                // start activity login
                                startActivity(
                                    Intent(
                                        this@RegisterActivity,
                                        LoginActivity::class.java
                                    )
                                )
                                Toast.makeText(
                                    this@RegisterActivity,
                                    "Berhasil Register, Silakan Login!",
                                    Toast.LENGTH_LONG
                                ).show()
                                finish()
                            }
                        }.addOnFailureListener {
                        Toast.makeText(
                            this@RegisterActivity,
                            it.localizedMessage,
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }
    }

    private fun addUser(user: User){
        userCollection
            .document(user.id)
            .set(user)
            .addOnSuccessListener {
                Toast.makeText(this@RegisterActivity,"Silakan Login",Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Log.d("Register Error", "Error register: ", it)
            }
    }
}