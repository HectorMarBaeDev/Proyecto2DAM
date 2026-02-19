package com.example.pokemonapp.model

data class AuthResponseDto(
    val token: String,
    val userId: Long,
    val username: String
)