package com.example.uas_papb_2023

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.map
import com.example.uas_papb_2023.adapter.RvAdminAdapter
import com.example.uas_papb_2023.dataClass.Movie
import com.example.uas_papb_2023.database.MovieConverter
import com.example.uas_papb_2023.database.MovieRoom
import com.example.uas_papb_2023.database.MovieRoomDao
import com.example.uas_papb_2023.database.MovieRoomDatabase
import com.example.uas_papb_2023.databinding.FragmentReadAdminBinding
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ReadAdminFragment : Fragment() {
    private lateinit var binding: FragmentReadAdminBinding
    private var firebaseFirestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val movieCollection = firebaseFirestore.collection("movies")
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
        binding = FragmentReadAdminBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeMovie()
        observeMoviesChanges()
    }

    private fun observeMoviesChanges() {
        movieCollection.addSnapshotListener{ snapshots, error ->
            // jika dia terjadi error maka dia akan memunculkan log
            if(error != null){
                Log.d("MainAdminActivity", "Error listening for movies changes: ", error)
                return@addSnapshotListener
            }
            // jika gak error maka akan langsung ke sini
            val movies = snapshots?.toObjects(Movie::class.java)
            val roomMovies = MovieConverter.convertMovieListToRoom(movies)

            if (movies != null) {
//                movieListLiveData.postValue(movies)
                for (movie in movies) {
                    Log.d("MainAdminActivity", "Movie: $movie")
                }
                saveMoviesToRoom(roomMovies)
            }
        }
    }

    private fun saveMoviesToRoom(roomMovies: List<MovieRoom>) {
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
    private fun observeMovie(){
        movieListLiveData.observe(this){ movies ->
            // Setel data ke RecyclerView Adapter
            val adapter = RvAdminAdapter(movies,
                onClickItemMovie =  { movie->
                    // Implementasi ketika item diklik
                    val intentToDetailFilmActivity = Intent(context, DetailFilmActivity::class.java)
                    intentToDetailFilmActivity.putExtra("EXT_MOVIE", movie)
                    startActivity(intentToDetailFilmActivity)
                }
                , onLongClickItemMovie = { movie->
                    // Implementasi ketika item diklik lama
                    val viewPagerAdmin = MainAdminActivity.viewPagerAdmin
                    // Mendapatkan referensi ke fragment ke-0 dalam ViewPager
                    val crudFragment = viewPagerAdmin.adapter?.instantiateItem(viewPagerAdmin, 0) as CrudFragment
                    crudFragment.setDataUpdate(movie)
                    viewPagerAdmin.currentItem = 0
                })
            binding.rvMovie.adapter = adapter
        }
    }




}