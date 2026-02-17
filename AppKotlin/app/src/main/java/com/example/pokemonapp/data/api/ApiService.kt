package com.example.pokemonapp.data.api

import com.example.pokemonapp.data.model.*
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    // ======================
    // AUTH
    // ======================

    @POST("auth/login")
    suspend fun login(@Body body: Map<String, String>): AuthResponseDto

    @POST("auth/register")
    suspend fun register(@Body body: Map<String, String>)

    // ======================
    // USERS
    // ======================

    @GET("users")
    suspend fun getUsers(): List<UserDto>

    @Multipart
    @POST("users/me/profile-picture")
    suspend fun uploadProfilePicture(
        @Part file: MultipartBody.Part
    ): Unit

    // Obtener foto de perfil
    @GET("users/{id}/profile-picture")
    suspend fun getProfilePicture(
        @Path("id") id: Long
    ): Response<ResponseBody>

    // ======================
    // TEAMS
    // ======================

    @GET("teams/me")
    suspend fun getMyTeams(): List<TeamDto>

    @POST("teams")
    suspend fun createTeam(
        @Body body: Map<String, String>
    ): TeamDto

    // ======================
    // POKEMON
    // ======================

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

    @GET("pokemon")
    suspend fun getAllPokemon(): List<PokemonListItemDto>

    @GET("pokemon/{id}")
    suspend fun getPokemonById(
        @Path("id") id: Long
    ): PokemonDto

    @PUT("pokemon/{id}")
    suspend fun updatePokemon(
        @Path("id") id: Long,
        @Body pokemon: PokemonDto
    ): PokemonDto

}
