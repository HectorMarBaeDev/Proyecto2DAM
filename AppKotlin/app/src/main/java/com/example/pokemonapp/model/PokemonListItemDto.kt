package com.example.pokemonapp.model

data class PokemonListItemDto(
    val pokedexNumber: Int,
    val name: String,
    val primaryType: String,
    val secondaryType: String?
)
