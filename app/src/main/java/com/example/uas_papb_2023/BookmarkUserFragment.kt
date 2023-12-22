package com.example.uas_papb_2023

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import com.example.uas_papb_2023.adapter.RvUserAdapter
import com.example.uas_papb_2023.dataClass.Movie
import com.example.uas_papb_2023.database.MovieConverter
import com.example.uas_papb_2023.database.MovieRoom
import com.example.uas_papb_2023.database.MovieRoomDao
import com.example.uas_papb_2023.database.MovieRoomDatabase
import com.example.uas_papb_2023.databinding.FragmentBookmarkUserBinding
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BookmarkUserFragment : Fragment() {
    private lateinit var binding: FragmentBookmarkUserBinding
    private var firebaseFirestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val movieCollection = firebaseFirestore.collection("movies")
    private val bookmarkCollection = firebaseFirestore.collection("bookmarks")
    private val movieListLiveData: MutableLiveData<List<Movie>> by lazy {
        MutableLiveData<List<Movie>>()
    }
    private lateinit var roomMovieDao: MovieRoomDao
    private lateinit var roomDatabase: MovieRoomDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Inisialisasi Room Database dan DAO
        roomDatabase = MovieRoomDatabase.getDatabase(requireContext())!!
        roomMovieDao = roomDatabase.movieRoomDao()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // membuat binding
        binding = FragmentBookmarkUserBinding.inflate(inflater, container, false)
        val sharedPref = activity?.getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
        val userId = sharedPref?.getString("userId", "null")
        observeBookmarkMovie()
        observeMovieBookmarkChanges(userId!!)
        return binding.root
    }

    private fun observeMovieBookmarkChanges(userId:String) {
        val context = context ?: return // pemeriksaan untuk memastikan konteks masih terlampir
        bookmarkCollection
            .whereEqualTo("userId", userId)
            .addSnapshotListener{ snapshots, error ->
            // jika dia terjadi error maka dia akan memunculkan log
            if(error != null){
                Log.d("MainUserActivity", "Error listening for movies changes: ", error)
                return@addSnapshotListener
            }
            // jika gak error maka akan langsung ke sini
            val idFilmList = snapshots?.documents?.map { it.getString("filmId") }

            // Jika ada idFilm, ambil data dari koleksi movies
            if (idFilmList != null && idFilmList.isNotEmpty()) {
                binding.ivEmptyBookmark.visibility = View.GONE
                movieCollection
                    .whereIn("id", idFilmList)
                    .get()
                    .addOnSuccessListener { moviesSnapshots ->
                        val movies = moviesSnapshots.toObjects(Movie::class.java)
                        val roomMovies = MovieConverter.convertMovieListToRoom(movies)

                        saveMoviesBookmarkToRoom(roomMovies)
                    }
                    .addOnFailureListener { e ->
                        Log.d("BookmarkUserFragment", "Error getting movies: ", e)
                    }
            } else {
                binding.ivEmptyBookmark.visibility = View.VISIBLE
            }
        }
    }
    private fun saveMoviesBookmarkToRoom(roomMovies: List<MovieRoom>) {
        // Simpan data ke Room
        lifecycleScope.launch(Dispatchers.IO) {
            // Hapus semua data di Room terlebih dahulu
            roomMovieDao.deleteAll()
            roomMovieDao.insertAll(roomMovies)

            // convert list movie room ke list movie
            var roomMoviesGet:List<MovieRoom> = roomMovieDao.getAllMovies()
            var movies: List<Movie> = MovieConverter.convertRoomMovieListToMovie(roomMoviesGet)
            movieListLiveData.postValue(movies)
        }
    }
    private fun observeBookmarkMovie(){
        movieListLiveData.observe(this){ movies ->
            // Setel data ke RecyclerView Adapter
            val adapter = RvUserAdapter(movies,
                onClickItemMovie =  { movie->
                    // Implementasi ketika item diklik
                    val intentToDetailFilmActivity = Intent(context, DetailFilmActivity::class.java)
                    intentToDetailFilmActivity.putExtra("EXT_MOVIE", movie)
                    startActivity(intentToDetailFilmActivity)
                })
            binding.rvMovieBookmark.adapter = adapter
        }
    }
}