package com.example.uas_papb_2023.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.uas_papb_2023.R
import com.example.uas_papb_2023.dataClass.Movie
import com.example.uas_papb_2023.databinding.ItemMovieUserBinding
import com.google.firebase.firestore.FirebaseFirestore


class RvUserAdapter (private val listMovie: List<Movie>, private val onClickItemMovie: onClickItemMovie) :
    RecyclerView.Adapter<RvUserAdapter.ItemDisasterViewHolder>(){
    private var firebaseFirestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val movieCollection = firebaseFirestore.collection("movies")
    private val bookmarkCollection = firebaseFirestore.collection("bookmarks")
    inner class ItemDisasterViewHolder(private val binding: ItemMovieUserBinding) : RecyclerView.ViewHolder(binding.root)
    {
        private var isBookmarked = false

        fun bind (movie: Movie){

            with(binding){
                txtTitle.text = movie.title
                txtGenre.text = movie.genre

                itemView.setOnClickListener{
                    onClickItemMovie(movie)
                }
                val sharedPref = itemView.context.getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
                val userId = sharedPref?.getString("userId", "null")

                checkBookmarkStatus(userId!!, movie.id)
                layoutBookmark.setOnClickListener {
                    toggleBookmark(userId!!, movie)
//                    createBookmark(userId!!, movie)
                }
            }
            Glide.with(itemView.context).load(movie.imageUrl).into(binding.imageMovie)
        }

        private fun updateIconBookmark(bookmarkIcon: ImageView) {
            val drawableId = if (isBookmarked) R.drawable.ic_bookmark else R.drawable.ic_bookmark_outline
            bookmarkIcon.setImageResource(drawableId)
        }

        private fun checkBookmarkStatus(userId: String, movieId: String) {
            bookmarkCollection
                .whereEqualTo("userId", userId)
                .whereEqualTo("filmId", movieId)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    isBookmarked = !querySnapshot.isEmpty
                    updateIconBookmark(binding.bookmark)
                }
                .addOnFailureListener { e ->
                    Log.w("MainUserActivity", "Error checking bookmark status", e)
                }
        }

        private fun toggleBookmark(userId: String, movie: Movie) {
            if (isBookmarked) {
                // Jika sudah di-bookmark, hapus bookmark
                removeBookmark(userId, movie.id)
            } else {
                // Jika belum di-bookmark, tambahkan bookmark
                createBookmark(userId, movie)
            }
            isBookmarked = !isBookmarked
            updateIconBookmark(binding.bookmark)
        }

        private fun removeBookmark(userId: String, movieId: String) {
            // Hapus bookmark berdasarkan userId dan movieId
            bookmarkCollection
                .whereEqualTo("userId", userId)
                .whereEqualTo("filmId", movieId)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    for (document in querySnapshot) {
                        document.reference.delete()
                    }
                }
                .addOnFailureListener { e ->
                    Log.w("MainUserActivity", "Error removing bookmark", e)
                }
        }
        private fun createBookmark(userId:String, movie: Movie) {
            if (movie.id.isEmpty()) {
                Log.d("MainMovieUser", "Error Bookmark!")
                return
            }
            val bookmarkData = hashMapOf(
                "userId" to userId,
                "filmId" to movie.id
            )

            bookmarkCollection
                .add(bookmarkData)
                .addOnSuccessListener { documentReference ->
                    val bookmarkId = documentReference.id
                    Log.d("MainUserActivity", "Bookmark berhasil ditambahkan! Generated ID: $bookmarkId")
                }
                .addOnFailureListener { e ->
                    Log.w("MainUserActivity", "Error adding bookmark", e)
                }

            // toast untuk menandakan berhasil ditambahkan ke bookmark
            Toast.makeText(itemView.context, "Berhasil Dibookmark!", Toast.LENGTH_SHORT).show()
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

