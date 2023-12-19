package com.example.uas_papb_2023.database

import com.example.uas_papb_2023.dataClass.Movie

object MovieConverter {
    fun convertMovieListToRoom(movieList: List<Movie>?): List<MovieRoom> {
        return movieList?.mapNotNull { movie ->
            // Map and filter out null results
            convertMovieToRoom(movie)
        } ?: emptyList()
    }

    private fun convertMovieToRoom(movie: Movie?): MovieRoom? {
        return movie?.let {
            MovieRoom(
                id = it.id,
                title = it.title,
                date = it.date,
                description = it.description,
                genre = it.genre,
                imageUrl = it.imageUrl
            )
        }
    }

    fun convertRoomMovieListToMovie(roomMovieList: List<MovieRoom>?): List<Movie> {
        return roomMovieList?.mapNotNull { roomMovie ->
            // Map and filter out null results
            convertRoomMovieToMovie(roomMovie)
        } ?: emptyList()
    }

    private fun convertRoomMovieToMovie(roomMovie: MovieRoom?): Movie? {
        return roomMovie?.let {
            Movie(
                id = it.id,
                title = it.title,
                date = it.date,
                description = it.description,
                genre = it.genre,
                imageUrl = it.imageUrl
            )
        }
    }


}