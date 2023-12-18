package com.example.uas_papb_2023.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.uas_papb_2023.dataClass.Movie
import com.example.uas_papb_2023.databinding.ItemMovieUserBinding
import com.google.firebase.firestore.FirebaseFirestore


class RvUserAdapter (private val listMovie: List<Movie>, private val onClickItemMovie: onClickItemMovie) :
    RecyclerView.Adapter<RvUserAdapter.ItemDisasterViewHolder>(){
    private var firebaseFirestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val movieCollection = firebaseFirestore.collection("movies")
    inner class ItemDisasterViewHolder(private val binding: ItemMovieUserBinding) : RecyclerView.ViewHolder(binding.root)
    {
        fun bind (movie: Movie){
            with(binding){
                txtTitle.text = movie.title
                txtDate.text = movie.date
                txtGenre.text = movie.genre

                itemView.setOnClickListener{
                    onClickItemMovie(movie)
                }
            }
            Glide.with(itemView.context).load(movie.imageUrl).into(binding.imageMovie)

        }
    }

    // untuk menyusun yang akan ditampilkan
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RvUserAdapter.ItemDisasterViewHolder
    {
        val binding = ItemMovieUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ItemDisasterViewHolder(binding)
    }

    // berisi jumlah item yang akan diisi nantinya
    override fun getItemCount(): Int = listMovie.size

    override fun onBindViewHolder(holder: RvUserAdapter.ItemDisasterViewHolder, position: Int) {
        holder.bind(listMovie[position])
    }
}

