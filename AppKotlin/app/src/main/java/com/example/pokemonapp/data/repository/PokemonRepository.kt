package com.example.pokemonapp.data.repository

import android.net.Uri
import android.util.Log
import com.example.pokemonapp.data.api.ApiService
import com.example.pokemonapp.data.auth.AuthManager
import com.example.pokemonapp.data.model.PokemonDto
import com.example.pokemonapp.data.model.PokemonListItemDto
import com.example.pokemonapp.data.model.TeamDto
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

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
    // USERS
    // ======================

    suspend fun uploadProfilePicture(uri: Uri, context: android.content.Context) {
        // Convertimos Uri a File temporal
        val file = uriToFile(uri, context)

        val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
        val body = MultipartBody.Part.createFormData("file", file.name, requestFile)

        val response = RetrofitInstance.createWithToken().uploadProfilePicture(body) // tu API de Retrofit
        if (!response.isSuccessful) {
            throw Exception("Error subiendo imagen: ${response.code()}")
        }
    }


    fun uriToFile(uri: Uri, context: android.content.Context): File {
        val inputStream = context.contentResolver.openInputStream(uri)
            ?: throw IllegalArgumentException("No se pudo abrir el URI")
        val file = File(context.cacheDir, "temp_image_${System.currentTimeMillis()}.jpg")
        inputStream.use { input ->
            file.outputStream().use { output ->
                input.copyTo(output)
            }
        }
        return file
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

    suspend fun getAllPokemon(): List<PokemonListItemDto> {
        val api = RetrofitInstance.createWithToken()
        return api.getAllPokemon()
    }
}
