package com.example.uas_papb_2023.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.uas_papb_2023.dataClass.Movie
import com.example.uas_papb_2023.databinding.ItemMovieBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
typealias onClickItemMovie = (Movie) -> Unit
typealias onLongClickItemMovie = (Movie) -> Unit

// menerima dua buah parameter, yakni list disasternya, sama onclickdisaster,
// jadi ketika itemnya itu di click dia dapat menjalankan suatu fungsi
class RvAdminAdapter (private val listMovie: List<Movie>, private val onClickItemMovie: onClickItemMovie, private val onLongClickItemMovie:onLongClickItemMovie) :
    RecyclerView.Adapter<RvAdminAdapter.ItemDisasterViewHolder>(){
    private var firebaseFirestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val movieCollection = firebaseFirestore.collection("movies")
    inner class ItemDisasterViewHolder(private val binding: ItemMovieBinding) : RecyclerView.ViewHolder(binding.root)
    {
        fun bind (movie: Movie){
            with(binding){
                txtTitle.text = movie.title
                txtDate.text = movie.date
                txtGenre.text = movie.genre
                itemView.setOnClickListener{
                    onClickItemMovie(movie)
                }
                itemView.setOnLongClickListener{
                    onLongClickItemMovie(movie)
                    true
                }
            }
            Glide.with(itemView.context).load(movie.imageUrl).into(binding.imageMovie)
            with(binding){
                btnDelete.setOnClickListener {
                    deleteMovie(movie, itemView.context)
                }
            }
        }
    }

    // untuk menyusun yang akan ditampilkan
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemDisasterViewHolder
    {
        val binding = ItemMovieBinding.inflate(
            LayoutInflater.from(parent.context),
            parent, false)

        return ItemDisasterViewHolder(binding)
    }

    // berisi jumlah item yang akan diisi nantinya
    override fun getItemCount(): Int = listMovie.size

    override fun onBindViewHolder(holder: ItemDisasterViewHolder, position: Int) {
        holder.bind(listMovie[position])
    }

    // fungsi delete movie
    private fun deleteMovie(movie: Movie, context: Context) {
        if (movie.id.isEmpty()) {
            Log.d("MainActivityAdmin", "Error deleting: movie ID is empty!")
            return
        }
        val imageUrl = movie.imageUrl
        movieCollection.document(movie.id)
            .delete()
            .addOnSuccessListener {
                if (imageUrl.isNotEmpty()) {
                    deleteImageFromStorage(imageUrl)
                    Toast.makeText(context, "Movie Berhasil Dihapus", Toast.LENGTH_SHORT).show()
                    Log.d("MainActivityAdmin", "Success Add Image: $imageUrl")
                }
            }
            .addOnFailureListener {
                Log.d("MainActivityAdmin", "Error deleting movie: ", it)
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
}