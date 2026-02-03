package com.example.pokemonapp.data.api

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {

    private const val BASE_URL =
        "https://pokemon-backend-849x.onrender.com/api/"

    fun create(
        username: String,
        password: String
    ): ApiService {

        val client = OkHttpClient.Builder()
            .addInterceptor(BasicAuthInterceptor(username, password))
            .build()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}
