package com.example.pokemonapp.data.repository

import android.content.Context
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
            AuthManager.userId = response.userId
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

    suspend fun uploadProfilePicture(uri: Uri, context: Context) {
        if (AuthManager.jwt == null) throw Exception("No hay token disponible para subir imagen")

        val file = uriToFile(uri, context)
        val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
        val body = MultipartBody.Part.createFormData("file", file.name, requestFile)

        // ✅ Llamada que lanza excepción si hay 403/401
        RetrofitInstance.createWithToken().uploadProfilePicture(body)

        Log.d("Upload", "Imagen subida correctamente")
    }



    fun uriToFile(uri: Uri, context: Context): File {
        val inputStream = context.contentResolver.openInputStream(uri)
            ?: throw IllegalArgumentException("No se puede abrir el archivo")
        val tempFile = File(context.cacheDir, "temp_upload.jpg")
        tempFile.outputStream().use { output ->
            inputStream.copyTo(output)
        }
        return tempFile
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

    suspend fun getPokemonById(id: Long): PokemonDto {
        val api = RetrofitInstance.createWithToken()
        return api.getPokemonById(id)
    }

    suspend fun updatePokemon(pokemon: PokemonDto): PokemonDto {
        val api = RetrofitInstance.createWithToken()
        return api.updatePokemon(pokemon.id, pokemon)
    }

}
