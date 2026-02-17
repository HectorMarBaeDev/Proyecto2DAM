package com.example.pokemonapp.data.repository

import android.util.Log
import com.example.pokemonapp.data.auth.AuthManager
import com.example.pokemonapp.data.model.PokemonDto
import com.example.pokemonapp.data.model.TeamDto

class PokemonRepository {

    // ======================
    // AUTH
    // ======================

    suspend fun login(username: String, password: String): Long {
        val api = RetrofitInstance.create()

        try {
            val response = api.login(
                mapOf(
                    "username" to username,
                    "password" to password
                )
            )

            AuthManager.jwt = response.token
            return response.userId

        } catch (e: Exception) {
            Log.e("LOGIN_ERROR", e.toString())
            throw e
        }
    }


    suspend fun register(
        username: String,
        email: String,
        password: String
    ) {
        RetrofitInstance.create().register(
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

    suspend fun getTeamsByUser(): List<TeamDto> {
        val api = RetrofitInstance.createWithToken() // token en el header
        return api.getMyTeams()
    }

    suspend fun createTeam(
        userId: Long = 0, // lo ignoramos
        name: String,
        format: String
    ): TeamDto {
        val api = RetrofitInstance.createWithToken()
        return api.createTeam(
            mapOf(
                "name" to name,
                "format" to format
            )
        )
    }


    // ======================
    // POKEMON
    // ======================

    suspend fun getPokemonByTeam(teamId: Long): List<PokemonDto> {
        val api = RetrofitInstance.createWithToken()
        return api.getPokemonByTeam(teamId)
    }

    suspend fun addPokemon(
        teamId: Long,
        identifier: String
    ): PokemonDto {
        val api = RetrofitInstance.createWithToken()

        return api.addPokemon(
            teamId,
            mapOf("identifier" to identifier)
        )
    }

    suspend fun deletePokemon(pokemonId: Long) {
        val api = RetrofitInstance.createWithToken()
        api.deletePokemon(pokemonId)
    }
}
