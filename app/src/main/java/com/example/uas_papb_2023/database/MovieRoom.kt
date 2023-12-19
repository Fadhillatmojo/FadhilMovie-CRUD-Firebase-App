package com.example.uas_papb_2023.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "movie_table")
data class MovieRoom(
    @PrimaryKey var id: String = "",
    var title: String = "",
    var date: String = "",
    var description: String = "",
    var genre: String = "",
    var imageUrl: String = ""
): Serializable
