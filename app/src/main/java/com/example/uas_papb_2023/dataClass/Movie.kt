package com.example.uas_papb_2023.dataClass

import java.io.Serializable

data class Movie(
    var id: String = "",
    var title: String = "",
    var date: String = "",
    var description: String = "",
    var genre: String = "",
    var imageUrl: String = ""
): Serializable
