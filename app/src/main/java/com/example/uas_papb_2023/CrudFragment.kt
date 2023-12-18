package com.example.uas_papb_2023

import android.R
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
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
                    //button update movie
                    btnUpdate.setOnClickListener {
                        if (etTitle.text.isNotEmpty() && etDate.text.isNotEmpty() && etDescription.text.isNotEmpty()){
                            uploadImageUri?.let { updateMovie(selectedGenre, it) }
                        } else {
                            Toast.makeText(requireContext(), "Masukkan Data yang Benar!", Toast.LENGTH_LONG).show()
                        }
                    }
                } else {
                    Toast.makeText(requireContext(), "Wajib Ada Gambar!", Toast.LENGTH_SHORT).show()
                }
            }

            // button logout
            btnLogout.setOnClickListener{
                MainAdminActivity.firebaseAuth.signOut()
                // Di dalam aktivitas login setelah pengguna berhasil login
                val context: Context = requireActivity()
                val sharedPref = context.getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
                val userLogin = sharedPref.edit()
                // Set isLoggedIn menjadi true setelah berhasil login
                userLogin.putBoolean("isAdminLoggedIn", false)
                userLogin.apply()
                val intentToLoginActivity = Intent(requireContext(), LoginActivity::class.java)
                startActivity(intentToLoginActivity)
                Toast.makeText(requireContext(), "Berhasil Logout!", Toast.LENGTH_SHORT).show()
                finishCurrentActivity()
            }
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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

    private fun updateMovie(selectedGenre:String, imageUri: Uri){
        val storageRef = firebaseStorage.reference.child("images/${UUID.randomUUID()}")
        val uploadImageTask = storageRef.putFile(imageUri!!)
        uploadImageTask.addOnSuccessListener {
            storageRef.downloadUrl.addOnSuccessListener {imageUri ->
                val updateId = binding.tvIdFilm.text.toString()
                var title = binding.etTitle.text.toString()
                var date = binding.etDate.text.toString()
                var description = binding.etDescription.text.toString()
                var genre = selectedGenre
                val movieToUpdate = Movie(
                    id = updateId,
                    title = title,
                    date = date,
                    description = description,
                    genre = genre,
                    imageUrl = imageUri.toString()
                )

                val imageUriOld = binding.tvUriImage.text.toString()
                movieCollection
                    .document(updateId)
                    .set(movieToUpdate)
                    .addOnSuccessListener {
                        resetForm()
                        deleteImageFromStorage(imageUriOld)
                        Toast.makeText(requireContext(), "Success Update Movie!", Toast.LENGTH_LONG).show()
                        MainAdminActivity.viewPagerAdmin.currentItem = 1
                        Log.d("UploadSuccess", "Image URL: $imageUri")
                    }
                    .addOnFailureListener {exception ->
                        Log.d("MainActivity", "Error updating Movie: ", exception)
                    }
            }
        }
    }

    // Fungsi untuk menghapus gambar dari Firebase Storage berdasarkan URL gambar
    private fun deleteImageFromStorage(imageUrl: String) {
        // Dapatkan referensi ke Firebase Storage
        val firebaseStorage = FirebaseStorage.getInstance()
        val storageRef = firebaseStorage.getReferenceFromUrl(imageUrl)

        // Hapus gambar dari Firebase Storage
        storageRef.delete()
            .addOnSuccessListener {
                Log.d("MainActivityAdmin", "Gambar Dihapus dari storage")
            }
            .addOnFailureListener {
                Log.d("MainActivityAdmin", "Error deleting image from storage: ", it)
            }
    }

    private fun resetForm(){
        with(binding){
            tvIdFilm.text = null
            tvUriImage.text = null
            etDate.text.clear()
            etDescription.text.clear()
            etTitle.text.clear()
            spinnerGenres.setSelection(0)
            val imageDefault = com.example.uas_papb_2023.R.drawable.add_image
            btnPickImage.setImageResource(imageDefault)
            btnCreate.visibility = View.VISIBLE
            btnUpdate.visibility = View.GONE
        }
    }

    fun setDataUpdate(movie:Movie){
        with(binding){
            Glide.with(requireContext()).load(movie.imageUrl).into(btnPickImage)
            tvIdFilm.text = movie.id
            tvUriImage.text = movie.imageUrl
            etTitle.setText(movie.title)
            etDate.setText(movie.date)
            etDescription.setText(movie.description)
            val dataGenre = movie.genre // Ganti dengan nilai yang sesuai dari Firebase
            val selectedIndex = genres.indexOf(dataGenre)
            if (selectedIndex != -1) {
                spinnerGenres.setSelection(selectedIndex)
            }
            btnCreate.visibility = View.GONE
            btnUpdate.visibility = View.VISIBLE
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