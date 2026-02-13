package com.example.pokemonapp.data.api

import android.util.Log
import com.example.pokemonapp.data.auth.AuthManager
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitInstance {

    private const val BASE_URL =
        "https://pokemon-backend-849x.onrender.com/api/"

    fun create(): ApiService {

        val client = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val requestBuilder = chain.request().newBuilder()

                Log.d("JWT_DEBUG", "Token actual: ${AuthManager.jwt}")

                AuthManager.jwt?.let {
                    requestBuilder.addHeader(
                        "Authorization",
                        "Bearer $it"
                    )
                }

                chain.proceed(requestBuilder.build())
            }
            .build()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
            .create(ApiService::class.java)

        /*val clientBuilder = OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)


        if (!username.isNullOrBlank() && !password.isNullOrBlank()) {
            clientBuilder.addInterceptor(
                BasicAuthInterceptor(username, password)
            )
        }

        val client = clientBuilder.build()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)*/
    }
}
