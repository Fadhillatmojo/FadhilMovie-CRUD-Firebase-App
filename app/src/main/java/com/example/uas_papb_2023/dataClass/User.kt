package com.example.uas_papb_2023.dataClass

import java.io.Serializable

data class User(
    var id: String = "",
    var email: String = "",
    var username: String = "",
    var role: String = ""
): Serializable {
    override fun toString(): String {
        return "Email: $email" +
                "\n" +
                "Username: $username" +
                "\n" +
                "Role: $role"
    }
}

