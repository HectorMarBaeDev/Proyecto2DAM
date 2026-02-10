package com.example.pokemonapp.data.repository

import com.example.pokemonapp.data.api.RetrofitInstance
import com.example.pokemonapp.data.auth.AuthManager
import com.example.pokemonapp.data.model.*

class PokemonRepository {

    // ======================
    // AUTH
    // ======================

    suspend fun login(username: String, password: String) {

        // 1️⃣ Login por JSON (SIN Basic Auth)
        val publicApi = RetrofitInstance.create(null)

        val jwt = publicApi.login(
            mapOf(
                "username" to username,
                "password" to password
            )
        )

        AuthManager.jwt = jwt

        /*publicApi.login(
            mapOf(
                "username" to username,
                "password" to password
            )
        )

        // 2️⃣ Guardamos credenciales
        AuthManager.username = username
        AuthManager.password = password

        // 3️⃣ Usamos Basic Auth para obtener datos protegidos
        val securedApi = RetrofitInstance.create(username, password)
        val users = securedApi.getUsers()

        return users.first { it.username == username }*/
    }

    suspend fun register(username: String, email: String, password: String) {
        RetrofitInstance.create(null).register(
            mapOf(
                "username" to username,
                "email" to email,
                "password" to password
            )
        )

        /*val api = RetrofitInstance.create(null, null)
        api.register(
            mapOf(
                "username" to username,
                "email" to email,
                "password" to password
            )
        )*/
    }

    // ======================
    // HELPERS
    // ======================

    /*private fun api() =
        RetrofitInstance.create(
            AuthManager.username,
            AuthManager.password
        )*/

    // ======================
    // TEAMS
    // ======================

    /*suspend fun getTeamsByUser(userId: Long): List<TeamDto> =
        api().getTeamsByUser(userId)

    suspend fun createTeam(
        userId: Long,
        name: String,
        format: String
    ): TeamDto =
        api().createTeam(
            userId,
            mapOf(
                "name" to name,
                "format" to format
            )
        )

    // ======================
    // POKEMON
    // ======================

    suspend fun getPokemonByTeam(teamId: Long): List<PokemonDto> =
        api().getPokemonByTeam(teamId)

    suspend fun addPokemon(
        teamId: Long,
        identifier: String
    ): PokemonDto =
        api().addPokemon(
            teamId,
            mapOf("identifier" to identifier)
        )

    suspend fun deletePokemon(pokemonId: Long) {
        api().deletePokemon(pokemonId)
    }*/
}
