package com.example.pokemonapp.data.model

data class PokemonListItemDto(
    val pokedexNumber: Int,
    val name: String,
    val primaryType: String,
    val secondaryType: String?
)
