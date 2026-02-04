package com.example.pokemonapp.data.repository

import com.example.pokemonapp.data.api.RetrofitInstance
import com.example.pokemonapp.data.auth.AuthManager
import com.example.pokemonapp.data.model.*

class PokemonRepository {

    private fun api() =
        RetrofitInstance.create(
            AuthManager.username!!,
            AuthManager.password!!
        )

    // ======================
    // AUTH
    // ======================

    suspend fun login(username: String, password: String): UserDto {

        println("➡️ Intentando login con usuario=$username")

        val api = RetrofitInstance.create(username, password)

        println("➡️ Llamando a /auth/login-check")
        api.loginCheck()   // aquí suele fallar

        println("✅ Login-check OK, pidiendo usuarios")

        val users = api.getUsers()

        println("📦 Usuarios recibidos: ${users.map { it.username }}")

        return users.first { it.username == username }
    }

    suspend fun register(username: String, email: String, password: String) {
        RetrofitInstance.create("", "").register(
            mapOf(
                "username" to username,
                "email" to email,
                "password" to password
            )
        )
    }

    // ======================
    // TEAMS
    // ======================

    suspend fun getTeamsByUser(userId: Long): List<TeamDto> =
        api().getTeamsByUser(userId)

    suspend fun createTeam(userId: Long, name: String, format: String): TeamDto =
        api().createTeam(
            userId,
            mapOf("name" to name, "format" to format)
        )

    // ======================
    // POKEMON
    // ======================

    suspend fun getPokemonByTeam(teamId: Long): List<PokemonDto> =
        api().getPokemonByTeam(teamId)

    suspend fun addPokemon(teamId: Long, identifier: String): PokemonDto =
        api().addPokemon(
            teamId,
            mapOf("identifier" to identifier)
        )

    suspend fun deletePokemon(pokemonId: Long) {
        api().deletePokemon(pokemonId)
    }
}
