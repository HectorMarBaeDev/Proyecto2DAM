package com.example.pokemonapp.data.repository

import com.example.pokemonapp.data.api.RetrofitInstance
import com.example.pokemonapp.data.model.*

class PokemonRepository {

    private val api = RetrofitInstance.api

    // USERS
    suspend fun getUsers(): List<UserDto> =
        api.getUsers()

    suspend fun createUser(username: String, email: String): UserDto =
        api.createUser(
            mapOf(
                "username" to username,
                "email" to email
            )
        )

    // TEAMS
    suspend fun getTeamsByUser(userId: Long): List<TeamDto> =
        api.getTeamsByUser(userId)

    suspend fun createTeam(userId: Long, name: String, format: String): TeamDto =
        api.createTeam(
            userId,
            mapOf(
                "name" to name,
                "format" to format
            )
        )

    // POKEMON
    suspend fun getPokemonByTeam(teamId: Long): List<PokemonDto> =
        api.getPokemonByTeam(teamId)

    suspend fun addPokemon(teamId: Long, identifier: String): PokemonDto =
        api.addPokemon(
            teamId,
            mapOf("identifier" to identifier)
        )

    suspend fun deletePokemon(pokemonId: Long) =
        api.deletePokemon(pokemonId)
}
