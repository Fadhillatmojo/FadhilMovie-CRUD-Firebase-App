package com.example.uas_papb_2023

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.example.uas_papb_2023.dataClass.Movie
import com.example.uas_papb_2023.databinding.ActivityDetailFilmBinding

class DetailFilmActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailFilmBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailFilmBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // mengambil data intent
        val intent =  intent
        val movie = intent.getSerializableExtra("EXT_MOVIE") as Movie

        with(binding){
            Glide.with(this@DetailFilmActivity).load(movie.imageUrl).into(imageFilm)
            tvTitle.text = movie.title
            tvRilisDate.text = movie.date
            tvDesc.text = movie.description
            tvGenre.text = movie.genre
            btnBack.setOnClickListener {
                finish()
            }
        }

    }
}