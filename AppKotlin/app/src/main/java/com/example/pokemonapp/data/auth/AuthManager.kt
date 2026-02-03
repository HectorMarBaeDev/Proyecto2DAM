package com.example.pokemonapp.data.auth

object AuthManager {

    var username: String? = null
        private set

    var password: String? = null
        private set

    fun login(user: String, pass: String) {
        username = user
        password = pass
    }

    fun logout() {
        username = null
        password = null
    }

    fun isLogged(): Boolean =
        username != null && password != null
}
