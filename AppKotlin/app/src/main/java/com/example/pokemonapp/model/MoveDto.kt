package com.example.pokemonapp.model

data class MoveDto(
    val name: String,
    val type: String,
    val category: String,   // physical | special | status
    val power: Int?         // null si es status
)
