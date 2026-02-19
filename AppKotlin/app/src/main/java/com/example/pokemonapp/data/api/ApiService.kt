package com.example.pokemonapp.data.api

import com.example.pokemonapp.model.AuthResponseDto
import com.example.pokemonapp.model.PokemonDto
import com.example.pokemonapp.model.PokemonListItemDto
import com.example.pokemonapp.model.TeamDto
import com.example.pokemonapp.model.UserDto
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
    )

    @GET("users/{id}/profile-picture")
    suspend fun getProfilePicture(
        @Path("id") id: Long
    ): Response<ResponseBody>

    // ======================
    // TEAMS
    // ======================

    @DELETE("teams/{id}")
    suspend fun deleteTeam(
        @Path("id") id: Long
    )

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

    // 🔁 RUTAS ACTUALIZADAS

    @DELETE("pokemon/id/{id}")
    suspend fun deletePokemon(
        @Path("id") pokemonId: Long
    )

    @GET("pokemon/id/{id}")
    suspend fun getPokemonById(
        @Path("id") id: Long
    ): PokemonDto

    @PUT("pokemon/id/{id}")
    suspend fun updatePokemon(
        @Path("id") id: Long,
        @Body pokemon: PokemonDto
    ): PokemonDto

    // Movimientos
    @GET("pokemon/{id}/moves")
    suspend fun getPokemonMoves(
        @Path("id") id: Long
    ): List<String>



    @GET("pokemon/id/{id}/abilities")
    suspend fun getPokemonAbilities(
        @Path("id") id: Long
    ): List<String>

    // ======================
    // ITEMS PAGINADOS
    // ======================

    @GET("pokemon/competitive-items")
    suspend fun getCompetitiveItems(): List<String>


    // ======================
    // INDEX
    // ======================

    @GET("pokemon")
    suspend fun getAllPokemon(): List<PokemonListItemDto>


}
