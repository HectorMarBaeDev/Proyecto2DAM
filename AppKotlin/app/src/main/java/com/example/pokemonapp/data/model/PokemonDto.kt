package com.example.pokemonapp.data.model

data class PokemonDto(
    val id: Long,
    val pokedexNumber: Int,
    val name: String,
    val primaryType: String,
    val secondaryType: String?
)