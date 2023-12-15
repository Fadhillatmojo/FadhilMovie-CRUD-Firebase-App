package com.example.uas_papb_2023

import android.R
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.bumptech.glide.Glide
import com.example.uas_papb_2023.dataClass.Movie
import com.example.uas_papb_2023.databinding.FragmentCrudBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.*

class CrudFragment : Fragment() {
    private lateinit var binding: FragmentCrudBinding
    private lateinit var genres:Array<String>
    private var firebaseFirestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private lateinit var firebaseStorage:FirebaseStorage
    private var displayImageUri: Uri? = null
    private var uploadImageUri: Uri? = null
    private val READ_EXTERNAL_STORAGE_PERMISSION_CODE = 123
    private lateinit var resultLauncher: ActivityResultLauncher<Intent>
    // make collection movie
    private val movieCollection = firebaseFirestore.collection("movies")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?)
    : View? {
        binding = FragmentCrudBinding.inflate(inflater, container, false)
        val view = binding.root
        genres = resources.getStringArray(com.example.uas_papb_2023.R.array.genres)
        firebaseStorage = FirebaseStorage.getInstance()

        with(binding){
            // spinner adapter role
            val adapterGenres = ArrayAdapter(requireContext(), R.layout.simple_spinner_item, genres)
            var selectedGenre ="Horror"
            adapterGenres.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
            spinnerGenres.adapter = adapterGenres
            // get data from spinner role
            spinnerGenres.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parentView: AdapterView<*>?, selectedItemView: View?, position: Int, id: Long) {
                    selectedGenre = genres[position]
                }

                // Handle jika tidak ada item yang dipilih (opsional)
                override fun onNothingSelected(parentView: AdapterView<*>?) {
                    selectedGenre = genres[0]
                }
            }

            // button image chooser memilih image
            btnPickImage.setOnClickListener {
                openFileChooser()
            }

            // result launcher untuk submit file
            resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK && result.data != null) {
                    displayImageUri = result.data?.data
                    Glide.with(requireContext()).load(displayImageUri).into(btnPickImage)
                    uploadImageUri=displayImageUri
                    // button create movie
                    btnCreate.setOnClickListener(){
                        if (etTitle.text.isNotEmpty() && etDate.text.isNotEmpty() && etDescription.text.isNotEmpty()) {
                            uploadImageUri?.let { uri->
                                addMovie(selectedGenre, uri)
                            }
                        } else {
                            Toast.makeText(requireContext(), "Masukkan Data yang Benar!", Toast.LENGTH_LONG).show()
                        }
                    }
//                    binding.btnSubmit.setOnClickListener {
//                        uploadImage(uploadImageUri!!)
//                        AdminActivity.viewpagers.currentItem = 1
//                    }
                } else {
                    Toast.makeText(requireContext(), "Wajib Ada Gambar!", Toast.LENGTH_SHORT).show()
                }
            }

            // button logout
            btnLogout.setOnClickListener(){
                MainAdminActivity.firebaseAuth.signOut()
                val intentToLoginActivity = Intent(requireContext(), LoginActivity::class.java)
                startActivity(intentToLoginActivity)
                Toast.makeText(requireContext(), "Berhasil Logout!", Toast.LENGTH_SHORT).show()
                finishCurrentActivity()
            }
        }

        return view
    }

    private fun openFileChooser() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        resultLauncher.launch(intent)
    }

    // fungsi add budget
    private fun addMovie(selectedGenre:String, imageUri: Uri) {
        val storageRef = firebaseStorage.reference.child("images/${UUID.randomUUID()}")
        val uploadImageTask = storageRef.putFile(imageUri!!)
        uploadImageTask.addOnSuccessListener {
            storageRef.downloadUrl.addOnSuccessListener {imageUri ->
                var title = binding.etTitle.text.toString()
                var date = binding.etDate.text.toString()
                var description = binding.etDescription.text.toString()
                var genre = selectedGenre
                val movie = Movie(
                                title = title,
                                date = date,
                                description = description,
                                genre = genre,
                                imageUrl = imageUri.toString()
                            )
                movieCollection
                    .add(movie)
                    .addOnSuccessListener { document ->
                        val createMovieId = document.id
                        movie.id = createMovieId
                        document
                            .set(movie)
                            .addOnSuccessListener {
                                resetForm()
                                Toast.makeText(requireContext(), "Success Menambahkan Movie!", Toast.LENGTH_LONG).show()
                                MainAdminActivity.viewPagerAdmin.currentItem = 1
                                Log.d("UploadSuccess", "Image URL: $imageUri")
                            }
                            .addOnFailureListener {
                                Log.d("MainActivity", "Error updating Movie ID: ", it)
                            }
                    }
                    .addOnFailureListener {
                        Log.d("MainActivity", "Error adding Movie: ", it)
                    }
            }
        }
    }

    private fun resetForm(){
        with(binding){
            etDate.text.clear()
            etDescription.text.clear()
            etTitle.text.clear()
            spinnerGenres.setSelection(0)
        }
    }

    private fun finishCurrentActivity() {
        activity?.let {
            if (!it.isFinishing) {
                it.finish()
            }
        }
    }
}