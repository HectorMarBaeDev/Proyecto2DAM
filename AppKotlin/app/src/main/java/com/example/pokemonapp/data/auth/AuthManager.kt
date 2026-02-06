package com.example.pokemonapp.data.auth

object AuthManager {
    var username: String? = null
    var password: String? = null

    fun isLogged(): Boolean =
        !username.isNullOrBlank() && !password.isNullOrBlank()

    fun logout() {
        username = null
        password = null
    }
}
