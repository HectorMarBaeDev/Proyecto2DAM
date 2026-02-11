package com.example.pokemonapp.data.model

data class AuthResponseDto(
    val token: String,
    val userId: Long,
    val username: String
)