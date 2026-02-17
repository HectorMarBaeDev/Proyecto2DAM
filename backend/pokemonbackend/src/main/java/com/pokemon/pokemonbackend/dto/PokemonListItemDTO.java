package com.pokemon.pokemonbackend.dto;

public class PokemonListItemDTO {

    private int pokedexNumber;
    private String name;

    public PokemonListItemDTO(int pokedexNumber, String name) {
        this.pokedexNumber = pokedexNumber;
        this.name = name;
    }

    public int getPokedexNumber() {
        return pokedexNumber;
    }

    public String getName() {
        return name;
    }
}
