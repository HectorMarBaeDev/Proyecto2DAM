package com.example.pokemonapp.data.api

import com.example.pokemonapp.data.model.*
import com.example.pokemonapp.data.model.UserDto
import retrofit2.http.*

interface ApiService {

    // -------- USERS --------

    @GET("users")
    suspend fun getUsers(): List<UserDto>

    @POST("users")
    suspend fun createUser(@Body user: Map<String, String>): UserDto

    // -------- TEAMS --------

    @GET("teams/user/{userId}")
    suspend fun getTeamsByUser(
        @Path("userId") userId: Long
    ): List<TeamDto>

    @POST("teams")
    suspend fun createTeam(
        @Query("userId") userId: Long,
        @Body team: Map<String, String>
    ): TeamDto

    // -------- POKEMON --------

    @GET("pokemon/team/{teamId}")
    suspend fun getPokemonByTeam(
        @Path("teamId") teamId: Long
    ): List<PokemonDto>

    @POST("pokemon")
    suspend fun addPokemon(
        @Query("teamId") teamId: Long,
        @Body request: Map<String, String>
    ): PokemonDto

    @DELETE("pokemon/{id}")
    suspend fun deletePokemon(
        @Path("id") pokemonId: Long
    )
    // AUTH
    @POST("auth/register")
    suspend fun register(@Body body: Map<String, String>)

    @POST("auth/login-check")
    suspend fun loginCheck()


}