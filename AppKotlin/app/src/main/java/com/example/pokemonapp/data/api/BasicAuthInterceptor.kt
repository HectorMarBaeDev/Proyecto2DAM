package com.example.pokemonapp.data.api

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.pokemonapp.data.auth.AuthManager
import okhttp3.Credentials
import okhttp3.Interceptor
import okhttp3.Response
import java.util.Base64

class BasicAuthInterceptor(
    private val username: String,
    private val password: String
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val credentials = Credentials.basic(username, password)

        println("🔐 Enviando Authorization: $credentials")

        val request = chain.request().newBuilder()
            .header("Authorization", credentials)
            .build()

        return chain.proceed(request)
    }

}


