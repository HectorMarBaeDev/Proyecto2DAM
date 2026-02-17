package com.pokemon.pokemonbackend.dto;

public class PokemonListItemDTO {

    private int pokedexNumber;
    private String name;
    private String primaryType;
    private String secondaryType;

    public PokemonListItemDTO(
            int pokedexNumber,
            String name,
            String primaryType,
            String secondaryType
    ) {
        this.pokedexNumber = pokedexNumber;
        this.name = name;
        this.primaryType = primaryType;
        this.secondaryType = secondaryType;
    }

    public int getPokedexNumber() {
        return pokedexNumber;
    }

    public String getName() {
        return name;
    }

    public String getPrimaryType() {
        return primaryType;
    }

    public String getSecondaryType() {
        return secondaryType;
    }
}
